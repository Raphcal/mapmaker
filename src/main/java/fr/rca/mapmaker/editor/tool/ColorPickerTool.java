package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.PaletteMap;

public class ColorPickerTool extends MouseAdapter implements Tool {
	
	private final PaletteMap paletteMap;
	
	private final Grid drawingGrid;
	private final TileLayer drawingLayer;
	
	public ColorPickerTool(PaletteMap paletteMap, Grid drawingGrid, TileLayer drawingLayer) {
		
		this.paletteMap = paletteMap;
		
		this.drawingGrid = drawingGrid;
		this.drawingLayer = drawingLayer;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		handleMouseEvent(e);
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		
		handleMouseEvent(e);
	}
	
	private void handleMouseEvent(MouseEvent e) {
		
		final Point point = drawingGrid.getLayerLocation(e.getX(), e.getY());
		
		if(point.x >= 0 && point.x < drawingLayer.getWidth() &&
				point.y >= 0 && point.y < drawingLayer.getHeight()) {
		
			final int tile = drawingLayer.getTile(point);
			final int paletteWidth = paletteMap.getWidth();
			
			final Point selectedPoint = new Point(tile % paletteWidth, tile / paletteWidth);
			paletteMap.setSelection(selectedPoint);
		}
	}
	
	@Override
	public void reset() {
	}
}
