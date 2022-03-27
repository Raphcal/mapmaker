package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.model.HasFunctionHitbox;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.operation.Language;
import fr.rca.mapmaker.operation.OperationParser;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class PaletteAsCodeHandler implements DataHandler<Palette> {

	@Override
	public void write(Palette t, OutputStream outputStream) throws IOException {
		final String name = Names.normalizeName(t, Names::toLowerCase);
		outputStream.write(("#include \"palette" + name + ".h\"\n"
				+ "\n"
				+ "float palette" + Names.normalizeName(t, Names::toPascalCase) + "Hitbox(uint8_t tile, float x) {\n"
				+ "    switch (tile) {\n").getBytes(StandardCharsets.UTF_8));
		if (t instanceof HasFunctionHitbox) {
			HasFunctionHitbox hasFunctionHitbox = (HasFunctionHitbox) t;
			for (int tile = 0; tile < t.size(); tile++) {
				String function = hasFunctionHitbox.getFunction(tile);
				if (function != null) {
					outputStream.write(("    case " + tile + ":\n"
							+ "        return " + OperationParser.parse(function).toString(Language.C) + ";\n").getBytes(StandardCharsets.UTF_8));
				}
			}
		}
		outputStream.write(("    default:\n"
				+ "        return " + t.getTileSize() + ";\n"
				+ "    }\n"
				+ "}\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public EditableImagePalette read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String fileNameFor(Palette t) {
		return "palette" + Names.normalizeName(t, Names::toLowerCase) + ".c";
	}

}
