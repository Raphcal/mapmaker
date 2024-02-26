package fr.rca.mapmaker.io.playdate;

import lombok.Data;

/**
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
@Data
public class PlaydateExportConfiguration {
	private SpriteNames spriteNames = new SpriteNames();
	private Maps maps = new Maps();
	private Instances instances = new Instances();
	/**
	 * Active le dithering des couleurs des sprites (mais pas top actuellement).
	 */
	private Boolean enableDithering;

	/**
	 * Exporte les sprites dans un dossier "sprites" et les cartes dans un
	 * dossier "maps".
	 */
	private Boolean createDirectories;

	@Data
	public static class SpriteNames {
		private SpriteNamesGetDefinitionType getDefinitionType = SpriteNamesGetDefinitionType.struct;
	}

	@Data
	public static class Maps {
		/**
		 * Exporte un grand bitmap pour chaque couche des cartes plutôt qu'exporter
		 * la palette et la grille de tuiles.
		 */
		private Boolean flattenLayers;

		/**
		 * Écrit la taille des tuiles au début des cartes.
		 */
		private Boolean writeTileSize;
	}

	@Data
	public static class Instances {
		private InstancesOrder order;
	}

	public static enum SpriteNamesGetDefinitionType {
		struct,
		pointer;
	}

	public static enum InstancesOrder {
		x,
		zIndex;
	}
}
