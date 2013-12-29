package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class BufferedImageDataHandler implements DataHandler<BufferedImage> {

	@Override
	public void write(BufferedImage t, OutputStream outputStream) throws IOException {
		ImageIO.write(t, "png", outputStream);
	}

	@Override
	public BufferedImage read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("NIY");
	}
	
}
