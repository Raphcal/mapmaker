package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.HasFunctionHitbox;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.operation.ByteCode;
import fr.rca.mapmaker.operation.Language;
import fr.rca.mapmaker.operation.OperationParser;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Map;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class PaletteAsCodeHandler extends CodeDataHandler<Palette> {

	@Override
	public void write(Palette t, OutputStream outputStream) throws IOException {
		final String name = Names.normalizeName(t, Names::toLowerCase);
		outputStream.write((generateHeader(t)
				+ "#include \"palette" + name + ".h\"\n"
				+ "\n"
				+ "float palette" + Names.normalizeName(t, Names::toPascalCase) + "XHitbox(uint16_t tile, float x) {\n"
				+ "    switch (tile) {\n").getBytes(StandardCharsets.UTF_8));
		if (t instanceof PaletteReference) {
			t = ((PaletteReference) t).getPalette();
		}
		if (t instanceof HasFunctionHitbox) {
			final HasFunctionHitbox hasFunctionHitbox = (HasFunctionHitbox) t;
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
				+ "}\n"
				+ "\n"
				+ "float palette" + Names.normalizeName(t, Names::toPascalCase) + "YHitbox(uint16_t tile, float y) {\n"
				+ "    switch (tile) {\n").getBytes(StandardCharsets.UTF_8));
		if (t instanceof HasFunctionHitbox) {
			final Map<ByteCode, String> replaceXByY = new HashMap<ByteCode, String>();
			replaceXByY.put(ByteCode.X, "y");

			final HasFunctionHitbox hasFunctionHitbox = (HasFunctionHitbox) t;
			for (int tile = 0; tile < t.size(); tile++) {
				String function = hasFunctionHitbox.getYFunction(tile);
				if (function != null) {
					outputStream.write(("    case " + tile + ":\n"
							+ "        return " + OperationParser.parse(function).toString(Language.C, replaceXByY) + ";\n").getBytes(StandardCharsets.UTF_8));
				}
			}
		}
		outputStream.write(("    default:\n"
				+ "        return " + t.getTileSize() + ";\n"
				+ "    }\n"
				+ "}\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(Palette t) {
		return "palette" + Names.normalizeName(t, Names::toLowerCase) + ".c";
	}

}
