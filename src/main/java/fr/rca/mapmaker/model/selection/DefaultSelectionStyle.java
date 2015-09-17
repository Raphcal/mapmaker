package fr.rca.mapmaker.model.selection;

import java.awt.Color;
import java.awt.Graphics;

public class DefaultSelectionStyle extends AbstractSelectionStyle {

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void paintCursor(Graphics g, fr.rca.mapmaker.model.palette.Palette palette, int x, int y, int width, int height) {
		width--;
		height--;
		
		g.setColor(Color.BLACK);
		g.drawRect(x, y, width, height);
		g.drawRect(x + 3, y + 3, width - 6, height - 6);
		
		g.setColor(Color.WHITE);
		g.drawRect(x + 1, y + 1, width - 2, height - 2);
		g.drawRect(x + 2, y + 2, width - 4, height - 4);
	}

}
