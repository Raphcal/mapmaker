package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Gère le formattage des noms.
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public final class Names {
	private static final String PALETTE = "palette";
	private static final String MAP = "map";
	private static final String SPRITE = "sprite";

	private Names() {}

	public static @NotNull String normalizeName(@NotNull Palette palette, @NotNull Function<String, String> namingStyle) {
		if (palette instanceof PaletteReference) {
			palette = ((PaletteReference) palette).getPalette();
		}
		String name = palette.toString();
		if (name.toLowerCase().startsWith(PALETTE)) {
			name = name.substring(PALETTE.length(), name.length());
		}
		name = namingStyle.apply(name.trim());
		return name;
	}

	public static @Nullable String normalizeName(@NotNull TileMap tileMap, @NotNull Function<String, String> namingStyle) {
		String name = tileMap.getName();
		if (name == null) {
			Palette palette = tileMap.getPalette();
			if (palette instanceof PaletteReference) {
				Project project = ((PaletteReference) palette).getProject();
				final List<TileMap> maps = project.getMaps();
				for (int index = 0; index < maps.size(); index++) {
					if (tileMap == maps.get(index)) {
						return Integer.toString(index);
					}
				}
			}
			return null;
		}
		if (name.toLowerCase().startsWith(MAP)) {
			name = name.substring(MAP.length(), name.length());
		}
		name = namingStyle.apply(name.trim());
		return name;
	}

	public static @Nullable String normalizeName(@NotNull Sprite sprite, @NotNull Function<String, String> namingStyle) {
		String name = sprite.getName();
		if (name == null) {
			return null;
		}
		if (name.toLowerCase().startsWith(SPRITE)) {
			name = name.substring(SPRITE.length(), name.length());
		}
		name = namingStyle.apply(name.trim());
		return name;
	}

	/**
     * Converti la chaîne donnée en camelCase.
     *
     * @param source Chaîne à convertir.
     * @return La chaîne convertie.
     */
    public static String toCamelCase(String source) {
        final StringBuilder builder = new StringBuilder();
        Boolean toUppercase = Boolean.FALSE;
        for (char c : source.toCharArray()) {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                toUppercase = Boolean.TRUE;
            } else if (toUppercase == Boolean.TRUE || (toUppercase == null && Character.isUpperCase(c))) {
                toUppercase = Boolean.FALSE;
                builder.append(Character.toUpperCase(c));
            } else {
                toUppercase = null;
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

	/**
     * Converti la chaîne donnée en PascalCase.
     *
     * @param source Chaîne à convertir.
     * @return La chaîne convertie.
     */
    public static String toPascalCase(String source) {
        final StringBuilder builder = new StringBuilder();
        Boolean toUppercase = Boolean.TRUE;
        for (char c : source.toCharArray()) {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                toUppercase = Boolean.TRUE;
            } else if (toUppercase == Boolean.TRUE || (toUppercase == null && Character.isUpperCase(c))) {
                toUppercase = Boolean.FALSE;
                builder.append(Character.toUpperCase(c));
            } else {
                toUppercase = null;
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

	/**
     * Converti la chaîne donnée en snake_case.
     *
     * @param source Chaîne à convertir.
     * @return La chaîne convertie.
     */
    public static String toSnakeCase(String source) {
        return source.toLowerCase().replaceAll("[^a-z0-9]+", "_");
    }

	public static @NotNull String toLowerCase(@NotNull String source) {
		return source.toLowerCase().replaceAll("[^a-z0-9]+", "");
	}

	public static @NotNull String capitalize(@NotNull String source) {
		return source.length() > 1
				? Character.toUpperCase(source.charAt(0)) + source.substring(1)
				: source.toUpperCase();
	}
}
