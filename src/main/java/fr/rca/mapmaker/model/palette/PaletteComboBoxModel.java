package fr.rca.mapmaker.model.palette;

import java.util.ArrayList;
import java.util.List;
import javax.swing.ComboBoxModel;
import javax.swing.event.ListDataEvent;
import javax.swing.event.ListDataListener;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PaletteComboBoxModel implements ComboBoxModel {

	private int selectedIndex;
	private List<Palette> palettes;

	private final List<ListDataListener> listeners = new ArrayList<ListDataListener>();
	
	public PaletteComboBoxModel(List<Palette> palettes) {
		this.palettes = palettes;
	}

	@Override
	public int getSize() {
		return palettes.size();
	}

	@Override
	public Object getElementAt(int index) {
		return palettes.get(index);
	}
	
	public void renameElementAt(int index, String name) {
		
		final Palette palette = palettes.get(index);
		if(palette instanceof EditablePalette) {
			((EditablePalette)palette).setName(name);
			fireContentsChanged(index, index);
		}
	}
	
	public void removeElementAt(int index) {
		palettes.remove(index);
		fireIntervalRemoved(index, index);
	}
	
	public void addElement(Palette palette) {
		final int index = palettes.size();
		palettes.add(palette);
		fireIntervalAdded(index, index);
	}

	@Override
	public void addListDataListener(ListDataListener l) {
		listeners.add(l);
	}

	@Override
	public void removeListDataListener(ListDataListener l) {
		listeners.remove(l);
	}

	@Override
	public void setSelectedItem(Object anItem) {
		if(anItem instanceof PaletteReference) {
			selectedIndex = ((PaletteReference) anItem).getPaletteIndex();
			
		} else {
			selectedIndex = palettes.indexOf(anItem);
		}
	}

	@Override
	public Object getSelectedItem() {
		if(selectedIndex >= 0 && selectedIndex < palettes.size()) {
			return palettes.get(selectedIndex);
		} else {
			return null;
		}
	}
	
	protected void fireContentsChanged(int from, int to) {
		
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.CONTENTS_CHANGED, from, to);
		
		for(final ListDataListener listener : listeners) {
			listener.contentsChanged(event);
		}
	}
	
	protected void fireIntervalAdded(int from, int to) {
		
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_ADDED, from, to);
		
		for(final ListDataListener listener : listeners) {
			listener.intervalRemoved(event);
		}
	}
	
	protected void fireIntervalRemoved(int from, int to) {
		
		final ListDataEvent event = new ListDataEvent(this, ListDataEvent.INTERVAL_REMOVED, from, to);
		
		for(final ListDataListener listener : listeners) {
			listener.intervalRemoved(event);
		}
	}
}
