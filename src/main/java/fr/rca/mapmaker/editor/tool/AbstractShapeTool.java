package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;

public abstract class AbstractShapeTool extends MouseAdapter implements Tool {

	private final Grid grid;
	private int target;
	
	private Point startPoint;
	private Rectangle lastRectangle;
	
	public AbstractShapeTool(Grid grid) {
		this.grid = grid;
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
		
		final TileLayer previewLayer = grid.getOverlay();
		
		((TileLayer)grid.getActiveLayer()).merge(previewLayer);
		previewLayer.clear();
	}
	
	protected abstract Shape createShape(int x, int y, int width, int height);
	
	protected abstract void drawShape(Rectangle rectangle, int tile, TileLayer layer);
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		if(startPoint == null)
			return;
		
		final TileLayer previewLayer = grid.getOverlay();
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		final Rectangle rectangle = new Rectangle(
				Math.min(startPoint.x, point.x),
				Math.min(startPoint.y, point.y),
				Math.abs(startPoint.x - point.x),
				Math.abs(startPoint.y - point.y));
		
		if(lastRectangle != null)
			previewLayer.clear(lastRectangle);
		
		drawShape(rectangle, target, previewLayer);
		
		lastRectangle = new Rectangle(rectangle.x, rectangle.y,
				rectangle.width + 1, rectangle.height + 1);
	}
	
	@Override
	public void reset() {
	}
}
