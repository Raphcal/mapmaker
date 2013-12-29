package fr.rca.mapmaker.ui;

import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Paint;
import java.awt.Rectangle;
import java.awt.TexturePaint;
import java.awt.image.BufferedImage;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Paints {
	
	private Paints() {}
	
	private static final int SQUARE_SIZE = 8;
	public static final Paint TRANSPARENT_PAINT;
	
	static {
		final BufferedImage image = new BufferedImage(
				SQUARE_SIZE + SQUARE_SIZE, SQUARE_SIZE + SQUARE_SIZE, BufferedImage.TYPE_INT_RGB);
		final Graphics2D graphics = image.createGraphics();
		
		graphics.setColor(Color.WHITE);
		graphics.fillRect(0, 0, SQUARE_SIZE, SQUARE_SIZE);
		graphics.fillRect(SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
		
		graphics.setColor(Color.LIGHT_GRAY);
		graphics.fillRect(SQUARE_SIZE, 0, SQUARE_SIZE, SQUARE_SIZE);
		graphics.fillRect(0, SQUARE_SIZE, SQUARE_SIZE, SQUARE_SIZE);
		
		graphics.dispose();
		
		TRANSPARENT_PAINT = new TexturePaint(image, new Rectangle(16, 16));
	}
}
