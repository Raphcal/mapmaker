package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpriteDataHandler implements DataHandler<Sprite> {
	
	private final Format format;

	public SpriteDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void write(Sprite t, OutputStream outputStream) throws IOException {
//		final ZipOutputStream zipOutputStream = (ZipOutputStream)outputStream;
//		
//		Streams.write(t.toString(), outputStream);
//		Streams.write(t.getSize(), outputStream);
//		
//		
//		final ZipEntry zipEntry = new ZipEntry("sprite-" + t.toString() + ".png");
//		zipOutputStream.putNextEntry(zipEntry);
		
		final DataHandler<BufferedImage> bufferedImageHandler = format.getHandler(BufferedImage.class);
		
		final BufferedImage sprite = renderSprite(t);
		bufferedImageHandler.write(sprite, outputStream);
//		zipOutputStream.closeEntry();
	}

	@Override
	public Sprite read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
	private BufferedImage renderSprite(Sprite sprite) {
		final Dimension dimension = getSpriteDimension(sprite);
		final BufferedImage image = new BufferedImage(dimension.width, dimension.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setBackground(new Color(0, 0, 0, 0));
		
		int y = 0;
		for(final Animation animation : sprite.getAnimations()) {
			for(final double angle : animation.getAnglesWithValue()) {
				renderFrames(graphics, animation.getFrames(angle), sprite.getPalette(), y);
				y += sprite.getHeight();
			}
		}
		
		graphics.dispose();
		
		return image;
	}
	
	private void renderFrames(Graphics2D g, List<TileLayer> frames, Palette palette, int originY) {
		int originX = 0;
		for(final TileLayer frame : frames) {
			for(int y = 0; y < frame.getHeight(); y++) {
				for(int x = 0; x < frame.getWidth(); x++) {
					palette.paintTile(g, frame.getTile(x, y), x + originX, y + originY, 1);
				}
			}
			originX += frame.getWidth();
		}
	}
	
	private Dimension getSpriteDimension(Sprite sprite) {
		int width = 0;
		int height = 0;
		
		for(final Animation animation : sprite.getAnimations()) {
			for(final double angle : animation.getAnglesWithValue()) {
				width = Math.max(animation.getFrames(angle).size(), width);
				height++;
			}
		}
		
		return new Dimension(width * sprite.getWidth(), height * sprite.getHeight());
	}
	
}
