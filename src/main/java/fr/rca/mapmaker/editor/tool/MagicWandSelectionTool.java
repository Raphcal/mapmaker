package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import java.awt.Point;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;

public class MagicWandSelectionTool extends AbstractSelectionTool {

	public MagicWandSelectionTool(Grid grid) {
		super(grid);
	}

	@Override
	protected void handleMouseClicked(MouseEvent e) {
		
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		final int tile = drawingLayer.getTile(point);
		
		expandSelectionRecursive(point.x, point.y, tile);
		
		drawingLayer.clear(grid.getOverlay());
		
		grid.setFocusVisible(true);
		selected = true;
	}
	
	private void expandSelectionRecursive(int x, int y, int tile) {
		
		grid.getOverlay().setTile(x, y, tile);
		
		if(canExpandSelection(x - 1, y, tile))
			expandSelectionRecursive(x - 1, y, tile);
		
		if(canExpandSelection(x, y - 1, tile))
			expandSelectionRecursive(x, y - 1, tile);
		
		if(canExpandSelection(x + 1, y, tile))
			expandSelectionRecursive(x + 1, y, tile);
		
		if(canExpandSelection(x, y + 1, tile))
			expandSelectionRecursive(x, y + 1, tile);
	}
	
	private boolean canExpandSelection(int x, int y, int tile) {
		
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();
		final TileLayer previewLayer = grid.getOverlay();
		
		return x >= 0 && x < drawingLayer.getWidth() &&
			   y >= 0 && y < drawingLayer.getHeight() &&
			   drawingLayer.getTile(x, y) == tile &&
			   previewLayer.getTile(x, y) == -1;
	}
}
