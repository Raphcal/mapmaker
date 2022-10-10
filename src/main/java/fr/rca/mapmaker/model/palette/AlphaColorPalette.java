package fr.rca.mapmaker.model.palette;

import java.awt.Color;
import java.awt.Graphics;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class AlphaColorPalette extends ColorPalette {

	private static final Logger LOGGER = LoggerFactory.getLogger(AlphaColorPalette.class);

	private static final int[] ALPHAS = {255, 224, 192, 160, 128, 96, 64, 32};
	private static final int MASK = 1000;

	private int selectedAlpha = 0;

	public AlphaColorPalette() {
	}

	public AlphaColorPalette(int length) {
		super(length);
	}

	public AlphaColorPalette(Color... colors) {
		super(colors);
	}

	@Override
	public boolean isEditable() {
		return false;
	}

	@Override
	public void paintTile(Graphics g, int tile, int x, int y, int size) {
		final Color color = getColor(tile);
		if (color != null) {
			g.setColor(color);
			g.fillRect(x, y, size, size);
		}
	}

	public void setSelectedAlpha(int alphaIndex) {
		selectedAlpha = alphaIndex;
	}

	@Override
	public int getSelectedTile() {
		return getTile(super.getSelectedTile(), selectedAlpha);
	}

	@Override
	public Color getColor(int index) {
		final int colorIndex = index % MASK;
		final int alpha = index / MASK;

		if (colorIndex < 0 || colorIndex >= getColors().length || alpha < 0 || alpha >= ALPHAS.length) {
			return null;
		}

		Color baseColor = super.getColor(colorIndex);
		if (baseColor == null) {
			LOGGER.error("Base color #" + colorIndex + " is null");
			baseColor = new Color(0);
		}

		return new Color(baseColor.getRed(), baseColor.getGreen(), baseColor.getBlue(), ALPHAS[alpha]);
	}

	public static int getTile(int tile, int alpha) {
		return tile + MASK * alpha;
	}

	public static int getAlphaFromTile(int tile) {
		return tile / MASK;
	}

	public static int getTileFromTile(int tile) {
		return tile % MASK;
	}

	public static AlphaColorPalette getDefaultColorPalette() {
		return new AlphaColorPalette(ColorPalette.getDefaultColors());
	}

	public static Color[] getAlphaColors() {
		final Color[] colors = new Color[ALPHAS.length];
		for (int index = 0; index < ALPHAS.length; index++) {
			colors[index] = new Color(ALPHAS[index], ALPHAS[index], ALPHAS[index]);
		}
		return colors;
	}

	public static ColorPalette getAlphaPalette() {
		return new ColorPalette(getAlphaColors());
	}
}
