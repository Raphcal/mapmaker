package fr.rca.mapmaker.model.project;

import fr.rca.mapmaker.model.palette.PaletteComboBoxModel;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.map.MapAndInstances;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.HasColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.palette.SpritePalette;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.ListModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Project implements ListModel, HasColorPalette {
	
	public static final String CURRENT_MAP = "currentMap";
	public static final String CURRENT_PALETTE_MAP = "currentPaletteMap";
	public static final String CURRENT_SPRITE_PALETTE_MAP = "currentSpritePaletteMap";
	public static final String CURRENT_LAYER_MODEL = "currentLayerModel";
	public static final String CURRENT_SELECTED = "selected";
	public static final String CURRENT_INSTANCES = "instances";
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private int selectedIndex;
	
    /**
     * Palette de couleur principale du projet.
     */
    private ColorPalette colorPalette = AlphaColorPalette.getDefaultColorPalette();
    
    /**
     * Liste de toutes les cartes.
     */
	private final List<MapAndInstances> maps = new ArrayList<>();
    
    /**
     * Liste des sprites.
     */
	private final List<Sprite> sprites = new ArrayList<>();
    
    /**
     * Liste de toutes les palettes.
     */
	private final List<Palette> palettes = new ArrayList<>();
	
	private final List<ListDataListener> listeners = new ArrayList<>();
	
	private PaletteMap spritePaletteMap;

	private int nextMap;
	
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
		final List<Instance> oldInstances = getInstances();
		
		// Nettoyage
		final int oldSize = getSize();
		maps.clear();
		palettes.clear();
		sprites.clear();
		
		// Palettes
		palettes.addAll(project.getPalettes());
		
		// Cartes
		maps.addAll(project.maps);
		
		// Sprites
		sprites.addAll(project.getSprites());
		
		for(final List<Instance> instances : project.getAllInstances()) {
			for(final Instance instance : instances) {
				instance.setProject(this);
			}
		}
		
		final int newSize = getSize();
		
		if(newSize < oldSize) {
			fireContentsChanged(0, newSize -1);
			fireIntervalRemoved(newSize, oldSize -1);
			
		} else if(newSize > oldSize) {
			fireContentsChanged(0, oldSize -1);
			fireIntervalAdded(oldSize, newSize -1);
			
		} else {
			fireContentsChanged(0, newSize -1);
		}
		
		// Séquences
		nextMap = project.nextMap;
		
		// Sélection + événement de modification
		selectedIndex = 0;
		propertyChangeSupport.firePropertyChange(CURRENT_MAP, oldMap, getCurrentMap());
		propertyChangeSupport.firePropertyChange(CURRENT_PALETTE_MAP, oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_LAYER_MODEL, oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_SPRITE_PALETTE_MAP, oldSpritePaletteMap, getCurrentSpritePaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_SELECTED, oldSelected, isSelected());
		propertyChangeSupport.firePropertyChange(CURRENT_INSTANCES, oldInstances, getInstances());
	}
	
	public void setSelectedIndex(int selectedIndex) {
		final TileMap oldMap = getCurrentMap();
		final TileMap oldPaletteMap = getCurrentPaletteMap();
		final boolean oldSelected = isSelected();
		final List<Instance> oldInstances = getInstances();
		
		this.selectedIndex = selectedIndex;
		
		propertyChangeSupport.firePropertyChange(CURRENT_MAP, oldMap, getCurrentMap());
		propertyChangeSupport.firePropertyChange(CURRENT_PALETTE_MAP, oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_LAYER_MODEL, oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange(CURRENT_SELECTED, oldSelected, isSelected());
		propertyChangeSupport.firePropertyChange(CURRENT_INSTANCES, oldInstances, getInstances());
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
		if(selectedIndex < 0 || maps == null || maps.isEmpty() || selectedIndex >= maps.size()) {
			return null;
		}
		
		return maps.get(selectedIndex).getTileMap();
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
	
	public List<MapAndInstances> getMaps() {
		return maps;
	}

    @Override
    public ColorPalette getColorPalette() {
        return colorPalette;
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

	/**
	 * Récupère les instances de la carte actuellement sélectionnée.
	 * 
	 * @return La liste des instances de la carte sélectionnée ou une liste vide
	 * si aucune carte n'est sélectionnée.
	 */
	public List<Instance> getInstances() {
		if(selectedIndex >= 0 && selectedIndex < maps.size()) {
			return maps.get(selectedIndex).getSpriteInstances();
		} else {
			return Collections.<Instance>emptyList();
		}
	}

	public List<List<Instance>> getAllInstances() {
        final ArrayList<List<Instance>> allInstances = new ArrayList<>();
        for (final MapAndInstances map : maps) {
            allInstances.add(map.getSpriteInstances());
        }
		return allInstances;
	}
	
	@Override
	public TileMap getElementAt(int index) {
		return maps.get(index).getTileMap();
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	public int getNextMap() {
		return nextMap;
	}

	public void setNextMap(int nextMap) {
		this.nextMap = nextMap;
	}
	
	public void addMap(TileMap map) {
		addMap(map, new ArrayList<Instance>());
	}
	
	public void addMap(TileMap map, List<Instance> instances) {
		map.setParent(this);
		
		if(map.getIndex() == null) {
			map.setIndex(nextMap++);
		} else if(nextMap <= map.getIndex()) {
			nextMap = map.getIndex() + 1;
		}
		
		final int index = maps.size();
		maps.add(new MapAndInstances(map, instances));
		
		fireIntervalAdded(index, index);
	}
	
	public void removeMap(int index) {
		if(index >= 0 && index < maps.size()) {
			maps.remove(index);
			fireIntervalRemoved(index, index);
		}
	}
	
	public void swapMaps(int first, int second) {
		final MapAndInstances firstMap = maps.get(first);
		final MapAndInstances secondMap = maps.get(second);
		
		maps.set(second, firstMap);
		maps.set(first, secondMap);
		
		fireContentsChanged(Math.min(first, second), Math.max(first, second));
	}
	
	protected void fireIntervalAdded(int from, int to) {
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, from, to);
		
		for(final ListDataListener listener : listeners) {
			listener.intervalAdded(event);
		}
	}
	
	protected void fireIntervalRemoved(int from, int to) {
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, from, to);
		
		for(final ListDataListener listener : listeners) {
			listener.intervalRemoved(event);
		}
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
