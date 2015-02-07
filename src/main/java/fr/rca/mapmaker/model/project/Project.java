package fr.rca.mapmaker.model.project;

import fr.rca.mapmaker.model.PaletteComboBoxModel;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.palette.SpritePalette;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Project implements ListModel {
	
	public static final String CURRENT_MAP = "currentMap";
	public static final String CURRENT_PALETTE_MAP = "currentPaletteMap";
	public static final String CURRENT_SPRITE_PALETTE_MAP = "currentSpritePaletteMap";
	public static final String CURRENT_LAYER_MODEL = "currentLayerModel";
	public static final String CURRENT_SELECTED = "selected";
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private int selectedIndex;
	
	private ArrayList<TileMap> maps = new ArrayList<TileMap>();
	private ArrayList<Sprite> sprites = new ArrayList<Sprite>();
	private ArrayList<Palette> palettes = new ArrayList<Palette>();
	
	private ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();
	
	private PaletteMap spritePaletteMap;

	
	public static Project createEmptyProject() {
		final Project project = new Project();
		
		final EditableImagePalette emptyPalette = new EditableImagePalette(32, 4);
		emptyPalette.setName("Palette 1");
		project.addPalette(emptyPalette);
		
		final TileLayer emptyTileLayer = new TileLayer(20, 15);
		emptyTileLayer.setName("Calque 1");
		
		final TileMap emptyTileMap = new TileMap();
		emptyTileMap.setParent(project);
		emptyTileMap.setPalette(new PaletteReference(project, 0));
		emptyTileMap.setBackgroundColor(Color.WHITE);
		emptyTileMap.add(emptyTileLayer);
		project.addMap(emptyTileMap);
		
		return project;
	}
	
	public void morphTo(Project project) {
		final TileMap oldMap = getCurrentMap();
		final TileMap oldPaletteMap = getCurrentPaletteMap();
		final TileMap oldSpritePaletteMap = getCurrentSpritePaletteMap();
		final boolean oldSelected = isSelected();
		
		// Nettoyage
		final int oldSize = getSize();
		maps.clear();
		palettes.clear();
		sprites.clear();
		
		// Palettes
		palettes.addAll(project.getPalettes());
		
		// Cartes
		maps.addAll(project.getMaps());
		
		// Sprites
		sprites.addAll(project.getSprites());
		
		final int newSize = getSize();
		
		if(newSize < oldSize) {
			fireContentsChanged(0, newSize -1);
			fireIntervalRemoved(newSize, oldSize -1);
			
		} else if(newSize > oldSize) {
			fireContentsChanged(0, oldSize -1);
			fireIntervalAdded(oldSize, newSize -1);
			
		} else
			fireContentsChanged(0, newSize -1);
		
		// Sélection + événement de modification
		selectedIndex = 0;
		propertyChangeSupport.firePropertyChange(CURRENT_MAP, oldMap, getCurrentMap());
		propertyChangeSupport.firePropertyChange(CURRENT_PALETTE_MAP, oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_LAYER_MODEL, oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_SPRITE_PALETTE_MAP, oldSpritePaletteMap, getCurrentSpritePaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_SELECTED, oldSelected, isSelected());
	}
	
	public void setSelectedIndex(int selectedIndex) {
		final TileMap oldMap = getCurrentMap();
		final TileMap oldPaletteMap = getCurrentPaletteMap();
		final boolean oldSelected = isSelected();
		
		this.selectedIndex = selectedIndex;
		
		propertyChangeSupport.firePropertyChange(CURRENT_MAP, oldMap, getCurrentMap());
		propertyChangeSupport.firePropertyChange(CURRENT_PALETTE_MAP, oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_LAYER_MODEL, oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_SELECTED, oldSelected, isSelected());
	}
	
	public void currentPaletteChanged() {
		propertyChangeSupport.firePropertyChange(CURRENT_PALETTE_MAP, null, getCurrentPaletteMap());
	}
	
	public int getSelectedIndex() {
		return selectedIndex;
	}
	
	public boolean isSelected() {
		return selectedIndex != -1;
	}
	
	public TileMap getCurrentMap() {
		if(selectedIndex < 0 || maps == null || maps.isEmpty() || selectedIndex >= maps.size())
			return null;
		
		return maps.get(selectedIndex);
	}
	
	public TileMap getCurrentPaletteMap() {
		final TileMap currentMap = getCurrentMap();
		
		if(currentMap == null || currentMap.getPalette() == null) {
			return null;
		} else {
			return new PaletteMap(currentMap.getPalette(), 4);
		}
	}
	
	public TileMap getCurrentLayerModel() {
		final TileMap currentMap = getCurrentMap();
		
		if(currentMap == null) {
			return new TileMap();
		} else {
			return currentMap;
		}
	}
	
	public TileMap getCurrentSpritePaletteMap() {
		if(spritePaletteMap == null) {
			spritePaletteMap = new PaletteMap(new SpritePalette(sprites), 4);
		}
		return spritePaletteMap;
	}
	
	public List<TileMap> getMaps() {
		return maps;
	}
	
	public Palette getPalette(int index) {
		return palettes.get(index);
	}

	public List<Palette> getPalettes() {
		return palettes;
	}
	
	public void addPalette(Palette palette) {
		palettes.add(palette);
	}
	
	public PaletteComboBoxModel getPaletteListModel() {
		return new PaletteComboBoxModel(palettes);
	}

	public List<Sprite> getSprites() {
		return sprites;
	}
	
	@Override
	public int getSize() {
		return maps.size();
	}

	@Override
	public TileMap getElementAt(int index) {
		return maps.get(index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}
	
	public void addMap(TileMap map) {
		map.setParent(this);
		
		final int index = maps.size();
		maps.add(map);
		
		fireIntervalAdded(index, index);
	}
	
	public void removeMap(int index) {
		if(index >= 0 && index < maps.size()) {
			maps.remove(index);
			fireIntervalRemoved(index, index);
		}
	}
	
	protected void fireIntervalAdded(int from, int to) {
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, from, to);
		
		for(final ListDataListener listener : listeners)
			listener.intervalAdded(event);
	}
	
	protected void fireIntervalRemoved(int from, int to) {
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, from, to);
		
		for(final ListDataListener listener : listeners)
			listener.intervalRemoved(event);
	}
	
	protected void fireContentsChanged(int from, int to) {
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, from, to);
		
		for(final ListDataListener listener : listeners) {
			listener.contentsChanged(event);
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
}
