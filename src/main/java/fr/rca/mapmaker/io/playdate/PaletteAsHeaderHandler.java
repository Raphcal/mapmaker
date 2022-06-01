package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.palette.Palette;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class PaletteAsHeaderHandler extends CodeDataHandler<Palette> {

	@Override
	public void write(Palette t, OutputStream outputStream) throws IOException {
		final String name = Names.normalizeName(t, Names::toLowerCase);
		outputStream.write((generateHeader(t)
				+ "#ifndef palette" + name + "_h\n"
				+ "#define palette" + name + "_h\n"
				+ "\n"
				+ "#include <stdio.h>\n"
				+ "\n"
				+ "#include \"../lib/melice.h\"\n"
				+ "\n"
				+ "float palette" + Names.normalizeName(t, Names::toPascalCase) + "Hitbox(uint8_t tile, float x);\n"
				+ "\n"
				+ "#endif /* palette" + name + "_h */\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public Palette read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String fileNameFor(Palette t) {
		return "palette" + Names.normalizeName(t, Names::toLowerCase) + ".h";
	}
}
