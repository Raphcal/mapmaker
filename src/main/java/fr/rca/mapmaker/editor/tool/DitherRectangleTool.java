package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class DitherRectangleTool extends AbstractShapeTool {

	public DitherRectangleTool(Grid grid) {
		super(grid);
	}
	
	@Override
	protected Shape createShape(int x, int y, int width, int height) {
		return new Rectangle(x, y, width, height);
	}

	@Override
	protected void drawShape(Rectangle rectangle, int tile, TileLayer layer) {
		for(int y = 0; y < rectangle.height; y++) {
			for(int x = y % 2; x < rectangle.width; x += 2) {
				layer.setTile(x + rectangle.x, y + rectangle.y, tile);
			}
		}
	}
	
}
