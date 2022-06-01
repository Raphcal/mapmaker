package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class SpriteAsCodeHandler extends CodeDataHandler<Sprite> {

	private final static double[] ANGLES = {0, 0.78, 1.57, 2.35, 3.14, 3.92, 4.71, 5.49};

	private List<String> animationNames;

	public SpriteAsCodeHandler(List<String> animationNames) {
		this.animationNames = animationNames;
	}

	@Override
	public void write(Sprite t, OutputStream outputStream) throws IOException {
		final String pascalCasedName = Names.normalizeName(t, Names::toPascalCase);
		outputStream.write((generateHeader(t)
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
				+ "    (MELAnimationDefinition * _Nullable [" + (animationNames.size() * ANGLES.length) + "]) {\n").getBytes(StandardCharsets.UTF_8));

		int frameIndex = 0;
		for (final String animationName : animationNames) {
			final Animation animation = t.findByName(animationName);
			outputStream.write(("        // " + Names.toPascalCase(animationName) + "\n").getBytes(StandardCharsets.UTF_8));
			for (final double angle : ANGLES) {
				final List<TileLayer> frames = animation != null ? animation.getFrames(angle) : null;
				if (frames != null && !frames.isEmpty()) {
					final int frameCount = frames.size();
					outputStream.write(("        &((MELAnimationDefinition) {\n"
							+ "            // Frame count\n"
							+ "            " + frameCount + ",\n"
							+ "            // Frames\n"
							+ "            (unsigned int[" + frameCount + "]) {" + range(frameIndex, frameCount) + "},\n"
							+ "            // Frequency\n"
							+ "            " + animation.getFrequency() + ",\n"
							+ "            // Type\n"
							+ "            " + animationType(frameCount, animation.isLooping()) + "\n"
							+ "        }),\n").getBytes(StandardCharsets.UTF_8));
					frameIndex += frameCount;
				} else {
					outputStream.write("        NULL,\n".getBytes(StandardCharsets.UTF_8));
				}
			}
		}

		outputStream.write(("    }\n"
				+ "};\n"
				+ "\n"
				+ "void loadSprite" + pascalCasedName + "Palette(void) {\n"
				+ "    const char *error = NULL;\n"
				+ "    sprite" + pascalCasedName + ".palette = playdate->graphics->loadBitmapTable(\"sprite-" + Names.normalizeName(t, Names::toSnakeCase) + "\", &error);\n"
				+ "    if (error) {\n"
				+ "        playdate->system->logToConsole(\"Unable to load bitmap table of sprite " + pascalCasedName + ": %s\", error);\n"
				+ "    }\n"
				+ "}\n"
				+ "\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public Sprite read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String fileNameFor(Sprite t) {
		return "sprite" + Names.normalizeName(t, Names::toLowerCase) + ".c";
	}

	public void setAnimationNames(List<String> animationNames) {
		this.animationNames = animationNames;
	}

	private static String range(int from, int length) {
		StringBuilder stringBuilder = new StringBuilder();
		final int max = from + length;
		for (int index = from; index < max; index++) {
			stringBuilder.append(index).append(", ");
		}
		final int resultLength = stringBuilder.length();
		if (resultLength > 2) {
			stringBuilder.setLength(resultLength - 2);
		}
		return stringBuilder.toString();
	}

	private static String animationType(int frameCount, boolean looping) {
		if (frameCount == 1) {
			return "MELAnimationTypeSingleFrame";
		} else if (looping) {
			return "MELAnimationTypeLooping";
		} else if (frameCount > 1) {
			return "MELAnimationTypePlayOnce";
		} else {
			return "MELAnimationTypeNone";
		}
	}

}
