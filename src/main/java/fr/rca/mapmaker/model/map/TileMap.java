package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.HasPropertyChangeListeners;
import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.LayerChangeListener;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.HasColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
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
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * Carte composée de couches de tuiles.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Getter
public class TileMap implements HasSizeChangeListeners, HasPropertyChangeListeners, ListModel, HasColorPalette {

	/**
	 * Gestion des événements de changement des propriétés.
	 */
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	/**
	 * Numéro de la carte.
	 */
	private Integer index;

	/**
	 * Nom de la carte.
	 */
	private String name;

	/**
	 * Nom de la carte après export. REM: Inutilisé.
	 */
	private String fileName;

	/**
	 * Largeur de la grille.
	 */
	private int width;

	/**
	 * Hauteur de la grille.
	 */
	private int height;

	/**
	 * Palette de couleur utilisée pour afficher la palette de la grille.
	 */
	private ColorPalette colorPalette;

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
	private final ArrayList<Layer> layers = new ArrayList<>();

	@Setter
	private List<Instance> spriteInstances;

	/**
	 * Couleur de fond.
	 */
	private Color backgroundColor;

	private final SizeChangeListener sizeChangeListener;
	private final ArrayList<LayerChangeListener> layerChangeListeners = new ArrayList<>();
	private final ArrayList<SizeChangeListener> sizeChangeListeners = new ArrayList<>();
	private final ArrayList<ListDataListener> listDataListeners = new ArrayList<>();

	public TileMap() {
		sizeChangeListener = (source, oldSize, newSize) -> updateSize();
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
		if (palette instanceof PaletteReference) {
			((PaletteReference) palette).setProject(parent);
		}

		this.parent = parent;
	}

	public void setIndex(Integer index) {
		final Integer oldIndex = this.index;
		this.index = index;

		propertyChangeSupport.firePropertyChange("index", oldIndex, index);
	}

	public @Nullable
	String getName() {
		return name;
	}

	public void setName(String name) {
		final String oldName = this.name;
		this.name = name;

		propertyChangeSupport.firePropertyChange("name", oldName, name);
	}

	public void setFileName(String fileName) {
		final String oldFileName = this.fileName;
		this.fileName = fileName;

		propertyChangeSupport.firePropertyChange("fileName", oldFileName, fileName);
	}

	public void setWidth(int width) {
		final int oldWidth = this.width;
		this.width = width;

		propertyChangeSupport.firePropertyChange("width", oldWidth, width);
	}

	public void setHeight(int height) {
		final int oldHeight = this.height;
		this.height = height;

		propertyChangeSupport.firePropertyChange("height", oldHeight, height);
	}

	@Override
	public ColorPalette getColorPalette() {
		return colorPalette != null ? colorPalette : parent.getColorPalette();
	}

	public void setColorPalette(ColorPalette colorPalette) {
		this.colorPalette = colorPalette;
	}

	public void setPalette(Palette palette) {
		if (parent != null && palette instanceof PaletteReference) {
			((PaletteReference) palette).setProject(parent);
		}

		final Integer oldTileSize;
		if (this.palette != null) {
			oldTileSize = this.palette.getTileSize();
		} else {
			oldTileSize = null;
		}

		this.palette = palette;

		if (oldTileSize != null && oldTileSize != palette.getTileSize()) {
			final Dimension dimension = new Dimension(width, height);
			fireSizeChanged(dimension, dimension);
		}
	}

	public void refresh() {
		// Vide mais les sous-classes l'implémentent.
	}

	public int getLayerIndex(Layer layer) {
		return layers.indexOf(layer);
	}

	public void setBackgroundColor(Color backgroundColor) {
		final Color oldBackgroundColor = this.backgroundColor;
		this.backgroundColor = backgroundColor;

		propertyChangeSupport.firePropertyChange("backgroundColor", oldBackgroundColor, backgroundColor);
	}

	private void updateSizeForLayer(Layer layer) {
		final int layerWidth = (int) (layer.getWidth() / Math.max(layer.getScrollRate().getX(), 1.0f));
		final int layerHeight = (int) (layer.getHeight() / Math.max(layer.getScrollRate().getY(), 1.0f));

		if (layerWidth > width) {
			setWidth(layerWidth);
		}

		if (layerHeight > height) {
			setHeight(layerHeight);
		}
	}

	private void updateSize() {
		if (!layers.isEmpty()) {
			final Dimension oldDimension = new Dimension(width, height);

			width = 0;
			height = 0;

			for (final Layer layer : layers) {
				updateSizeForLayer(layer);
			}

			final Dimension newDimension = new Dimension(width, height);

			if (!oldDimension.equals(newDimension)) {
				fireSizeChanged(oldDimension, newDimension);
			}
		}
	}

	public void add(Layer layer) {
		final int index = layers.size();

		layers.add(layer);

		updateSizeForLayer(layer);

		if (layer instanceof TileLayer) {
			final TileLayer tileLayer = (TileLayer)layer;
			tileLayer.setParent(this);

			for (final LayerChangeListener listener : layerChangeListeners) {
				tileLayer.addLayerChangeListener(listener);
			}
		}

		if (layer instanceof HasSizeChangeListeners) {
			((HasSizeChangeListeners) layer).addSizeChangeListener(sizeChangeListener);
		}

		fireIntervalAdded(index);
	}

	public Layer remove(Layer layer) {
		return remove(layers.indexOf(layer));
	}

	public Layer remove(int index) {
		final Layer layer;

		if (index >= 0 && index < layers.size()) {
			layer = layers.remove(index);

			if (layer instanceof TileLayer) {
				final TileLayer tileLayer = (TileLayer)layer;
				tileLayer.setParent(null);

				for (final LayerChangeListener listener : layerChangeListeners) {
					tileLayer.removeLayerChangeListener(listener);
				}
			}

			if (layer instanceof HasSizeChangeListeners) {
				((HasSizeChangeListeners) layer).removeSizeChangeListener(sizeChangeListener);
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
		while (iterator.hasNext()) {
			add(iterator.next());
		}
	}

	public void setLayerAtIndex(int index, Layer layer) {
		if (index >= 0 && index < layers.size()) {
			final Layer oldLayer = layers.get(index);
			layers.set(index, layer);
			if (oldLayer instanceof TileLayer) {
				final TileLayer tileLayer = (TileLayer)oldLayer;
				tileLayer.setParent(null);

				for (final LayerChangeListener listener : layerChangeListeners) {
					tileLayer.removeLayerChangeListener(listener);
				}
			}
			if (layer instanceof TileLayer) {
				final TileLayer tileLayer = (TileLayer)layer;
				tileLayer.setParent(this);

				for (final LayerChangeListener listener : layerChangeListeners) {
					tileLayer.addLayerChangeListener(listener);
				}
			}
			updateSizeForLayer(layer);
			fireContentChanged(index, index);
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
		for (Layer layer : layers) {
			if (layer instanceof TileLayer) {
				((TileLayer) layer).addLayerChangeListener(listener);
			}
		}

		layerChangeListeners.add(listener);
	}

	public void removeLayerChangeListener(LayerChangeListener listener) {
		layerChangeListeners.remove(listener);

		for (Layer layer : layers) {
			if (layer instanceof TileLayer) {
				((TileLayer) layer).removeLayerChangeListener(listener);
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

		while (iterator.hasNext()) {
			final Layer layer = iterator.next();

			if (layer instanceof TileLayer) {
				for (final LayerChangeListener listener : layerChangeListeners) {
					((TileLayer) layer).removeLayerChangeListener(listener);
				}
			}

			if (layer instanceof HasSizeChangeListeners) {
				((HasSizeChangeListeners) layer).removeSizeChangeListener(sizeChangeListener);
			}

			iterator.remove();
		}
	}

	protected void fireSizeChanged(Dimension oldSize, Dimension newSize) {
		for (final SizeChangeListener listener : sizeChangeListeners) {
			listener.sizeChanged(this, oldSize, newSize);
		}
	}

	protected void fireIntervalAdded(int index) {

		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, index, index);

		for (final ListDataListener listener : listDataListeners) {
			listener.intervalAdded(event);
		}
	}

	protected void fireIntervalRemoved(int index) {

		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, index, index);

		for (final ListDataListener listener : listDataListeners) {
			listener.intervalRemoved(event);
		}
	}

	protected void fireContentChanged(int from, int to) {

		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, from, to);

		for (final ListDataListener listener : listDataListeners) {
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

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

}
