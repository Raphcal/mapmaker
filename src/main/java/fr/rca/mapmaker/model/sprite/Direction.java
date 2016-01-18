package fr.rca.mapmaker.model.sprite;

import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public enum Direction {
	LEFT, RIGHT;
	
	public static @NotNull Direction from(@Nullable Double value) {
		if (value != null && value == 0.0) {
			return LEFT;
		} else {
			return RIGHT;
		} 
	}
}
