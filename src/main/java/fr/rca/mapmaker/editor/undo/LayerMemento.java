package fr.rca.mapmaker.editor.undo;

import java.awt.Rectangle;
import java.util.ArrayDeque;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.LayerChangeListener;
import java.util.Arrays;
import java.util.List;

public class LayerMemento {

	private int[][] states;
	private final ArrayDeque<Change> undoStack = new ArrayDeque<Change>();
	private final ArrayDeque<Change> redoStack = new ArrayDeque<Change>();
	
	private boolean ignoreNextChange;
	
	public LayerMemento() {
	}
	
	public LayerMemento(final TileLayer... layers) {
		setLayers(Arrays.asList(layers));
	}
	
	public final void setLayers(final List<TileLayer> layers) {
		
		states = new int[layers.size()][];
		
		for(int i = 0; i < layers.size(); i++) {
			
			final int index = i;
			final TileLayer layer = layers.get(index);
			
			states[index] = layer.copyData();
			
			layer.addLayerChangeListener(new LayerChangeListener() {
				
				@Override
				public void layerChanged(TileLayer layer, Rectangle dirtyRectangle) {
					
					if(!ignoreNextChange) {
						redoStack.clear();
						undoStack.push(new Change(index, layer, states[index], dirtyRectangle));
						
					} else
						ignoreNextChange = false;
					
					states[index] = layer.copyData();
				}
			});
		}
	}
	
	private void restore(ArrayDeque<Change> source, ArrayDeque<Change> destination) {
		
		if(!source.isEmpty()) {
			
			final Change lastChange = source.pop();
			final TileLayer layer = lastChange.getLayer();
			
			destination.push(new Change(lastChange.getLayerIndex(), layer,
					states[lastChange.getLayerIndex()], lastChange.getRectangle()));
			
			ignoreNextChange = true;
			layer.restoreData(lastChange.getTiles(), lastChange.getRectangle());
		}
	}
	
	public void undo() {
		
		restore(undoStack, redoStack);
	}
	
	public void redo() {
		
		restore(redoStack, undoStack);
	}
}
