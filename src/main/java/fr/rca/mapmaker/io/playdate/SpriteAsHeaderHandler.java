package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class SpriteAsHeaderHandler extends CodeDataHandler<Sprite> {

	public static final String SPRITE_TYPE = "MELSpriteDefinition";

	@Override
	public void write(Sprite t, OutputStream outputStream) throws IOException {
		final String name = Names.normalizeName(t, Names::toLowerCase);
		final String pascalCasedName = Names.normalizeName(t, Names::toPascalCase);
		outputStream.write((generateHeader(t)
				+ "#ifndef sprite" + name + "_h\n"
				+ "#define sprite" + name + "_h\n"
				+ "\n"
				+ "#include <stdio.h>\n"
				+ "\n"
				+ "#include \"../lib/melice.h\"\n"
				+ "\n"
				+ "extern " + SPRITE_TYPE + " sprite" + pascalCasedName + ";\n"
				+ "\n"
				+ "void loadSprite" + pascalCasedName + "Palette(void);\n"
				+ "\n"
				+ "#endif /* sprite" + name + "_h */\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(Sprite t) {
		return "sprite" + Names.normalizeName(t, Names::toLowerCase) + ".h";
	}

}
