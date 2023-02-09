package fr.rca.mapmaker.io.playdate;

import lombok.Data;

/**
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
@Data
public class PlaydateExportConfiguration {
	private SpriteNames spriteNames = new SpriteNames();

	@Data
	public static class SpriteNames {
		private SpriteNamesGetDefinitionType getDefinitionType = SpriteNamesGetDefinitionType.struct;
	}

	public static enum SpriteNamesGetDefinitionType {
		struct,
		pointer;
	}
}
