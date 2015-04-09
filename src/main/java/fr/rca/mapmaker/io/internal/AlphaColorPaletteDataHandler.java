package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author daeke
 */
public class AlphaColorPaletteDataHandler implements DataHandler<AlphaColorPalette> {
	
	private final Format format;

	public AlphaColorPaletteDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void write(AlphaColorPalette t, OutputStream outputStream) throws IOException {
		
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
				
		final Color[] colors = t.getColors();
		
		Streams.write(colors.length, outputStream);
		
		for(final Color color : colors)
			colorHandler.write(color, outputStream);
	}

	@Override
	public AlphaColorPalette read(InputStream inputStream) throws IOException {
		
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
		
		final int length = Streams.readInt(inputStream);
		
		final AlphaColorPalette palette = new AlphaColorPalette(length);
		
		for(int index = 0; index < length; index++)
			palette.setColor(index, colorHandler.read(inputStream));
		
		return palette;
	}
	
}
