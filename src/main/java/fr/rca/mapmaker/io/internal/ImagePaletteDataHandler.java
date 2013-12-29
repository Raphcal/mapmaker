package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.palette.ImagePalette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ImagePaletteDataHandler implements DataHandler<ImagePalette> {

	private final Format format;

	public ImagePaletteDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void write(ImagePalette t, OutputStream outputStream) throws IOException {
		
		Streams.write(t.getTileSize(), outputStream);
		
		final DataHandler<BufferedImage> bufferedImageHandler = format.getHandler(BufferedImage.class);
		bufferedImageHandler.write(t.getTiles(), outputStream);
	}

	@Override
	public ImagePalette read(InputStream inputStream) throws IOException {
		
		final int tileSize = Streams.readInt(inputStream);
		
		final DataHandler<BufferedImage> bufferedImageHandler = format.getHandler(BufferedImage.class);
		final BufferedImage bufferedImage = bufferedImageHandler.read(inputStream);
		
		return new ImagePalette(bufferedImage, tileSize);
	}
	
}
