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
		if(!selected) {
			selectionLayer.restoreData(tiles, rectangle);

			setSelected(true);
			
		} else {
			releaseSelection();
		}
	}
	
	public void setSelection(TileLayer data) {
		if(!selected) {
			selectionLayer.resize(
				Math.max(selectionLayer.getWidth(), data.getWidth()), 
				Math.max(selectionLayer.getHeight(), data.getHeight()));
			
			selectionLayer.copyAndTranslate(data, 0, 0);

			setSelected(true);
			
		} else {
			releaseSelection();
		}
	}

	@Override
	protected void releaseSelection() {
		super.releaseSelection();
		
		grid.removeMouseListener(this);
		grid.removeMouseMotionListener(this);
	}
}
