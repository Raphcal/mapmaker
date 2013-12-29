package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;

public class AbstractSelectionTool extends MouseAdapter implements Tool {

	protected final Grid grid;
	
	protected Point startPoint;
	
	protected boolean selected = false;
	
	public AbstractSelectionTool(Grid grid) {
		this.grid = grid;
	}
	
	protected void handleMouseClicked(MouseEvent e) {
	}
	
	protected void handleMouseDragged(MouseEvent e) {
	}
	
	protected void handleMousePressed(MouseEvent e) {
	}
	
	protected void handleMouseReleased(MouseEvent e) {
	}
	
	protected void releaseSelection() {
		final TileLayer drawingLayer = (TileLayer)grid.getActiveLayer();
		final TileLayer previewLayer = grid.getOverlay();
		
		drawingLayer.merge(previewLayer);
		previewLayer.clear();
		grid.setFocusVisible(false);
		selected = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		if(selected) {
			if(grid.getOverlay().getTile(point) == -1)
				releaseSelection();
			
		} else
			handleMouseClicked(e);
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = grid.getLayerLocation(e.getX(), e.getY());
		
		if(!selected)
			handleMousePressed(e);
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		
		if(!selected)
			handleMouseReleased(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		if(startPoint == null)
			return;
		
		if(!selected)
			handleMouseDragged(e);
			
		else {
			final Point point = grid.getLayerLocation(e.getX(), e.getY());
			
			if(!point.equals(startPoint)) { 
				grid.getOverlay().translate(point.x - startPoint.x, point.y - startPoint.y);
				startPoint = point;
			}
		}
	}
	
	@Override
	public void reset() {
		
		if(selected) {
			releaseSelection();
			
		} else {
			grid.getOverlay().clear();
			grid.setFocusVisible(false);
		}
	}
}
