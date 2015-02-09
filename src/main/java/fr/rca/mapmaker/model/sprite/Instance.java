package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Instance extends JComponent {
	private static final int TILE_SIZE = 1;
	
	private Sprite sprite;
	private BufferedImage image;

	public Instance() {
	}

	public Instance(Sprite sprite, Point location) {
		setSprite(sprite);
		setBounds(location.x, location.y, sprite.getSize(), sprite.getSize());
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	private void setSprite(Sprite sprite) {
		this.sprite = sprite;
		setPreferredSize(new Dimension(sprite.getSize(), sprite.getSize()));
		
		final ImageRenderer renderer = new ImageRenderer();
		
		final TileLayer defaultLayer = sprite.getDefaultLayer();
		if(defaultLayer != null) {
			image = renderer.renderImage(defaultLayer, sprite.getPalette(), TILE_SIZE);
		} else {
			image = renderer.renderImage(sprite.getSize(), sprite.getSize(), TILE_SIZE);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
}
