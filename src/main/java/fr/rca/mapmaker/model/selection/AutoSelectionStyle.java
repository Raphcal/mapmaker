package fr.rca.mapmaker.model.selection;

import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Graphics;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class AutoSelectionStyle extends AbstractSelectionStyle {
	
	private static final int SMALL_SIZE = 12;
	
	private final SelectionStyle medium = new DefaultSelectionStyle();
	private final SelectionStyle small = new SmallSelectionStyle();

	@Override
	public void paintCursor(Graphics g, Palette palette, int x, int y, int width, int height) {
		if(width <= SMALL_SIZE || height <= SMALL_SIZE) {
			small.paintCursor(g, palette, x, y, width, height);
		} else {
			medium.paintCursor(g, palette, x, y, width, height);
		}
	}

	@Override
	public void paintCursor(Graphics g, Palette palette, double size, int x, int y, int width, int height) {
		if(size <= SMALL_SIZE) {
			small.paintCursor(g, palette, size, x, y, width, height);
		} else {
			medium.paintCursor(g, palette, size, x, y, width, height);
		}
	}
	
}
