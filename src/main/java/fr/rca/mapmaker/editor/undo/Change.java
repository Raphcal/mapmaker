package fr.rca.mapmaker.editor.undo;

import java.awt.Rectangle;

import fr.rca.mapmaker.model.map.TileLayer;

public class Change {

	private final int layerIndex;
	private final TileLayer layer;
	private final int[] tiles;
	private final Rectangle rectangle;
	
	public Change(int index, TileLayer layer, int[] tiles, Rectangle rectangle) {
		this.layerIndex = index;
		this.layer = layer;
		this.tiles = tiles.clone();
		this.rectangle = rectangle;
	}
	
	public int getLayerIndex() {
		return layerIndex;
	}
	
	public TileLayer getLayer() {
		return layer;
	}
	
	public int[] getTiles() {
		return tiles;
	}
	
	public Rectangle getRectangle() {
		return rectangle;
	}
}
