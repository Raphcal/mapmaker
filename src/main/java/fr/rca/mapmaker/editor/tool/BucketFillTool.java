package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;

public class BucketFillTool extends MouseAdapter implements Tool {

	private final static int ORIGIN_NONE = 0;
	private final static int ORIGIN_RIGHT = 1;
	private final static int ORIGIN_DOWN = 2;
	private final static int ORIGIN_LEFT = 3;
	private final static int ORIGIN_UP = 4;
	
	private final Grid grid;
	
	public BucketFillTool(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();
		final TileLayer previewLayer = grid.getOverlay();
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		if(point.x >=0 && point.x < drawingLayer.getWidth() &&
				point.y >= 0 && point.y < drawingLayer.getHeight()) {
		
			final int source = drawingLayer.getTile(point.x, point.y);
			final int target;

			if(e.getButton() == MouseEvent.BUTTON1) {
				final int selectedTile = grid.getTileMap().getPalette().getSelectedTile();
				if(selectedTile == -1)
					target = -2;
				else
					target = selectedTile;

			} else
				target = -2;

			paintRecursive(point.x, point.y, source, target, ORIGIN_NONE);

			drawingLayer.merge(previewLayer);
			previewLayer.clear();
		}
	}
	
	private void paintRecursive(int x, int y, int source, int target, int origin) {
		// TODO: Essayer de réduire le nombre de récursivités
		
		grid.getOverlay().setTile(x, y, target);
		
		if(origin != ORIGIN_LEFT && canPaint(x - 1, y, source))
			paintRecursive(x - 1, y, source, target, ORIGIN_RIGHT);
		
		if(origin != ORIGIN_UP && canPaint(x, y - 1, source))
			paintRecursive(x, y - 1, source, target, ORIGIN_DOWN);
		
		if(origin != ORIGIN_RIGHT && canPaint(x + 1, y, source))
			paintRecursive(x + 1, y, source, target, ORIGIN_LEFT);
		
		if(origin != ORIGIN_DOWN && canPaint(x, y + 1, source))
			paintRecursive(x, y + 1, source, target, ORIGIN_UP);
	}
	
	private boolean canPaint(int x, int y, int source) {
		
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();
		final TileLayer previewLayer = grid.getOverlay();
		
		return x >= 0 && x < drawingLayer.getWidth() &&
			   y >= 0 && y < drawingLayer.getHeight() &&
			   drawingLayer.getTile(x, y) == source &&
			   previewLayer.getTile(x, y) == -1;
	}
	
	@Override
	public void reset() {
	}
}
