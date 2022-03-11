package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.operation.Operation;
import fr.rca.mapmaker.operation.OperationParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class EditableImagePaletteHandler implements DataHandler<EditableImagePalette> {
	@Override
	public void write(EditableImagePalette t, OutputStream outputStream) throws IOException {
		Streams.write(t.toString(), outputStream);
		Streams.write(t.getTileSize(), outputStream);
		Streams.write(t.size(), outputStream);

		// Écriture des fonctions d'hitbox
		for(int index = 0; index < t.size(); index++) {
			final String function = t.getFunction(index);

			Streams.write(function != null, outputStream);
			if (function != null) {
				final Operation operation = OperationParser.parse(function);
				Streams.write(operation.toByteArray(), outputStream);
			}
		}
	}

	@Override
	public EditableImagePalette read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
