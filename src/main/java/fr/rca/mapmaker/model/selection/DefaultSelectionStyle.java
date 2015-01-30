package fr.rca.mapmaker.model.selection;

import java.awt.Color;
import java.awt.Graphics;

public class DefaultSelectionStyle implements SelectionStyle {

	@Override
	public void paintCursor(Graphics g, fr.rca.mapmaker.model.palette.Palette palette, int x, int y, int size) {
		size--;
		
		g.setColor(Color.BLACK);
		g.drawRect(x, y, size, size);
		g.drawRect(x + 3, y + 3, size - 6, size - 6);
		
		g.setColor(Color.WHITE);
		g.drawRect(x + 1, y + 1, size - 2, size - 2);
		g.drawRect(x + 2, y + 2, size - 4, size - 4);
	}

}
