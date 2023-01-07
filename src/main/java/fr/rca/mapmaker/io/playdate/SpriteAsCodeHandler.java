package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.SecondaryHitboxLayerPlugin;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Stream;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
@Data
@EqualsAndHashCode(callSuper = false)
public class SpriteAsCodeHandler extends CodeDataHandler<Sprite> {

	private final static double[] ANGLES = {0, 0.78, 1.57, 2.35, 3.14, 3.92, 4.71, 5.49};

	private List<String> animationNames;

	public SpriteAsCodeHandler(List<String> animationNames) {
		this.animationNames = animationNames;
	}

	@Override
	public void write(Sprite t, OutputStream outputStream) throws IOException {
		final String pascalCasedName = Names.normalizeName(t, Names::toPascalCase);
		final boolean hasScriptFile = t.getScriptFile() != null && !t.getScriptFile().trim().isEmpty();
		outputStream.write((generateHeader(t)
				+ "#include \"sprite" + Names.normalizeName(t, Names::toLowerCase) + ".h\"\n"
				+ "\n"
				+ "#include \"../lib/melice.h\"\n"
				+ "#include \"../gen/spritenames.h\"\n"
				+ (hasScriptFile ? ("#include \"../src/" + Names.toSnakeCase(t.getScriptFile()) + ".h\"\n") : "")
				+ "\n"
				+ SpriteAsHeaderHandler.SPRITE_TYPE + " sprite" + pascalCasedName + " = (" + SpriteAsHeaderHandler.SPRITE_TYPE + ") {\n"
				+ "    .name = SpriteName" + pascalCasedName + ",\n"
				+ "    .type = " + spriteType(t) + ",\n"
				+ (
					hasScriptFile
						? "    .constructor = " + Names.toPascalCase(t.getScriptFile()) + "Constructor,\n"
								+ "    .loader = " + Names.toPascalCase(t.getScriptFile()) + "Loader,\n"
						: ""
				)
				+ "    .size = {" + t.getWidth() + ", " + t.getHeight() + "},\n"
				+ "    .animations = (MELAnimationDefinition * _Nullable [" + (animationNames.size() * ANGLES.length) + "]) {\n").getBytes(StandardCharsets.UTF_8));

		final HashMap<TileLayer, Integer> indexForTile = new HashMap<>();
		for (final String animationName : animationNames) {
			final Animation animation = t.findByName(animationName);
			outputStream.write(("        // " + Names.toPascalCase(animationName) + "\n").getBytes(StandardCharsets.UTF_8));
			for (final double angle : ANGLES) {
				final List<TileLayer> frames = animation != null ? animation.getFrames(angle) : null;
				if (frames != null && !frames.isEmpty()) {
					final int frameCount = frames.size();
					outputStream.write(("        &((MELAnimationDefinition) {\n"
							+ "            .frameCount = " + frameCount + ",\n"
							+ "            .frames = (MELAnimationFrame[" + frameCount + "]) {" + framesToString(frames, indexForTile) + "},\n"
							+ "            .frequency = " + animation.getFrequency() + ",\n"
							+ "            .type = " + animationType(frameCount, animation.isLooping()) + "\n"
							+ "        }),\n").getBytes(StandardCharsets.UTF_8));
				} else {
					outputStream.write("        NULL,\n".getBytes(StandardCharsets.UTF_8));
				}
			}
		}

		outputStream.write(("    }\n"
				+ "};\n"
				+ "\n"
				+ "void loadSprite" + pascalCasedName + "Palette(void) {\n"
				+ "    if (sprite" + pascalCasedName + ".palette) {\n"
				+ "        return;\n"
				+ "    }\n"
				+ "    const char *error = NULL;\n"
				+ "    sprite" + pascalCasedName + ".palette = playdate->graphics->loadBitmapTable(\"sprite-" + Names.normalizeName(t, Names::toSnakeCase) + "\", &error);\n"
				+ "    if (error) {\n"
				+ "        playdate->system->error(\"Unable to load bitmap table of sprite " + pascalCasedName + ": %s\", error);\n"
				+ "    }\n"
				+ "}\n"
				+ "\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(Sprite t) {
		return "sprite" + Names.normalizeName(t, Names::toLowerCase) + ".c";
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

	private static boolean hasAttackHitbox(Sprite sprite) {
		return sprite.getAnimations().stream()
				.flatMap(animation -> animation.getFrames().values().stream())
				.flatMap(frames -> frames != null ? frames.stream() : Stream.empty())
				.anyMatch(frame -> !isNullOrEmpty(frame.getPlugin(SecondaryHitboxLayerPlugin.class).getHitbox()));
	}

	private static String spriteType(Sprite sprite) {
		switch (sprite.getType()) {
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
			return hasAttackHitbox(sprite)
					? "MELSpriteTypeEnemyWithAttackHitbox"
					: "MELSpriteTypeEnemy";
		case 6:
			return "MELSpriteTypeCollidable";
		case 7:
			return "MELSpriteTypeFont";
		default:
				throw new UnsupportedOperationException("Unsupported sprite type: " + sprite.getType());
		}
	}

	private static String framesToString(List<TileLayer> frames, HashMap<TileLayer, Integer> indexForTile) {
		StringBuilder stringBuilder = new StringBuilder();
		Integer frameIndex = null;
		for (final TileLayer frame : frames) {
			if (frameIndex != null) {
				stringBuilder.append(", ");
			}
			frameIndex = indexForTile.get(frame);
			if (frameIndex == null) {
				frameIndex = indexForTile.size();
				indexForTile.put(frame, frameIndex);
			}
			stringBuilder.append("{ .atlasIndex = ")
					.append(frameIndex);
			appendHitbox(stringBuilder, frame, "hitbox", HitboxLayerPlugin.class);
			appendHitbox(stringBuilder, frame, "attackHitbox", SecondaryHitboxLayerPlugin.class);
			stringBuilder.append(" }");
		}
		return stringBuilder.toString();
	}

	private static <T extends HitboxLayerPlugin> void appendHitbox(StringBuilder stringBuilder, TileLayer frame, String name, Class<T> hitboxClass) {
		T hitboxPlugin = frame.getPlugin(hitboxClass);
		if (hitboxPlugin != null && !isNullOrEmpty(hitboxPlugin.getHitbox())) {
			stringBuilder.append(", .").append(name).append(" = {");
			final Rectangle hitbox = hitboxPlugin.getHitbox();
			stringBuilder
					.append('{')
					.append((int) hitbox.getX() - frame.getWidth() / 2 + hitbox.getWidth() / 2).append(", ")
					.append((int) hitbox.getY() - frame.getHeight() / 2 + hitbox.getHeight() / 2).append("}, {")
					.append((int) hitbox.getWidth()).append(", ")
					.append((int) hitbox.getHeight())
					.append("}}");
		}
	}

	private static boolean isNullOrEmpty(Rectangle rectangle) {
		return rectangle == null || (rectangle.getWidth() == 0 || rectangle.getHeight() == 0);
	}

}
