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
public class SpriteDefinitionsAsHeaderHandler extends CodeDataHandler<List<Sprite>> {

	@Override
	public void write(List<Sprite> t, OutputStream outputStream) throws IOException {
		outputStream.write((generateHeader(t)
				+ "#ifndef spritenames_h\n"
				+ "#define spritenames_h\n"
				+ "\n"
				+ "#include \"pd_api.h\"\n"
				+ "#include \"../lib/melice.h\"\n"
				+ "\n"
				+ "typedef enum {\n"
				+ t.stream()
						.map(sprite -> "    SpriteName" + Names.normalizeName(sprite, Names::toPascalCase) + ",\n")
						.collect(Collectors.joining())
				+ "} SpriteName;\n"
				+ "\n"
				+ "MELConstSpriteDefinition SpriteNameGetDefinition(SpriteName self);\n"
				+ "LCDBitmapTable * _Nullable SpriteNameLoadBitmapTable(SpriteName self);\n"
				+ "\n"
				+ "#endif /* spritenames_h */\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(List<Sprite> t) {
		return "spritenames.h";
	}

}
