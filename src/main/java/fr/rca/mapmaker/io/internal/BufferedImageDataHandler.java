package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Streams;
import java.awt.image.BufferedImage;
import java.awt.image.ColorModel;
import java.awt.image.Raster;
import java.awt.image.WritableRaster;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class BufferedImageDataHandler implements DataHandler<BufferedImage> {

	@Override
	public void write(BufferedImage t, OutputStream outputStream) throws IOException {
		
		// Données de l'image
		final Raster raster = t.getRaster();
		Streams.write(raster.getWidth(), outputStream);
		Streams.write(raster.getHeight(), outputStream);
		
		final int[] pixels = raster.getPixels(0, 0, raster.getWidth(), raster.getHeight(), (int[]) null);
		Streams.write(pixels, outputStream);
		
		// Prémultiplié
//		Streams.write(t.isAlphaPremultiplied(), outputStream);
	}

	@Override
	public BufferedImage read(InputStream inputStream) throws IOException {
		
		// Modèle de couleurs
		final ColorModel colorModel = ColorModel.getRGBdefault();
		
		// Données de l'image
		final int width = Streams.readInt(inputStream);
		final int height = Streams.readInt(inputStream);
		final WritableRaster raster = colorModel.createCompatibleWritableRaster(width, height);
		
		final int[] pixels = Streams.readIntArray(inputStream);
		raster.setPixels(0, 0, width, height, pixels);
		
		// Prémultiplié
//		final boolean rasterPremultiplied = Streams.readBoolean(inputStream);
		
		// Construction de l'image
		return new BufferedImage(colorModel, raster, true, null);
	}
	
}
