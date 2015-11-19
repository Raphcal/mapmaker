package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.editor.undo.Memento;
import fr.rca.mapmaker.model.Optional;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.ui.PalettePicker;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class BrushTool extends MouseAdapter implements Tool {
	
	private final Memento memento = Optional.newInstance(Memento.class);
	private Grid grid;
	private PalettePicker palettePicker;
	
	private boolean draw;

	public void setGrid(Grid grid) {
		this.grid = grid;
	}

	public void setPalettePicker(PalettePicker palettePicker) {
		this.palettePicker = palettePicker;
	}
	
	public void setMemento(Memento memento) {
		Optional.set(this.memento, memento);
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		this.draw = e.getButton() == MouseEvent.BUTTON1;
		drawAtPoint(e.getPoint());
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		this.draw = e.getButton() == MouseEvent.BUTTON1;
		memento.begin();
		drawAtPoint(e.getPoint());
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		memento.end();
	}
	
	@Override
	public void setup() {
		// Pas d'action.
	}
	
	@Override
	public void reset() {
		// Pas d'action.
	}
	
	private void drawAtPoint(Point point) {
		final Point layerLocation = grid.getLayerLocation(point);
		final TileLayer layer = (TileLayer) grid.getActiveLayer();
		
		if(draw) {
			layer.mergeAtPoint(palettePicker.getSelectionAsLayer(), layerLocation);
		} else {
			layer.clear(layerLocation.x, layerLocation.y);
		}
	}
	
}
