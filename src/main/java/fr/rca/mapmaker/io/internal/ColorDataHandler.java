package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ColorDataHandler implements DataHandler<Color> {
	
	@Override
	public void write(Color t, OutputStream outputStream) throws IOException {
		
		Streams.write(t.getRed(), outputStream);
		Streams.write(t.getGreen(), outputStream);
		Streams.write(t.getBlue(), outputStream);
		Streams.write(t.getAlpha(), outputStream);
	}

	@Override
	public Color read(InputStream inputStream) throws IOException {
		
		final int red = Streams.readInt(inputStream);
		final int green = Streams.readInt(inputStream);
		final int blue = Streams.readInt(inputStream);
		final int alpha = Streams.readInt(inputStream);

		return new Color(red, green, blue, alpha);
	}
	
}
