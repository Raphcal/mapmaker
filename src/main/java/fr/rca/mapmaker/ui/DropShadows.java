package fr.rca.mapmaker.ui;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DropShadows extends JComponent {
	
	public static void drawShadow(Rectangle r, Graphics2D g) {
		
//		final RoundRectangle2D shadowRectangle = new RoundRectangle2D.Double((double)r.x, (double)r.y, (double) r.width, (double) r.height, 8.0, 8.0);
		final float steps = (float) Math.sqrt((r.width/2) * (r.width/2) + (r.height/2) * (r.height/2)) * 2.0f;
		float stepX = (float)r.width * 2.0f / steps;
		float stepY = (float)r.height * 2.0f / steps;
		
		final Color color = new Color(0, 0, 0, Math.min(4.0f / steps, 1.0f));
		g.setColor(color);
		
		float x = (float) r.x;
		float y = (float) r.y;
		float width = (float) r.width;
		float height = (float) r.height;
		
		final int max = (int) steps;
		for(int index = 0; index <= max; index++) {
			g.fillRoundRect((int)x, (int)y, (int)width, (int)height, 16, 16);
			x += stepX;
			y += stepY;
			width -= stepX + stepX;
			height -= stepY + stepY;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		drawShadow(g.getClipBounds(), (Graphics2D)g);
	}
}
