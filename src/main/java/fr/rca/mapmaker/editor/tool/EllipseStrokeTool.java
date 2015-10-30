package fr.rca.mapmaker.editor.tool;

import java.awt.Shape;
import java.awt.geom.Ellipse2D;

import fr.rca.mapmaker.ui.Grid;

public class EllipseStrokeTool extends AbstractShapeStrokeTool {

	public EllipseStrokeTool(Grid grid) {
		super(grid);
	}

	@Override
	protected Shape createShape(int x, int y, int width, int height) {
		return new Ellipse2D.Double(x - 1, y - 1, width + 2, height + 2);
	}
}
