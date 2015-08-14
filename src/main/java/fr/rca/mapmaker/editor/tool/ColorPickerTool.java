package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.Layer;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;

public class ColorPickerTool extends MouseAdapter implements Tool {
	private PaletteMap alphaPaletteMap;
	private PaletteMap paletteMap;
	private final Grid drawingGrid;
	
	public ColorPickerTool(PaletteMap paletteMap, Grid drawingGrid) {
		this.paletteMap = paletteMap;
		this.drawingGrid = drawingGrid;
	}

	public ColorPickerTool(PaletteMap alphaPaletteMap, PaletteMap paletteMap, Grid drawingGrid) {
		this.alphaPaletteMap = alphaPaletteMap;
		this.paletteMap = paletteMap;
		this.drawingGrid = drawingGrid;
	}
	
	public void setPaletteMap(PaletteMap paletteMap) {
		this.paletteMap = paletteMap;
	}

	public void setAlphaPaletteMap(PaletteMap alphaPaletteMap) {
		this.alphaPaletteMap = alphaPaletteMap;
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
		final Layer drawingLayer = drawingGrid.getActiveLayer();
		final Point point = drawingGrid.getLayerLocation(e.getX(), e.getY());
		
		if(point.x >= 0 && point.x < drawingLayer.getWidth() &&
				point.y >= 0 && point.y < drawingLayer.getHeight()) {
		
			final int tile = drawingLayer.getTile(point);
			
			if(tile == -1) {
				paletteMap.setSelection(null);
				
			} else {
				final int paletteWidth = paletteMap.getWidth();
				
				if(alphaPaletteMap == null) {
					paletteMap.setSelection(new Point(tile % paletteWidth, tile / paletteWidth));
					
				} else {
					final int tileFromTile = AlphaColorPalette.getTileFromTile(tile);
					final int alphaFromTile = AlphaColorPalette.getAlphaFromTile(tile);
					
					alphaPaletteMap.setSelection(new Point(alphaFromTile, 0));
					paletteMap.setSelection(new Point(tileFromTile % paletteWidth, tileFromTile / paletteWidth));
				}
			}
		}
	}
	
	@Override
	public void reset() {
	}
}
