package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.HasFunctionHitbox;
import fr.rca.mapmaker.model.palette.ImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.operation.Operation;
import fr.rca.mapmaker.operation.OperationParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ImagePaletteDataHandler implements DataHandler<Palette> {
	
	@Override
	public void write(Palette t, OutputStream outputStream) throws IOException {
		Streams.write(t.toString(), outputStream);
		Streams.write(calculateColumns(t), outputStream);
		Streams.write(t.getTileSize(), outputStream);
		Streams.write(ProjectDataHandler.PALETTE_PADDING, outputStream);
		
		if(t instanceof HasFunctionHitbox) {
			// Écriture des fonctions d'hitbox
			final HasFunctionHitbox hasFunctionHitbox = (HasFunctionHitbox) t;
			
			Streams.write(t.size(), outputStream);
			for(int index = 0; index < t.size(); index++) {
				final String function = hasFunctionHitbox.getFunction(index);
				
				Streams.write(function != null, outputStream);
				if(function != null) {
					final Operation operation = OperationParser.parse(function);
					
					Streams.write(operation.toByteArray(), outputStream);
				}
			}
		}
	}
	
	private int calculateColumns(Palette p) {
		final long neededSurface = (p.getTileSize() + ProjectDataHandler.PALETTE_PADDING) * (p.getTileSize() + ProjectDataHandler.PALETTE_PADDING) * p.size();
		final int size = Surfaces.getNearestUpperPowerOfTwoForSurface(neededSurface);
		
		return (size - ProjectDataHandler.PALETTE_PADDING) / (p.getTileSize() + ProjectDataHandler.PALETTE_PADDING);
	}
	
	@Override
	public ImagePalette read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("NIY");
	}
	
}
