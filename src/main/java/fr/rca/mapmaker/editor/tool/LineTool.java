package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;

public class LineTool extends MouseAdapter implements Tool {

	private final Grid grid;
	private int target;
	
	private final TileLayer drawingLayer;
	private final TileLayer previewLayer;
	
	private Point startPoint;
	private Point lastDragPoint;
	
	public LineTool(Grid grid, TileLayer drawingLayer, TileLayer previewLayer) {
		this.grid = grid;
		this.drawingLayer = drawingLayer;
		this.previewLayer = previewLayer;
	}

	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = grid.getLayerLocation(e.getX(), e.getY());
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			final int selectedTile = grid.getTileMap().getPalette().getSelectedTile();
			if(selectedTile == -1) {
				target = -2;
			} else {
				target = selectedTile;
			}
			
		} else {
			target = -2;
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		startPoint = null;
		
		drawingLayer.merge(previewLayer);
		previewLayer.clear();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(startPoint == null) {
			return;
		}
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		if(lastDragPoint != null) {
			final Rectangle lastRectangle = new Rectangle(
					Math.min(startPoint.x, lastDragPoint.x),
					Math.min(startPoint.y, lastDragPoint.y),
					Math.abs(startPoint.x - lastDragPoint.x) + 1,
					Math.abs(startPoint.y - lastDragPoint.y) + 1);
			
			previewLayer.clear(lastRectangle);
		}
		
		if(point.x >= previewLayer.getWidth()) {
			point.x = previewLayer.getWidth() - 1;
		} else if(point.x < 0) {
			point.x = 0;
		}
		
		if(point.y >= previewLayer.getHeight()) {
			point.y = previewLayer.getHeight() - 1;
		} else if(point.y < 0) {
			point.y = 0;
		}
		
		previewLayer.setTiles(startPoint, point, target);
		lastDragPoint = point;
	}
	
	@Override
	public void setup() {
		// Pas d'action.
	}
	
	@Override
	public void reset() {
		// Pas d'action.
	}
}
