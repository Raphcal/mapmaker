package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class SpriteDefinitionsAsCodeHandler extends CodeDataHandler<List<Sprite>> {

	@Override
	public void write(List<Sprite> t, OutputStream outputStream) throws IOException {
		final SpriteAsHeaderHandler spriteAsHeaderHandler = new SpriteAsHeaderHandler();

		outputStream.write((generateHeader(t)
				+ "#include \"spritenames.h\"\n"
				+ "\n"
				+ "extern PlaydateAPI * _Nullable playdate;\n"
				+ "\n"
				+ t.stream()
						.map(sprite -> "#include \""+ spriteAsHeaderHandler.fileNameFor(sprite) + "\"\n")
						.collect(Collectors.joining())
				+ "\n"
				+ "MELSpriteDefinition " + (configuration.getSpriteNames().getGetDefinitionType() == PlaydateExportConfiguration.SpriteNamesGetDefinitionType.pointer ? "* _Nullable " : "") + "SpriteNameGetDefinition(SpriteName self) {\n"
				+ "    switch (self) {\n"
				+ t.stream()
						.map(sprite -> "    case SpriteName" + Names.normalizeName(sprite, Names::toPascalCase) + ":\n" +
										"        return " + (configuration.getSpriteNames().getGetDefinitionType() == PlaydateExportConfiguration.SpriteNamesGetDefinitionType.pointer ? "&" : "") + "sprite" + Names.normalizeName(sprite, Names::toPascalCase) + ";\n")
						.collect(Collectors.joining())
				+ "    default:\n"
				+ "        playdate->system->error(\"Unsupported sprite name: %d\", self);\n"
				+ "        return " + (configuration.getSpriteNames().getGetDefinitionType() == PlaydateExportConfiguration.SpriteNamesGetDefinitionType.pointer ? "NULL" : "(MELSpriteDefinition) {}") + ";\n"
				+ "    }\n"
				+ "}\n"
				+ "\n"
				+ "LCDBitmapTable * _Nullable SpriteNameLoadBitmapTable(SpriteName self) {\n"
				+ "    const char *error = NULL;\n"
				+ "    LCDBitmapTable *table = NULL;\n"
				+ "    switch (self) {\n"
				+ t.stream()
						.map(sprite -> "    case SpriteName" + Names.normalizeName(sprite, Names::toPascalCase) + ":\n" +
										"        table = playdate->graphics->loadBitmapTable(\"sprite-" + Names.normalizeName(sprite, Names::toSnakeCase) + "\", &error);\n" +
										"        break;\n")
						.collect(Collectors.joining())
				+ "    default:\n"
				+ "        playdate->system->error(\"Unsupported sprite name: %d\", self);\n"
				+ "        return NULL;\n"
				+ "    }\n"
				+ "    if (error) {\n"
				+ "        playdate->system->error(\"Unable to load table for sprite name %d: %s\", self, error);\n"
				+ "        return NULL;\n"
				+ "    }\n"
				+ "    return table;\n"
				+ "}\n"
				).getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(List<Sprite> t) {
		return "spritenames.c";
	}

}
