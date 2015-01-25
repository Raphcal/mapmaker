package fr.rca.mapmaker.model.project;

import fr.rca.mapmaker.model.PaletteComboBoxModel;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
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
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private int selectedIndex;
	
	private ArrayList<TileMap> maps = new ArrayList<TileMap>();
	private ArrayList<Palette> palettes = new ArrayList<Palette>();
	
	private ArrayList<ListDataListener> listeners = new ArrayList<ListDataListener>();

	
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
		final boolean oldSelected = isSelected();
		
		// Nettoyage
		final int oldSize = getSize();
		maps.clear();
		palettes.clear();
		
		// Palettes
		palettes.addAll(project.getPalettes());
		
		// Cartes
		maps.addAll(project.getMaps());
		
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
		propertyChangeSupport.firePropertyChange("currentMap", oldMap, getCurrentMap());
		propertyChangeSupport.firePropertyChange("currentPaletteMap", oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange("currentLayerModel", oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange("selected", oldSelected, isSelected());
	}
	
	public void setSelectedIndex(int selectedIndex) {
		final TileMap oldMap = getCurrentMap();
		final TileMap oldPaletteMap = getCurrentPaletteMap();
		final boolean oldSelected = isSelected();
		
		this.selectedIndex = selectedIndex;
		
		propertyChangeSupport.firePropertyChange("currentMap", oldMap, getCurrentMap());
		propertyChangeSupport.firePropertyChange("currentPaletteMap", oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange("currentLayerModel", oldPaletteMap, getCurrentPaletteMap());
		propertyChangeSupport.firePropertyChange("selected", oldSelected, isSelected());
	}
	
	public void currentPaletteChanged() {
		propertyChangeSupport.firePropertyChange("currentPaletteMap", null, getCurrentPaletteMap());
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
		
		if(currentMap == null || currentMap.getPalette() == null)
			return null;
		else
			return new PaletteMap(currentMap.getPalette(), 4);
	}
	
	public TileMap getCurrentLayerModel() {
		final TileMap currentMap = getCurrentMap();
		
		if(currentMap == null)
			return new TileMap();
		else
			return currentMap;
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
		
		for(final ListDataListener listener : listeners)
			listener.contentsChanged(event);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
}
