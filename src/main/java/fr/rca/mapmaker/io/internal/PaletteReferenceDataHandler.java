package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Streams;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PaletteReferenceDataHandler implements DataHandler<PaletteReference> {

	@Override
	public void write(PaletteReference t, OutputStream outputStream) throws IOException {
		
		Streams.write(t.getPaletteIndex(), outputStream);
	}

	@Override
	public PaletteReference read(InputStream inputStream) throws IOException {
		
		return new PaletteReference(null, Streams.readInt(inputStream));
	}
}
