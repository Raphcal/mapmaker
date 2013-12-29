package fr.rca.mapmaker.editor.tool;

import java.awt.Rectangle;
import java.awt.Shape;

import fr.rca.mapmaker.ui.Grid;

public class RectangleFillTool extends AbstractShapeFillTool {

	public RectangleFillTool(Grid grid) {
		super(grid);
	}

	@Override
	protected Shape createShape(int x, int y, int width, int height) {
		return new Rectangle(x, y, width, height);
	}

}
