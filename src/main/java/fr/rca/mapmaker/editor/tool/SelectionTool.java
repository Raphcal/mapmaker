package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import javax.swing.JPanel;

public class SelectionTool extends AbstractSelectionTool {

	private Point lastDragPoint;
	private boolean moveOverlay;
	
	public SelectionTool(Grid grid) {
		super(grid);
	}

	public SelectionTool(Grid grid, JPanel spriteLayerPanel) {
		super(grid, spriteLayerPanel);
	}

	@Override
	protected void handleMousePressed(MouseEvent e) {
		moveOverlay = e.getButton() == MouseEvent.BUTTON1;
	}

	@Override
	protected void handleMouseReleased(MouseEvent e) {
		if(moveOverlay) {
			final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();

			final Point endPoint = grid.getLayerLocation(e.getX(), e.getY());

			final int startX = Math.min(startPoint.x, endPoint.x);
			final int startY = Math.min(startPoint.y, endPoint.y);
			final int endX = Math.min(startX + Math.abs(endPoint.x - startPoint.x) + 1, drawingLayer.getWidth());
			final int endY = Math.min(startY + Math.abs(endPoint.y - startPoint.y) + 1, drawingLayer.getHeight());

			for(int y = startY; y < endY; y++) {
				for(int x = startX; x < endX; x++) {
					selectionLayer.setTile(x, y, drawingLayer.getTile(x, y));
				}
			}

			drawingLayer.clear(new Rectangle(startX, startY,
					Math.abs(endPoint.x - startPoint.x) + 1, Math.abs(endPoint.y - startPoint.y) + 1));

			startPoint = null;
			setSelected(true);
			selectInstancesInRect(startX, startY, endX, endY);
		}
	}
	
	@Override
	protected void handleMouseDragged(MouseEvent e) {
		if(moveOverlay) {
			final TileLayer previewLayer = grid.getOverlay();

			final Point point = grid.getLayerLocation(e.getX(), e.getY());

			final Rectangle rectangle = new Rectangle(
					Math.min(startPoint.x, point.x),
					Math.min(startPoint.y, point.y),
					Math.abs(startPoint.x - point.x) + 1,
					Math.abs(startPoint.y - point.y) + 1);

			if(lastDragPoint != null) {
				final Rectangle lastRectangle = new Rectangle(
						Math.min(startPoint.x, lastDragPoint.x),
						Math.min(startPoint.y, lastDragPoint.y),
						Math.abs(startPoint.x - lastDragPoint.x) + 1,
						Math.abs(startPoint.y - lastDragPoint.y) + 1);

				previewLayer.clear(lastRectangle);
			}

			final Rectangle shape = new Rectangle(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
			final Rectangle innerShape = new Rectangle(rectangle.x + 1, rectangle.y + 1, rectangle.width - 2, rectangle.height - 2);

			// FIXME: Utiliser autre chose que des tiles
			previewLayer.setTiles(shape, 0);
			previewLayer.clear(innerShape);
			lastDragPoint = point;
		}
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		if(moveOverlay) {
			super.mouseDragged(e);
		} else {
			moveLayer((TileLayer) grid.getActiveLayer(), e, true);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		if(e.getButton() != MouseEvent.BUTTON1) {
			startPoint = grid.getLayerLocation(e.getX(), e.getY());
		}
		super.mousePressed(e);
	}
	
}
