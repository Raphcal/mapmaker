package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.TileMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapAsHeaderHandler extends CodeDataHandler<TileMap> {

	private static final String MAP_TYPE = "MELConstMap";

	@Override
	public void write(TileMap t, OutputStream outputStream) throws IOException {
		final PaletteAsHeaderHandler paletteAsHeaderHandler = new PaletteAsHeaderHandler();

		final String name = Names.normalizeName(t, Names::toLowerCase);
		final String camelCasedName = Names.normalizeName(t, Names::toCamelCase);
		outputStream.write((generateHeader(t)
				+ "#ifndef map" + name + "_h\n"
				+ "#define map" + name + "_h\n"
				+ "\n"
				+ "#include <stdio.h>\n"
				+ "\n"
				+ "#include \"../lib/melice.h\"\n"
				+ "#include \"" + paletteAsHeaderHandler.fileNameFor(t.getPalette()) + "\"\n"
				+ "\n"
				+ MAP_TYPE + " * _Nonnull createMap" + camelCasedName + "(void);\n"
				+ "\n"
				+ "#endif /* map" + name + "_h */\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public TileMap read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String fileNameFor(TileMap t) {
		return "map" + Names.normalizeName(t, Names::toLowerCase) + ".h";
	}
}
