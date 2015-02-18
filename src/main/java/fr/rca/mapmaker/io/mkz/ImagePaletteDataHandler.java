package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.palette.ImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ImagePaletteDataHandler implements DataHandler<Palette> {
	
	private static final int PADDING = 4;
	
	private final Format format;

	public ImagePaletteDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void write(Palette t, OutputStream outputStream) throws IOException {
		final ZipOutputStream zipOutputStream = (ZipOutputStream)outputStream;
		
		Streams.write(t.toString(), outputStream);
		Streams.write(calculateColumns(t), outputStream);
		Streams.write(t.getTileSize(), outputStream);
		Streams.write(PADDING, outputStream);
		
		final BufferedImage palette = renderPalette(t, t.getTileSize());
		
		final ZipEntry zipEntry = new ZipEntry(t.toString() + '-' + t.getTileSize() + ".png");
		zipOutputStream.putNextEntry(zipEntry);
		
		final DataHandler<BufferedImage> bufferedImageHandler = format.getHandler(BufferedImage.class);
		bufferedImageHandler.write(palette, outputStream);
		
		zipOutputStream.closeEntry();
	}
	
	private int calculateColumns(Palette p) {
		final long neededSurface = (p.getTileSize() + PADDING) * (p.getTileSize() + PADDING) * p.size();
		final int size = Surfaces.getNearestUpperPowerOfTwoForSurface(neededSurface);
		
		return (size - PADDING) / (p.getTileSize() + PADDING);
	}
	
	@Override
	public ImagePalette read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("NIY");
	}
	
	private BufferedImage renderPalette(Palette p, int tileSize) {
		final int space = PADDING;
		// Calcul de l'espace total
		// TODO: il manque la bordure droite et la bordure basse, ajouter "space * space * p.size()" ?
		final long neededSurface = (tileSize + space) * (tileSize + space) * p.size();
		final int size = Surfaces.getNearestUpperPowerOfTwoForSurface(neededSurface);
		// final int lineTileCount = (size / (tileSize + 2));
		// final int space = getNearestLowerPowerOfTwo((size - (lineTileCount * tileSize)) / lineTileCount);
		
		final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setBackground(new Color(0, 0, 0, 0));
		
		int x = space;
		int y = space;
		for(int index = 0; index < p.size(); index++) {
			p.paintTile(graphics, index, x, y - 1, tileSize);
			graphics.clearRect(x, y, tileSize, tileSize);
			p.paintTile(graphics, index, x, y + 1, tileSize);
			graphics.clearRect(x, y, tileSize, tileSize);
			p.paintTile(graphics, index, x - 1, y, tileSize);
			graphics.clearRect(x, y, tileSize, tileSize);
			p.paintTile(graphics, index, x + 1, y, tileSize);
			graphics.clearRect(x, y, tileSize, tileSize);
			p.paintTile(graphics, index, x, y, tileSize);

			// Remarque : pour être propre il faudrait ajouter 2 * l'espace mais
			// vu que l'espace minimum est de 2 pixels, c'est suffisant.
			x += tileSize + space;
			if(x + tileSize + space > size) {
				x = space;
				y += tileSize + space;
			}
		}
		
		graphics.dispose();
		
		return image;
	}
}
