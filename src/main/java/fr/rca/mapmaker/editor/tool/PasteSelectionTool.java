package fr.rca.mapmaker.editor.tool;

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
		selectionLayer.restoreData(tiles, rectangle);
		
		grid.setFocusVisible(true);
		setSelected(true);
	}

	@Override
	protected void releaseSelection() {
		super.releaseSelection();
		
		grid.removeMouseListener(this);
		grid.removeMouseMotionListener(this);
	}
}
