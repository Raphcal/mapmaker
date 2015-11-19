package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Rectangle;
import java.awt.Shape;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class AbstractShapeStrokeTool extends AbstractShapeTool {

	public AbstractShapeStrokeTool(Grid grid) {
		super(grid);
	}

	@Override
	protected void drawShape(Rectangle rectangle, int tile, TileLayer layer) {
		final Shape shape = createShape(rectangle.x, rectangle.y, rectangle.width, rectangle.height);
		final Shape innerShape = createShape(rectangle.x + 1, rectangle.y + 1, rectangle.width - 2, rectangle.height - 2);
		
		layer.setTiles(shape, tile);
		layer.clear(innerShape);
	}
}
