package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;

public class AbstractSelectionTool extends MouseAdapter implements Tool {

	protected final Grid grid;
	protected TileLayer selectionLayer;
	
	protected Point startPoint;
	protected Point translation;
	
	protected boolean selected = false;
	
	public AbstractSelectionTool(Grid grid) {
		this.grid = grid;
		this.selectionLayer = new TileLayer(grid.getOverlay().getWidth(), grid.getOverlay().getHeight());
		
		this.grid.addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				final TileLayer overlay = AbstractSelectionTool.this.grid.getOverlay();
				selectionLayer.resize(overlay.getWidth(), overlay.getHeight());
			}
		});
	}
	
	public void setSelected(boolean selected) {
		this.selected = selected;
		
		if(selected) {
			translation = new Point(0, 0);
			grid.getOverlay().copyAndTranslate(selectionLayer, 0, 0);
		}
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
		selectionLayer.clear();
		grid.setFocusVisible(false);
		selected = false;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		if(selected) {
			if(grid.getOverlay().getTile(point) == -1) {
				releaseSelection();
			}
			
		} else {
			handleMouseClicked(e);
		}
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		startPoint = grid.getLayerLocation(e.getX(), e.getY());
		
		if(!selected) {
			handleMousePressed(e);
		}
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		if(!selected) {
			handleMouseReleased(e);
		} else {
			final Point releasePoint = grid.getLayerLocation(e.getX(), e.getY());
			translation.x += releasePoint.x - startPoint.x;
			translation.y += releasePoint.y - startPoint.y;
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		if(startPoint == null) {
			return;
		}
		
		if(!selected) {
			handleMouseDragged(e);
			
		} else {
			moveLayer(grid.getOverlay(), e, false);
		}
	}
	
	protected void moveLayer(TileLayer layer, MouseEvent event, boolean directly) {
		final Point point = grid.getLayerLocation(event.getX(), event.getY());
			
		if(!point.equals(startPoint)) {
			if(!directly) {
				layer.copyAndTranslate(selectionLayer, point.x - startPoint.x + translation.x, point.y - startPoint.y + translation.y);
			} else {
				layer.translate(point.x - startPoint.x, point.y - startPoint.y);
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
