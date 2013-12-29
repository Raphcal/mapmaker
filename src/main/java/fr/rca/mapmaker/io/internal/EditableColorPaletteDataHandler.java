package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.palette.EditableColorPalette;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class EditableColorPaletteDataHandler implements DataHandler<EditableColorPalette> {

	private final Format format;

	public EditableColorPaletteDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void write(EditableColorPalette t, OutputStream outputStream) throws IOException {
		
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
				
		Streams.write(t.size(), outputStream);
		
		for(int index = 0; index < t.size(); index++) {
			final Color color = t.getColor(index);
			
			if(color != null) {
				Streams.write(true, outputStream);
				colorHandler.write(color, outputStream);
				
			} else
				Streams.write(false, outputStream);
		}
	}

	@Override
	public EditableColorPalette read(InputStream inputStream) throws IOException {
		
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
		
		final int length = Streams.readInt(inputStream);
		
		final EditableColorPalette palette = new EditableColorPalette(length);
		
		for(int index = 0; index < length; index++) {
			boolean notNull = Streams.readBoolean(inputStream);
			
			if(notNull)
				palette.setColor(index, colorHandler.read(inputStream));
		}
		
		return palette;
	}
	
}
