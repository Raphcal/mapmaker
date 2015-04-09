package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Point;
import java.awt.event.MouseEvent;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ReplaceColorTool implements Tool {

	private Grid grid;

	public ReplaceColorTool() {
	}
	
	public ReplaceColorTool(Grid grid) {
		this.grid = grid;
	}

	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void reset() {
		// Pas d'action.
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		final Layer layer = grid.getActiveLayer();
		
		if(layer instanceof DataLayer) {
			final DataLayer dataLayer = (DataLayer) layer;
			
			final Point point = grid.getLayerLocation(e.getX(), e.getY());
			final int sourceTile = layer.getTile(point);
			final int destinationTile = grid.getTileMap().getPalette().getSelectedTile();
			
			final int[] data = dataLayer.copyData();
			for(int index = 0; index < data.length; index++) {
				if(data[index] == sourceTile) {
					data[index] = destinationTile;
				}
			}
			
			dataLayer.restoreData(data, null);
		}
	}

	@Override
	public void mousePressed(MouseEvent e) {
		// Pas d'action.
	}

	@Override
	public void mouseReleased(MouseEvent e) {
		// Pas d'action.
	}

	@Override
	public void mouseEntered(MouseEvent e) {
		// Pas d'action.
	}

	@Override
	public void mouseExited(MouseEvent e) {
		// Pas d'action.
	}

	@Override
	public void mouseDragged(MouseEvent e) {
		// Pas d'action.
	}

	@Override
	public void mouseMoved(MouseEvent e) {
		// Pas d'action.
	}
	
}
