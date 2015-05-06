package fr.rca.mapmaker.editor.undo;

import java.awt.Rectangle;
import java.util.ArrayDeque;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.LayerChangeListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.Arrays;
import java.util.Deque;
import java.util.List;

public class LayerMemento {

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private int[][] states;
	private final Deque<Change> undoStack = new ArrayDeque<Change>();
	private final Deque<Change> redoStack = new ArrayDeque<Change>();
	
	private boolean ignoreNextChange;
	private boolean transactionActive;
	
	private Change[] transactionObjects;
	
	public LayerMemento() {
	}
	
	public LayerMemento(final TileLayer... layers) {
		setLayers(Arrays.asList(layers));
	}
	
	public final void setLayers(final List<TileLayer> layers) {
		states = new int[layers.size()][];
		transactionObjects = new Change[layers.size()];
		
		clear();
		
		for(int i = 0; i < layers.size(); i++) {
			final int index = i;
			final TileLayer layer = layers.get(index);
			
			states[index] = layer.copyData();
			
			layer.addLayerChangeListener(new LayerChangeListener() {
				
				@Override
				public void layerChanged(TileLayer layer, Rectangle dirtyRectangle) {
					if(transactionActive) {
						final Change change = transactionObjects[index];
						if(change != null) {
							final Rectangle currentDirty = change.getRectangle();
							final int x1 = Math.min(dirtyRectangle.x, currentDirty.x);
							final int y1 = Math.min(dirtyRectangle.y, currentDirty.y);
							final int x2 = Math.max(dirtyRectangle.x + dirtyRectangle.width, currentDirty.x + currentDirty.width);
							final int y2 = Math.max(dirtyRectangle.y + dirtyRectangle.height, currentDirty.y + currentDirty.height);
							
							change.getRectangle().x = x1;
							change.getRectangle().y = y1;
							change.getRectangle().width = x2 - x1;
							change.getRectangle().height = y2 - y1;
							
						} else {
							transactionObjects[index] = new Change(index, layer, states[index], dirtyRectangle);
						}
						
					} else {
						if(!ignoreNextChange) {
							clearRedoStack();
							pushOnUndoStack(new Change(index, layer, states[index], dirtyRectangle));

						} else {
							ignoreNextChange = false;
						}

						states[index] = layer.copyData();
					}
				}
			});
		}
	}
	
	public void undo() {
		restore(undoStack, redoStack);
	}
	
	public void redo() {
		restore(redoStack, undoStack);
	}
	
	public boolean isUndoable() {
		return !undoStack.isEmpty();
	}
	
	public boolean isRedoable() {
		return !redoStack.isEmpty();
	}
	
	public void clear() {
		final boolean oldUndoable = isUndoable();
		final boolean oldRedoable = isRedoable();
		
		undoStack.clear();
		redoStack.clear();
		
		propertyChangeSupport.firePropertyChange("undoable", oldUndoable, false);
		propertyChangeSupport.firePropertyChange("redoable", oldRedoable, false);
	}
	
	public void begin() {
		transactionActive = true;
	}
	
	public void end() {
		transactionActive = false;
		
		boolean changed = false;
		
		for(int index = 0; index < transactionObjects.length; index++) {
			if(transactionObjects[index] != null) {
				pushOnUndoStack(transactionObjects[index]);
				states[index] = transactionObjects[index].getLayer().copyData();
				
				transactionObjects[index] = null;
				changed = true;
			}
		}
		
		if(changed) {
			clearRedoStack();
		}
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
	
	private void restore(Deque<Change> source, Deque<Change> destination) {
		final boolean oldUndoable = isUndoable();
		final boolean oldRedoable = isRedoable();
		
		if(!source.isEmpty()) {
			final Change lastChange = source.pop();
			final TileLayer layer = lastChange.getLayer();
			
			destination.push(new Change(lastChange.getLayerIndex(), layer,
					states[lastChange.getLayerIndex()], lastChange.getRectangle()));
			
			ignoreNextChange = true;
			layer.restoreData(lastChange.getTiles(), lastChange.getRectangle());
		}
		
		final boolean undoable = isUndoable();
		if(oldUndoable != undoable) {
			propertyChangeSupport.firePropertyChange("undoable", oldUndoable, undoable);
		}
		final boolean redoable = isRedoable();
		if(oldRedoable != redoable) {
			propertyChangeSupport.firePropertyChange("redoable", oldRedoable, redoable);
		}
	}
	
	private void clearRedoStack() {
		final boolean oldRedoable = isRedoable();
		
		redoStack.clear();
		
		final boolean redoable = isRedoable();
		if(oldRedoable != redoable) {
			propertyChangeSupport.firePropertyChange("redoable", oldRedoable, redoable);
		}
	}
	
	private void pushOnUndoStack(Change change) {
		final boolean oldUndoable = isUndoable();
		
		undoStack.push(change);
		
		final boolean undoable = isUndoable();
		if(oldUndoable != undoable) {
			propertyChangeSupport.firePropertyChange("undoable", oldUndoable, undoable);
		}
	}
}
