package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.SecondaryHitboxLayerPlugin;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Color;
import java.util.stream.IntStream;

/**
 * Permet d'associer une à plusieurs hitbox secondaires.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class SecondaryHitboxTool extends AbstractHitboxTool<SecondaryHitboxLayerPlugin> {
	private Integer colorIndex;
	private ColorPalette colorPalette;

	public SecondaryHitboxTool(Grid grid) {
		super(grid);
		final Palette palette = grid.getTileMap().getPalette();
		if (palette instanceof ColorPalette) {
			this.colorPalette = (ColorPalette) palette;
		}
	}

	@Override
	public String getPluginName() {
		return SecondaryHitboxLayerPlugin.NAME;
	}

	@Override
	public int getHitboxColor() {
		if (colorIndex == null && colorPalette != null) {
			colorIndex = IntStream.range(0, colorPalette.getColors().length)
					.reduce(this::mostRedColor)
					.getAsInt();
		} else if (colorIndex == null) {
			colorIndex = 1;
		}
		return colorIndex;
	}

	private int mostRedColor(int lhs, int rhs) {
		final Color[] colors = colorPalette.getColors();
		final Color leftColor = colors[lhs];
		final Color rightColor = colors[rhs];
		if (leftColor.getRed() > rightColor.getRed()) {
			return lhs;
		} else if (leftColor.getRed() < rightColor.getRed()) {
			return rhs;
		} else if (leftColor.getGreen() + leftColor.getBlue() < rightColor.getGreen() + rightColor.getBlue()) {
			return lhs;
		} else {
			return rhs;
		}
	}

}
