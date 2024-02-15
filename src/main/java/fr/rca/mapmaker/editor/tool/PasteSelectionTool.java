package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.io.playdate.TileMapHandler;
import fr.rca.mapmaker.model.map.Layer;
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
		if (!selected) {
			selectionLayer.restoreData(tiles, rectangle);

			setSelected(true);

		} else {
			releaseSelection();
		}
	}

	public void setSelection(TileLayer data) {
		if (!selected) {
			selectionLayer.resize(
					Math.max(selectionLayer.getWidth(), data.getWidth()),
					Math.max(selectionLayer.getHeight(), data.getHeight()));

			selectionLayer.copyAndTranslate(data, 0, 0);

			Layer activeLayer = grid.getActiveLayer();
			if (selectionLayer.isEmpty(new Rectangle(0, 0, activeLayer.getWidth(), activeLayer.getHeight()))) {
				// Rien à coller, recherche de l'emplacement haut gauche à coller.
				final Rectangle layerSize = TileMapHandler.getLayerSize(data);
				if (layerSize.width > 0 && layerSize.height > 0) {
					selectionLayer.resize(
						Math.max(selectionLayer.getWidth(), layerSize.width),
						Math.max(selectionLayer.getHeight(), layerSize.height));
					selectionLayer.copyAndTranslate(data, -layerSize.x, -layerSize.y);
				} else {
					// Rien à coller.
					return;
				}
			}

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
