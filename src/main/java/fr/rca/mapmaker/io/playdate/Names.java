package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.MapAndInstances;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.util.List;
import java.util.function.Function;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public final class Names {
	private static final String PALETTE = "palette";
	private static final String MAP = "map";

	private Names() {}

	public static @NotNull String normalizeName(@NotNull Palette palette, @NotNull Function<String, String> namingStyle) {
		if (palette instanceof PaletteReference) {
			final PaletteReference reference = ((PaletteReference) palette);
			palette = reference.getProject().getPalette(reference.getPaletteIndex());
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
				final List<MapAndInstances> maps = project.getMaps();
				for (int index = 0; index < maps.size(); index++) {
					if (tileMap == maps.get(index).getTileMap()) {
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

	/**
     * Converti la chaîne donnée en camelCase.
     *
     * @param source Chaîne à convertir.
     * @return La chaîne convertie.
     */
    public static String toCamelCase(String source) {
        final StringBuilder builder = new StringBuilder();
        boolean toUppercase = false;
        for (char c : source.toCharArray()) {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                toUppercase = true;
            } else if (toUppercase) {
                toUppercase = false;
                builder.append(Character.toUpperCase(c));
            } else {
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
    }

	/**
     * Converti la chaîne donnée en camelCase.
     *
     * @param source Chaîne à convertir.
     * @return La chaîne convertie.
     */
    public static String toSnakeCase(String source) {
        final StringBuilder builder = new StringBuilder();
        boolean toUppercase = false;
        for (char c : source.toCharArray()) {
            if (!Character.isAlphabetic(c) && !Character.isDigit(c)) {
                toUppercase = true;
            } else if (toUppercase) {
                toUppercase = false;
                builder.append(Character.toUpperCase(c));
            } else {
                builder.append(Character.toLowerCase(c));
            }
        }
        return builder.toString();
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
