package fr.rca.mapmaker.util;

import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Color;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import javax.swing.JFrame;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */

public class Dithering {
	private static final Logger LOGGER = LoggerFactory.getLogger(Dithering.class);
	public static boolean usePerceivedLightness = false;


	public static TileLayer dither(DataLayer layer, ColorPalette palette) {
		final int blackColor = palette.indexOf(Color.BLACK);
		final int whiteColor = palette.indexOf(Color.WHITE);
		final int transparentColor = -1;

		final HashMap<Integer, Double> lightnessMap = new HashMap<>();

		final int height = layer.getHeight();
		final int width = layer.getWidth();
		final int length = width * height;

		final int[] bwPixels = new int[length];
		Arrays.fill(bwPixels, transparentColor);

		for (int y = 0; y < height; y++) {
			int offset = y * width;

			for (int x = 0; x < width; x++) {
				int pixel = layer.getTile(x, y);
				if (pixel < 0) {
					continue;
				}
				if (pixel == blackColor || pixel == whiteColor) {
					bwPixels[offset + x] = pixel;
					continue;
				}
				final double lightness = lightnessMap.computeIfAbsent(pixel,
						usePerceivedLightness
							? key -> perceivedLightnessOf(palette.getColor(key))
							: key -> luminanceOf(palette.getColor(key)) * 100.0);
				// Faire x traits noirs sur 16 en fonction de la brillance
				// noir si position dans la grille < 16 * (100 - brillance) /100
				final int gridSize = 4;
				int blackLine = (int) (gridSize - Math.floor(gridSize * lightness / 100.0));
				bwPixels[offset + x] = blackLine > 0 && blackLine > ((y + x) % gridSize)
						? blackColor
						: whiteColor;
			}
		}

		return new TileLayer(width, height, bwPixels);
	}

	public static int rcaBalanceLines(final double lightness, int y, final int whiteColor, final int blackColor) {
		if (lightness >= 95) {
			return whiteColor;
		} else if (lightness < 5) {
			return blackColor;
		} else if (lightness >= 50) {
			// Plus de traits blancs que de noirs.
			int blackLineCount = (int) Math.round(4.0 * (lightness - 50.0) / 50.0);
			return blackLineCount > 0 && (y % blackLineCount == 0)
					? blackColor
					: whiteColor;
		} else {
			// Plus de traits noirs que de blancs.
			int whiteLineCount = (int) Math.round(4.0 * lightness / 50.0);
			return whiteLineCount > 0 && (y % whiteLineCount == 0)
					? whiteColor
					: blackColor;
		}
	}

	/**
	 * L* is a value from 0 (black) to 100 (white) where 50 is the perceptual "middle grey".
	 * L* = 50 is the equivalent of Y = 18.4, or in other words an 18% grey card,
	 * representing the middle of a photographic exposure (Ansel Adams zone V).
	 * @param color
	 * @return 
	 */
	private static double perceivedLightnessOf(Color color) {
		double luminance = luminanceOf(color);
		// The CIE standard states 0.008856 but 216/24389 is the intent for 0.008856451679036
		double lightness = luminance <= (216.0 / 24389.0)
				// The CIE standard states 903.3, but 24389/27 is the intent, making 903.296296296296296
				? luminance * (24389.0 / 27.0)
				: Math.pow(luminance, 1.0 / 3.0) * 116.0 - 16.0;
		LOGGER.trace("Color: " + color + ", luminance: " + luminance + ", lightness: " + lightness);
		return lightness;
	}

	private static double luminanceOf(Color color) {
		double luminance =  0.2126 * vRGBToLinear(color.getRed())
				+ 0.7152 * vRGBToLinear(color.getGreen())
				+ 0.0722 * vRGBToLinear(color.getBlue());
		int grid = (int) (5 - Math.floor(5 * luminance));
		LOGGER.trace("Color: " + color + ", luminance: " + luminance + ", grid: " + grid);
		return luminance;
	}

	private static double vRGBToLinear(int colorChannel) {
		double sRGBValue = colorChannel / 255.0;
        // Send this function a decimal sRGB gamma encoded color value
        // between 0.0 and 1.0, and it returns a linearized value.

		return sRGBValue <= 0.04045
				? sRGBValue / 12.92
				: Math.pow(((sRGBValue + 0.055)/1.055), 2.4);
	}

	public static void main(String[] args) throws Exception {
		InternalFormat format = new InternalFormat();
		format.setVersion(InternalFormat.VERSION_15);
		InputStream inputStream = CleanEdge.class.getResourceAsStream("/test-rotate.mmk");
		format.readHeader(inputStream);
		Project project = format.getHandler(Project.class).read(inputStream);

		Grid grid = new Grid();
		grid.setZoom(1.0);
		TileLayer layer = project.getSprites().get(1).get(Animation.ANIMATION_NAMES.get(0)).getFrames(0.0).get(0);
		AlphaColorPalette palette = AlphaColorPalette.getDefaultColorPalette();
		layer = Dithering.dither(layer, AlphaColorPalette.getDefaultColorPalette());
		grid.setTileMap(new TileMap(layer, palette));

		JFrame frame = new JFrame();
		frame.getContentPane().add(grid);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}

}
