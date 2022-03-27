package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class SpriteAsCodeHandler implements DataHandler<Sprite> {

	@Override
	public void write(Sprite t, OutputStream outputStream) throws IOException {
		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final String name = Names.normalizeName(t, Names::toLowerCase);
		final String pascalCasedName = Names.normalizeName(t, Names::toPascalCase);
		outputStream.write(("//\n"
				+ "// sprite" + name + ".c\n"
				+ "//\n"
				+ "// Generated by MapMaker on " + dateFormat.format(new Date()) + ".\n"
				+ "//\n"
				+ "\n"
				+ "#include \"sprite" + Names.normalizeName(t, Names::toLowerCase) + ".h\"\n"
				+ "\n"
				+ "#include \"../lib/melice.h\"\n"
				+ "\n"
				+ SpriteAsHeaderHandler.SPRITE_TYPE + " sprite" + pascalCasedName + " = {\n"
				+ "    // Type\n"
				+ "    " + t.getType() + ",\n"
				+ "    // Palette\n"
				+ "    NULL,\n"
				+ "    // Animations\n"
				+ "    (MELAnimationDefinition[]) {\n").getBytes(StandardCharsets.UTF_8));

		// TODO: Boucler sur les animations

		outputStream.write(("    }\n"
				+ "};\n"
				+ "\n"
				+ "void loadSprite" + pascalCasedName + "Palette(void) {\n"
				+ "    \n"
				+ "}\n"
				+ "\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public Sprite read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String fileNameFor(Sprite t) {
		return "sprite" + Names.normalizeName(t, Names::toPascalCase) + ".c";
	}

}
