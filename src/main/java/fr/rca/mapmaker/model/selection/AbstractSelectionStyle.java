package fr.rca.mapmaker.model.selection;

import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Graphics;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public abstract class AbstractSelectionStyle implements SelectionStyle {

	@Override
	public void paintCursor(Graphics g, Palette palette, int size, int x, int y, int width, int height) {
		paintCursor(g, palette, (int) (x * size), (int) (y * size), (int) (width * size), (int) (height * size));
	}
	
}
