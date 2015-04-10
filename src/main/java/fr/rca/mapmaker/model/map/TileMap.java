package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.LayerChangeListener;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.awt.Color;
import java.awt.Dimension;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileMap implements HasSizeChangeListeners, ListModel {
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	/**
	 * Largeur de la grille.
	 */
	private int width;
	/**
	 * Hauteur de la grille.
	 */
	private int height;
	/**
	 * Palette utilisée pour afficher la grille.
	 */
	private Palette palette;
	/**
	 * Project contenant la carte.
	 */
	private Project parent;
	/**
	 * Liste des couches de la grille.
	 */
	private final ArrayList<Layer> layers = new ArrayList<Layer>();
	/**
	 * Couleur de fond.
	 */
	private Color backgroundColor;

	private final SizeChangeListener sizeChangeListener;
	private final ArrayList<LayerChangeListener> layerChangeListeners = new ArrayList<LayerChangeListener>();
	private final ArrayList<SizeChangeListener> sizeChangeListeners = new ArrayList<SizeChangeListener>();
	private final ArrayList<ListDataListener> listDataListeners = new ArrayList<ListDataListener>();

	public TileMap() {
		sizeChangeListener = new SizeChangeListener() {
			@Override
			public void sizeChanged(Object source, Dimension oldSize, Dimension newSize) {
				updateSize();
			}
		};
	}

	public TileMap(int width, int height, Color backgroundColor) {
		this();
		this.width = width;
		this.height = height;
		this.backgroundColor = backgroundColor;
	}
	
	public TileMap(Layer layer, Palette palette) {
		this();
		this.width = layer.getWidth();
		this.height = layer.getHeight();
		this.layers.add(layer);
		this.palette = palette;
	}

	public void setParent(Project parent) {
		if(palette instanceof PaletteReference) {
			((PaletteReference)palette).setProject(parent);
		}
		
		this.parent = parent;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		final int oldWidth = this.width;
		this.width = width;
		
		propertyChangeSupport.firePropertyChange("width", oldWidth, width);
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		final int oldHeight = this.height;
		this.height = height;
		
		propertyChangeSupport.firePropertyChange("height", oldHeight, height);
	}
	
	public Palette getPalette() {
		return palette;
	}

	public void setPalette(Palette palette) {
		if(parent != null && palette instanceof PaletteReference) {
			((PaletteReference)palette).setProject(parent);
		}
		
		final Integer oldTileSize;
		if(this.palette != null) {
			oldTileSize = this.palette.getTileSize();
		} else {
			oldTileSize = null;
		}
		
		this.palette = palette;
		
		if(oldTileSize != null && oldTileSize != palette.getTileSize()) {
			final Dimension dimension = new Dimension(width, height);
			fireSizeChanged(dimension, dimension);
		}
	}
	
	public void refresh() {
	}

	public List<Layer> getLayers() {
		return layers;
	}
	
	public int getLayerIndex(Layer layer) {
		return layers.indexOf(layer);
	}

	public Color getBackgroundColor() {
		return backgroundColor;
	}

	public void setBackgroundColor(Color backgroundColor) {
		final Color oldBackgroundColor = this.backgroundColor;
		this.backgroundColor = backgroundColor;
		
		propertyChangeSupport.firePropertyChange("backgroundColor", oldBackgroundColor, backgroundColor);
	}

	private void updateSizeForLayer(Layer layer) {
		final int layerWidth = (int) (layer.getWidth() / Math.max(layer.getScrollRate(), 1.0f));
		final int layerHeight = (int) (layer.getHeight() / Math.max(layer.getScrollRate(), 1.0f));

		if(layerWidth > width) {
			setWidth(layerWidth);
		}

		if(layerHeight > height) {
			setHeight(layerHeight);
		}
	}
	
	private void updateSize() {
		if(layers.size() > 0) {
			final Dimension oldDimension = new Dimension(width, height);

			width = 0;
			height = 0;

			for(final Layer layer : layers) {
				updateSizeForLayer(layer);
			}

			final Dimension newDimension = new Dimension(width, height);

			if(!oldDimension.equals(newDimension)) {
				fireSizeChanged(oldDimension, newDimension);
			}
		}
	}
	
	public void add(Layer layer) {
		final int index = layers.size();
		
		layers.add(layer);
		
		updateSizeForLayer(layer);

		if(layer instanceof TileLayer) {
//			((TileLayer)layer).setParent(this);
			
			for(final LayerChangeListener listener : layerChangeListeners) {
				((TileLayer)layer).addLayerChangeListener(listener);
			}
		}
		
		if(layer instanceof HasSizeChangeListeners) {
			((HasSizeChangeListeners)layer).addSizeChangeListener(sizeChangeListener);
		}
		
		fireIntervalAdded(index);
	}
	
	public Layer remove(Layer layer) {
		return remove(layers.indexOf(layer));
	}
	
	public Layer remove(int index) {
		final Layer layer;
		
		if(index >= 0 && index < layers.size()) {
			layer = layers.remove(index);
			
			if(layer instanceof TileLayer) {
//				((TileLayer)layer).setParent(null);

				for(final LayerChangeListener listener : layerChangeListeners) {
					((TileLayer)layer).removeLayerChangeListener(listener);
				}
			}

			if(layer instanceof HasSizeChangeListeners) {
				((HasSizeChangeListeners)layer).removeSizeChangeListener(sizeChangeListener);
			}
			
			updateSize();
			fireIntervalRemoved(index);
			
		} else {
			layer = null;
		}
		
		return layer;
	}
	
	public void addAll(Collection<Layer> layers) {
		final Iterator<Layer> iterator = layers.iterator();
		while(iterator.hasNext()) {
			add(iterator.next());
		}
	}
	
	public void swapLayers(int first, int second) {
		final Layer firstLayer = layers.get(first);
		final Layer secondLayer = layers.get(second);
		
		layers.set(second, firstLayer);
		layers.set(first, secondLayer);
		
		fireContentChanged(Math.min(first, second), Math.max(first, second));
	}
	
	public void addLayerChangeListener(LayerChangeListener listener) {
		for(Layer layer : layers) {
			if(layer instanceof TileLayer) {
				((TileLayer)layer).addLayerChangeListener(listener);
			}
		}
			
		layerChangeListeners.add(listener);
	}
	
	public void removeLayerChangeListener(LayerChangeListener listener) {
		layerChangeListeners.remove(listener);
		
		for(Layer layer : layers) {
			if(layer instanceof TileLayer) {
				((TileLayer)layer).removeLayerChangeListener(listener);
			}
		}
	}
	
	@Override
	public void addSizeChangeListener(SizeChangeListener listener) {
		sizeChangeListeners.add(listener);
	}
	
	@Override
	public void removeSizeChangeListener(SizeChangeListener listener) {
		sizeChangeListeners.remove(listener);
	}
	
	public void clear() {
		final Iterator<Layer> iterator = layers.iterator();
		
		while(iterator.hasNext()) {
			final Layer layer = iterator.next();
			
			if(layer instanceof TileLayer) {
				for(final LayerChangeListener listener : layerChangeListeners) {
					((TileLayer)layer).removeLayerChangeListener(listener);
				}
			}
			
			if(layer instanceof HasSizeChangeListeners) {
				((HasSizeChangeListeners)layer).removeSizeChangeListener(sizeChangeListener);
			}
			
			iterator.remove();
		}
	}
	
	protected void fireSizeChanged(Dimension oldSize, Dimension newSize) {
		for(final SizeChangeListener listener : sizeChangeListeners) {
			listener.sizeChanged(this, oldSize, newSize);
		}
	}

	protected void fireIntervalAdded(int index) {
		
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index);
		
		for(final ListDataListener listener : listDataListeners) {
			listener.intervalAdded(event);
		}
	}
	
	protected void fireIntervalRemoved(int index) {
		
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);
		
		for(final ListDataListener listener : listDataListeners) {
			listener.intervalRemoved(event);
		}
	}
	
	protected void fireContentChanged(int from, int to) {
		
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, from, to);
		
		for(final ListDataListener listener : listDataListeners) {
			listener.contentsChanged(event);
		}
	}
	
	@Override
	public int getSize() {
		return layers.size();
	}

	@Override
	public Layer getElementAt(int index) {
		return layers.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listDataListeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listDataListeners.remove(l);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
}
