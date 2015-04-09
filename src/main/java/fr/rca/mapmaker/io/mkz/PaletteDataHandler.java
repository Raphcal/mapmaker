package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.ImagePalette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PaletteDataHandler implements DataHandler<Palette> {
	
	private static final Map<Class<? extends Palette>, String> CLASS_TO_NAME_MAP;
	private static final Map<String, Class<? extends Palette>> NAME_TO_CLASS_MAP;
	static {
		CLASS_TO_NAME_MAP = new HashMap<Class<? extends Palette>, String>();
		NAME_TO_CLASS_MAP = new HashMap<String, Class<? extends Palette>>();
		
		addPaletteType("IMG", ImagePalette.class);
		addPaletteType("EIMG", EditableImagePalette.class);
		addPaletteType("COL", ColorPalette.class);
		addPaletteType("ACOL", AlphaColorPalette.class);
		addPaletteType("ECOL", EditableColorPalette.class);
		addPaletteType("REF", PaletteReference.class);
	}
	
	private static void addPaletteType(String name, Class<? extends Palette> clazz) {
		CLASS_TO_NAME_MAP.put(clazz, name);
		NAME_TO_CLASS_MAP.put(name, clazz);
	}
	
	private Format format;

	public PaletteDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void write(Palette t, OutputStream outputStream) throws IOException {
		
		final DataHandler<Palette> paletteHandler = format.getHandler(t.getClass());
		Streams.write(CLASS_TO_NAME_MAP.get(t.getClass()), outputStream);
		
		paletteHandler.write(t, outputStream);
	}

	@Override
	public Palette read(InputStream inputStream) throws IOException {
		
		final String paletteName = Streams.readString(inputStream);
		final Class<?> paletteClass = NAME_TO_CLASS_MAP.get(paletteName);
		final DataHandler<Palette> paletteHandler = format.getHandler(paletteClass.getName());
		
		return paletteHandler.read(inputStream);
	}
	
}
