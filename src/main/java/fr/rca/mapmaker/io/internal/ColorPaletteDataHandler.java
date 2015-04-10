package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author daeke
 */
public class ColorPaletteDataHandler implements DataHandler<ColorPalette> {
	
	private final Format format;

	public ColorPaletteDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void write(ColorPalette t, OutputStream outputStream) throws IOException {
		
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
				
		final Color[] colors = t.getColors();
		
		Streams.write(colors.length, outputStream);
		
		for(final Color color : colors) {
			colorHandler.write(color, outputStream);
		}
	}

	@Override
	public ColorPalette read(InputStream inputStream) throws IOException {
		
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
		
		final int length = Streams.readInt(inputStream);
		
		final ColorPalette palette = new ColorPalette(length);
		
		for(int index = 0; index < length; index++) {
			palette.setColor(index, colorHandler.read(inputStream));
		}
		
		return palette;
	}
	
}
