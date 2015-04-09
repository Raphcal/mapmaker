package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PaletteDataHandler implements DataHandler<Palette> {
	
	private Format format;

	public PaletteDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void write(Palette t, OutputStream outputStream) throws IOException {
		final DataHandler<Palette> paletteHandler = format.getHandler(t.getClass());
		Streams.write(t.getClass(), outputStream);
		
		paletteHandler.write(t, outputStream);
	}

	@Override
	public Palette read(InputStream inputStream) throws IOException {
		final String paletteClass = Streams.readString(inputStream);
		final DataHandler<Palette> paletteHandler = format.getHandler(paletteClass);
		
		return paletteHandler.read(inputStream);
	}
	
}
