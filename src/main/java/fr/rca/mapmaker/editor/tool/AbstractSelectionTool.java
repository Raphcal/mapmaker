package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Instance;
import java.awt.Component;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JPanel;

public class AbstractSelectionTool extends MouseAdapter implements Tool {

	protected final Grid grid;
	protected JPanel spriteLayerPanel;
	protected TileLayer selectionLayer;
	
	protected Point startPoint;
	protected Point translation;
	
	protected boolean selected = false;
	
	protected Set<Instance> selectedInstances = new HashSet<Instance>();
	
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
	
	public AbstractSelectionTool(Grid grid, JPanel spriteLayerPanel) {
		this(grid);
		this.spriteLayerPanel = spriteLayerPanel;
	}

	public void setSelected(boolean selected) {
		this.selected = selected;
		
		if(selected) {
			translation = new Point(0, 0);
			grid.getOverlay().copyAndTranslate(selectionLayer, 0, 0);
			selectInstances();
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
		
		final int tileSize = gridTileSize();
		final int x = translation.x * tileSize;
		final int y = translation.y * tileSize;
		for (final Instance instance : selectedInstances) {
			final Point p = instance.getPoint();
			p.x += x;
			p.y += y;
			instance.updateBounds();
		}
		selectedInstances.clear();
		
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
			
		if(!directly) {
			final int x = point.x - startPoint.x + translation.x;
			final int y = point.y - startPoint.y + translation.y;
			layer.copyAndTranslate(selectionLayer, x, y);
			
			final int tileSize = gridTileSize();
			
			for (final Instance instance : selectedInstances) {
				instance.previewTranslation(x * tileSize, y * tileSize);
			}
		} else {
			if(startPoint != null) {
				final int x = point.x - startPoint.x;
				final int y = point.y - startPoint.y;
				
				layer.translate(x, y);
				
				final int tileSize = gridTileSize();
				
				for (final Instance instance : selectedInstances) {
					final Point p = instance.getPoint();
					p.x += x * tileSize;
					p.y += y * tileSize;
					instance.updateBounds();
				}
			}
			startPoint = point;
		}
	}
	
	@Override
	public void reset() {
		if(selected) {
			releaseSelection();
		} else {
			grid.getOverlay().clear();
		}
		selectedInstances.clear();
	}
	
	protected void selectInstances() {
		if (spriteLayerPanel == null) {
			return;
		}
		for (final Component component : spriteLayerPanel.getComponents()) {
			if (component instanceof Instance) {
				final Instance instance = (Instance) component;
				final Point point = pointInGridForInstance(instance);
				if (selectionLayer.getTile(point.x, point.y) >= 0) {
					selectedInstances.add(instance);
				} 
			}
		}
	}
	
	protected void selectInstancesInRect(int x1, int y1, int x2, int y2) {
		if (spriteLayerPanel == null) {
			return;
		}
		for (final Component component : spriteLayerPanel.getComponents()) {
			if (component instanceof Instance) {
				final Instance instance = (Instance) component;
				final Point point = pointInGridForInstance(instance);
				if (point.x >= x1 && point.x <= x2 &&
						point.y >= y1 && point.y <= y2) {
					selectedInstances.add(instance);
				} 
			}
		}
	}
	
	private int gridTileSize() {
		return grid.getTileMap().getPalette().getTileSize();
	}
	
	private Point pointInGridForInstance(Instance instance) {
		final int tileSize = gridTileSize();
		final Point point = instance.getPoint();
		return new Point(point.x / tileSize, point.y / tileSize);
	}
}
