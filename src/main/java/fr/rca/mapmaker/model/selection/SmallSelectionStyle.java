package fr.rca.mapmaker.model.selection;

import fr.rca.mapmaker.model.palette.ColorPalette;
import java.awt.Graphics;

public class SmallSelectionStyle implements SelectionStyle {

	private ColorPalette palette;

	public SmallSelectionStyle(ColorPalette palette) {
		this.palette = palette;
	}
	
	@Override
	public void paintCursor(Graphics g, int x, int y, int size) {
		
		g.setColor(palette.getInverseColor());
		g.drawRect(x, y, size - 1, size - 1);
		g.drawRect(x + 1, y + 1, size - 3, size - 3);
	}
}
