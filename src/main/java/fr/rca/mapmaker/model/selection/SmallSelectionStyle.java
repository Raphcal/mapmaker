package fr.rca.mapmaker.model.selection;

import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Color;
import java.awt.Graphics;

public class SmallSelectionStyle implements SelectionStyle {

	public SmallSelectionStyle() {
	}
	
	@Override
	public void paintCursor(Graphics g, Palette palette, int x, int y, int size) {
		final Color color;
		if(palette instanceof ColorPalette) {
			color = ((ColorPalette)palette).getInverseColor();
		} else {
			color = Color.WHITE;
		}
		g.setColor(color);
		g.drawRect(x, y, size - 1, size - 1);
		g.drawRect(x + 1, y + 1, size - 3, size - 3);
	}
}
