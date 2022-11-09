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
public class PaletteNamesAsCodeHandler extends CodeDataHandler<List<Palette>> {

	@Override
	public void write(List<Palette> t, OutputStream outputStream) throws IOException {
		final PaletteAsHeaderHandler paletteAsHeaderHandler = new PaletteAsHeaderHandler();

		outputStream.write((generateHeader(t)
				+ "#include \"palettenames.h\"\n"
				+ "\n"
				+ "extern PlaydateAPI * _Nullable playdate;\n"
				+ "\n"
				+ t.stream()
						.map(palette -> "#include \""+ paletteAsHeaderHandler.fileNameFor(palette) + "\"\n")
						.collect(Collectors.joining())
				+ "\n"
				+ "void PaletteNameSetMapHitboxes(PaletteName self, MELMap * _Nonnull map) {\n"
				+ "    switch (self) {\n"
				+ t.stream()
						.map(palette -> "    case PaletteName" + Names.normalizeName(palette, Names::toPascalCase) + ":\n" +
										"        map->xHitbox = &palette" + Names.normalizeName(palette, Names::toPascalCase) + "XHitbox;\n" +
										"        map->yHitbox = &palette" + Names.normalizeName(palette, Names::toPascalCase) + "YHitbox;\n" +
										"        break;\n")
						.collect(Collectors.joining())
				+ "    default:\n"
				+ "        playdate->system->error(\"Unsupported palette name: %d\", self);\n"
				+ "        break;\n"
				+ "    }\n"
				+ "}\n"
				+ "\n"
				+ "LCDBitmapTable * _Nullable PaletteNameLoadBitmapTable(PaletteName self) {\n"
				+ "    const char *error = NULL;\n"
				+ "    LCDBitmapTable *table = NULL;\n"
				+ "    switch (self) {\n"
				+ t.stream()
						.map(palette -> "    case PaletteName" + Names.normalizeName(palette, Names::toPascalCase) + ":\n" +
										"        table = playdate->graphics->loadBitmapTable(\"palette-" + Names.normalizeName(palette, Names::toLowerCase) + "\", &error);\n" +
										"        break;\n")
						.collect(Collectors.joining())
				+ "    default:\n"
				+ "        playdate->system->error(\"Unsupported palette name: %d\", self);\n"
				+ "        return NULL;\n"
				+ "    }\n"
				+ "    if (error) {\n"
				+ "        playdate->system->error(\"Unable to load palette: %s\", error);\n"
				+ "        return NULL;\n"
				+ "    }\n"
				+ "    return table;\n"
				+ "}\n"
				).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(List<Palette> t) {
		return "palettenames.c";
	}
}
