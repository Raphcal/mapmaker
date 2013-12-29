package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;

public class CircleStrokeTool extends MouseAdapter implements Tool {

	private final Grid grid;
	private int target;
	
	private final TileLayer drawingLayer;
	private final TileLayer previewLayer;
	
	private Point startPoint;
	private Rectangle lastRectangle;
	
	public CircleStrokeTool(Grid grid, TileLayer drawingLayer, TileLayer previewLayer) {
		this.grid = grid;
		this.drawingLayer = drawingLayer;
		this.previewLayer = previewLayer;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = grid.getLayerLocation(e.getX(), e.getY());
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			final int selectedTile = grid.getTileMap().getPalette().getSelectedTile();
			if(selectedTile == -1)
				target = -2;
			else
				target = selectedTile;
			
		} else
			target = -2;
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		startPoint = null;
		lastRectangle = null;
		
		drawingLayer.merge(previewLayer);
		previewLayer.clear();
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		if(startPoint == null)
			return;
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		final int radius = (int) Math.sqrt(square(startPoint.x - point.x) + 
				square(startPoint.y - point.y));

		if(lastRectangle != null)
			previewLayer.clear(lastRectangle);
		
		final Rectangle rectangle = new Rectangle(startPoint.x - radius,
				startPoint.y - radius, radius * 2 + 1, radius * 2 + 1);
		
		// FIXME: A optimiser (avec le rayon réel)
		for(int y = 0; y < drawingLayer.getHeight(); y++) {
			for(int x = 0; x < drawingLayer.getWidth(); x++) {
				final int length = (int) Math.sqrt(square(startPoint.x - x) + 
						square(startPoint.y - y));
				
				if(length == radius)
					previewLayer.setTile(x, y, target);
			}
		}
		
		lastRectangle = rectangle;
	}
	
	private int square(int value) {
		return value * value;
	}
	
	@Override
	public void reset() {
	}
}
