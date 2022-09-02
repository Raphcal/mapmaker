package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Rectangle;
import java.io.IOException;
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
				+ "    " + spriteType(t.getType()) + ",\n"
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
							+ "            (MELAnimationFrame[" + frameCount + "]) {" + framesToString(frames, frameIndex) + "},\n"
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

	private static String spriteType(int type) {
		switch (type) {
		case 0:
			return "MELSpriteTypeDecor";
		case 1:
			return "MELSpriteTypePlayer";
		case 2:
			return "MELSpriteTypePlatform";
		case 3:
			return "MELSpriteTypeBonus";
		case 4:
			return "MELSpriteTypeDestructible";
		case 5:
			return "MELSpriteTypeEnemy";
		case 6:
			return "MELSpriteTypeCollidable";
		case 7:
			return "MELSpriteTypeFont";
		default:
				throw new UnsupportedOperationException("Unsupported sprite type: " + type);
		}
	}

	private static String framesToString(List<TileLayer> frames, int frameIndex) {
		StringBuilder stringBuilder = new StringBuilder();
		for (final TileLayer frame : frames) {
			stringBuilder.append("{")
					.append(frameIndex++)
					.append(", (PDRect) {");
			HitboxLayerPlugin hitboxPlugin = frame.getPlugin(HitboxLayerPlugin.class);
			if (hitboxPlugin != null && !isNullOrEmpty(hitboxPlugin.getHitbox())) {
				final Rectangle hitbox = hitboxPlugin.getHitbox();
				stringBuilder
						.append((int) hitbox.getX()).append(", ")
						.append((int) hitbox.getY()).append(", ")
						.append((int) hitbox.getWidth()).append(", ")
						.append((int) hitbox.getHeight());
			}
			stringBuilder.append("}},");
		}
		return stringBuilder.toString();
	}

	private static boolean isNullOrEmpty(Rectangle rectangle) {
		return rectangle == null || (rectangle.getWidth() == 0 || rectangle.getHeight() == 0);
	}

}
