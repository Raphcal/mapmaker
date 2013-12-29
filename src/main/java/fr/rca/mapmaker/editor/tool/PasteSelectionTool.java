package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Rectangle;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PasteSelectionTool extends AbstractSelectionTool {

	public PasteSelectionTool(Grid grid) {
		super(grid);
	}
	
	public void setSelection(final int[] tiles, Rectangle rectangle) {
		final TileLayer overlay = grid.getOverlay();
		overlay.restoreData(tiles, rectangle);
		
		grid.setFocusVisible(true);
		selected = true;
	}

	@Override
	protected void releaseSelection() {
		super.releaseSelection();
		
		grid.removeMouseListener(this);
		grid.removeMouseMotionListener(this);
	}
}
