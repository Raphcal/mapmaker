package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.palette.Palette;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class PaletteNameAsHeaderHandler extends CodeDataHandler<List<Palette>> {

	@Override
	public void write(List<Palette> t, OutputStream outputStream) throws IOException {
		outputStream.write((generateHeader(t)
				+ "#ifndef palettenames_h\n"
				+ "#define palettenames_h\n"
				+ "\n"
				+ "#include <stdio.h>\n"
				+ "\n"
				+ "#include \"pd_api.h\"\n"
				+ "#include \"../lib/melice.h\"\n"
				+ "\n"
				+ "typedef enum {\n"
				+ t.stream()
						.map(palette -> "    PaletteName" + Names.normalizeName(palette, Names::toPascalCase) + ",\n")
						.collect(Collectors.joining())
				+ "} PaletteName;\n"
				+ "\n"
				+ "void PaletteNameSetMapHitboxes(PaletteName self, MELConstMap * _Nonnull map);\n"
				+ "LCDBitmapTable * _Nullable PaletteNameLoadBitmapTable(PaletteName self);\n"
				+ "\n"
				+ "#endif /* palettenames_h */\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(List<Palette> t) {
		return "palettenames.h";
	}
}
