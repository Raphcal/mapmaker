package fr.rca.mapmaker.model.sprite;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public enum Direction {
	LEFT, RIGHT, UP, DOWN;
	
	public static @NotNull Direction from(@Nullable Double value) {
		final int index = value != null && value >= 0 && value < values().length
				? value.intValue()
				: 0;
		return values()[index];
	}
}
