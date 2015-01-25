package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;

public class PenTool extends MouseAdapter implements Tool {

	private final Grid grid;
	private int button = 0;
	
	private Point lastPoint;
	
	public PenTool(Grid grid) {
		// TODO: Utiliser un numéro de calque (ou le calque actif) plutôt que la référence.
		this.grid = grid;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		button = e.getButton();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		button = 0;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		button = e.getButton();
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		draw(point);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		if(lastPoint != null && lastPoint.equals(point))
			return;
		
		final TileLayer layer = (TileLayer) grid.getActiveLayer();
		
		if(point.x >= 0 && point.x < layer.getWidth() &&
				point.y >= 0 && point.y < layer.getHeight()) {
			
			draw(point);
		}
		
		lastPoint = point;
	}
	
	private void draw(Point point) {
		final TileLayer layer = (TileLayer) grid.getActiveLayer();
		
		if(button == MouseEvent.BUTTON1)
			layer.setTile(point.x, point.y, grid.getTileMap().getPalette().getSelectedTile());
		else
			layer.clear(point.x, point.y);
	}
	
	@Override
	public void reset() {
	}
}
