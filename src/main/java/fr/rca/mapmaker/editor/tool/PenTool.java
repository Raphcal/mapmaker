package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.editor.undo.LayerMemento;
import fr.rca.mapmaker.model.map.PaletteMap;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;

public class PenTool extends MouseAdapter implements Tool {

	private Grid grid;
	private int button = 0;
	
	private Point lastPoint;
	
	private LayerMemento memento;
	
	private ColorPickerTool colorPickerTool;

	public PenTool() {
	}
	
	public PenTool(Grid grid) {
		this.grid = grid;
	}
	
	public PenTool(Grid grid, LayerMemento memento) {
		this.grid = grid;
		this.memento = memento;
	}
	
	public PenTool(Grid grid, PaletteMap paletteMap, LayerMemento memento) {
		this.grid = grid;
		this.memento = memento;
		this.colorPickerTool = new ColorPickerTool(paletteMap, grid);
	}

	public void setMemento(LayerMemento memento) {
		this.memento = memento;
	}

	public LayerMemento getMemento() {
		return memento;
	}

	public void setPaletteMap(PaletteMap paletteMap) {
		if(colorPickerTool == null) {
			this.colorPickerTool = new ColorPickerTool(paletteMap, grid);
		} else {
			this.colorPickerTool.setPaletteMap(paletteMap);
		}
	}
	
	public void setPaletteMaps(PaletteMap alphaPaletteMap, PaletteMap paletteMap) {
		if(colorPickerTool == null) {
			this.colorPickerTool = new ColorPickerTool(alphaPaletteMap, paletteMap, grid);
		} else {
			this.colorPickerTool.setPaletteMap(paletteMap);
			this.colorPickerTool.setAlphaPaletteMap(alphaPaletteMap);
		}
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void mousePressed(MouseEvent e) {
		button = e.getButton();
	}
	
	@Override
	public void mouseReleased(MouseEvent e) {
		button = 0;
		lastPoint = null;
		
		if(memento != null) {
			memento.end();
		}
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		button = e.getButton();
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		if(colorPickerTool == null || button == MouseEvent.BUTTON1) {
			draw(point);
		} else {
			colorPickerTool.mouseClicked(e);
		}
	}
	
	@Override
	public void mouseDragged(MouseEvent e) {
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		if(lastPoint == null) {
			if(memento != null) {
				memento.begin();
			}
			
		} else if(lastPoint != null && lastPoint.equals(point)) {
			return;
		}
		
		final TileLayer layer = (TileLayer) grid.getActiveLayer();
		
		if(point.x >= 0 && point.x < layer.getWidth() &&
				point.y >= 0 && point.y < layer.getHeight()) {
			
			draw(point);
		}
		
		lastPoint = point;
	}
	
	private void draw(Point point) {
		final TileLayer layer = (TileLayer) grid.getActiveLayer();
		
		if(button == MouseEvent.BUTTON1) {
			layer.setTile(point.x, point.y, grid.getTileMap().getPalette().getSelectedTile());
		} else {
			layer.clear(point.x, point.y);
		}
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
