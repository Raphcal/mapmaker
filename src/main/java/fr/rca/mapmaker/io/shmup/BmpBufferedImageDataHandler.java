package fr.rca.mapmaker.io.shmup;

import fr.rca.mapmaker.io.DataHandler;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import javax.imageio.ImageIO;

/**
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public class BmpBufferedImageDataHandler implements DataHandler<BufferedImage> {

	@Override
	public void write(BufferedImage t, OutputStream outputStream) throws IOException {
		ImageIO.write(t, "bmp", outputStream);
	}

	@Override
	public BufferedImage read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not implemented yet");
	}
	
}