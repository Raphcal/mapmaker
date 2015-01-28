package fr.rca.mapmaker.model.palette;

import java.awt.Color;
import java.awt.Graphics;

public class AlphaColorPalette extends ColorPalette {

	private static final int[] ALPHAS = {255, 224, 192, 160, 128, 96, 64, 32};
	private static final int MASK = 1000;
	
	private int selectedAlpha = 0;
	
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
		final int colorIndex = tile % MASK;
		final int alpha = tile / MASK;

		if(colorIndex >= 0 && colorIndex < getColors().length) {
			final Color baseColor = getColor(colorIndex);

			final Color color = new Color(baseColor.getRed(), baseColor.getGreen(),
					baseColor.getBlue(), ALPHAS[alpha]);

			g.setColor(color);
			g.fillRect(x, y, size, size);
		}
	}

	public void setSelectedAlpha(int alphaIndex) {
		selectedAlpha = alphaIndex;
	}

	@Override
	public int getSelectedTile() {
		return super.getSelectedTile() + MASK * selectedAlpha;
	}
	
	public static AlphaColorPalette getDefaultColorPalette() {
		return new AlphaColorPalette(ColorPalette.getDefaultColors());
	}
	
	public static ColorPalette getAlphaPalette() {
		final Color[] colors = new Color[ALPHAS.length];
		for(int index = 0; index < ALPHAS.length; index++) {
			colors[index] = new Color(ALPHAS[index], ALPHAS[index], ALPHAS[index]);
		}
		return new ColorPalette(colors);
	}
}
