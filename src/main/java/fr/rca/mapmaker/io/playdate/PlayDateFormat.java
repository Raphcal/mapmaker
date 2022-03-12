package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.mkz.BufferedImageDataHandler;
import fr.rca.mapmaker.io.mkz.InstanceDataHandler;
import fr.rca.mapmaker.io.mkz.TileMapDataHandler;
import fr.rca.mapmaker.model.map.MapAndInstances;
import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class PlayDateFormat extends AbstractFormat {

	private static final String EXTENSION = ".playdate.zip";

	public PlayDateFormat() {
		super(EXTENSION, SupportedOperation.SAVE);

		addHandler(EditableImagePalette.class, new EditableImagePaletteHandler());
		addHandler(BufferedImage.class, new BufferedImageDataHandler());
		addHandler(TileMap.class, new TileMapDataHandler(this));
		addHandler(Instance.class, new InstanceDataHandler());

		// Handlers du format interne.
		addHandler(Color.class, new fr.rca.mapmaker.io.internal.ColorDataHandler());
		addHandler(TileLayer.class, new fr.rca.mapmaker.io.internal.LayerDataHandler(this));
		addHandler(ScrollRate.class, new fr.rca.mapmaker.io.internal.ScrollRateDataHandler());
		addHandler(Rectangle.class, new fr.rca.mapmaker.io.internal.RectangleDataHandler());
	}

	@Override
	public void saveProject(Project project, File file) {
		try (ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			final List<Palette> palettes = project.getPalettes();
			for(int index = 0; index < palettes.size(); index++) {
				final Palette palette = palettes.get(index);
				if (palette instanceof EditableImagePalette) {
					outputStream.putNextEntry(new ZipEntry("palette" + index + ".data"));
					write(palette, outputStream);

					outputStream.putNextEntry(new ZipEntry("palette" + index + "-table-" + palette.getTileSize() + '-' + palette.getTileSize() + ".png"));
					write(renderPalette((EditableImagePalette) palette), outputStream);
				}
			}

			final List<MapAndInstances> maps = project.getMaps();
			for(int index = 0; index < maps.size(); index++) {
				outputStream.putNextEntry(new ZipEntry("map" + index + ".data"));
				final MapAndInstances mapAndInstances = maps.get(index);
				write(mapAndInstances.getTileMap(), outputStream);
			}

			final List<Sprite> sprites = project.getSprites();
			for(int index = 0; index < sprites.size(); index++) {
				final Sprite sprite = sprites.get(index);
				BufferedImage spriteImage = renderSprite(sprite);
				if (spriteImage != null) {
					outputStream.putNextEntry(new ZipEntry("sprite" + index + "-table-" + sprite.getWidth() + '-' + sprite.getHeight() + ".png"));
					write(spriteImage, outputStream);
				}
			}
		} catch (IOException ex) {
			Exceptions.showStackTrace(ex, null);
		}
	}

	public static BufferedImage renderPalette(EditableImagePalette p) {
		int tileSize = p.getTileSize();

		final int tileCountPerLine = (int) Math.ceil(Math.sqrt(p.size()));
		final int width, height;
		width = height = tileSize * tileCountPerLine;

		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setBackground(new Color(0, 0, 0, 0));

		for(int index = 0; index < p.size(); index++) {
			int x = (index % tileCountPerLine) * tileSize;
			int y = (index / tileCountPerLine) * tileSize;
			p.paintTile(graphics, index, x, y, tileSize);
		}

		graphics.dispose();
		return image;
	}

	public static BufferedImage renderSprite(Sprite sprite) {
		int imageWidth = 0;
		int imageHeight = 0;
		for (final Animation animation : sprite.getAnimations()) {
			final List<TileLayer> frames = animation.getFrames(0);
			if (frames == null) {
				continue;
			}
			imageHeight++;
			if (frames.size() > imageWidth) {
				imageWidth = frames.size();
			}
		}

		if (imageWidth == 0 || imageHeight == 0) {
			return null;
		}

		int spriteWidth = sprite.getWidth();
		int spriteHeight = sprite.getHeight();

		final BufferedImage image = new BufferedImage(imageWidth * spriteWidth, imageHeight * spriteHeight, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setBackground(new Color(0, 0, 0, 0));

		final ColorPalette palette = sprite.getPalette();

		int originY = 0;
		for (final Animation animation : Animation.getDefaultAnimations()) {
			final List<TileLayer> frames = sprite.get(animation.getName()).getFrames(0);
			if (frames == null) {
				continue;
			}
			int originX = 0;
			for (TileLayer frame : frames) {
				for(int y = 0; y < frame.getHeight(); y++) {
					for(int x = 0; x < frame.getWidth(); x++) {
						palette.paintTile(graphics, frame.getTile(x, y), originX + x, originY + y, 1);
					}
				}
				originX += spriteWidth;
			}
			originY += spriteHeight;
		}

		graphics.dispose();
		return image;
	}
}
