package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.mkz.BufferedImageDataHandler;
import fr.rca.mapmaker.io.mkz.InstanceDataHandler;
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
public class PlaydateFormat extends AbstractFormat {

	private static final String EXTENSION = ".playdate.zip";

	private static final boolean WRITE_MAP_AS_CODE = false;

	public PlaydateFormat() {
		super(EXTENSION, SupportedOperation.SAVE);

		addHandler(EditableImagePalette.class, new EditableImagePaletteHandler());
		addHandler(BufferedImage.class, new BufferedImageDataHandler());
		addHandler(TileMap.class, new TileMapHandler());
		addHandler(Instance.class, new InstanceDataHandler());

		// Handlers du format interne.
		addHandler(ScrollRate.class, new fr.rca.mapmaker.io.internal.ScrollRateDataHandler());
		addHandler(Rectangle.class, new fr.rca.mapmaker.io.internal.RectangleDataHandler());
	}

	@Override
	public void saveProject(Project project, File file) {
		try (ZipOutputStream outputStream = new ZipOutputStream(new BufferedOutputStream(new FileOutputStream(file)))) {
			final PaletteAsHeaderHandler paletteAsHeaderHandler = new PaletteAsHeaderHandler();
			final PaletteAsCodeHandler paletteAsCodeHandler = new PaletteAsCodeHandler();

			final List<Palette> palettes = project.getPalettes();
			for(int index = 0; index < palettes.size(); index++) {
				final Palette palette = palettes.get(index);
				if (palette instanceof EditableImagePalette) {
					outputStream.putNextEntry(new ZipEntry("palette" + index + ".data"));
					write(palette, outputStream);

					outputStream.putNextEntry(new ZipEntry("palette" + index + "-table-" + palette.getTileSize() + '-' + palette.getTileSize() + ".png"));
					write(renderPalette((EditableImagePalette) palette), outputStream);

					outputStream.putNextEntry(new ZipEntry(paletteAsHeaderHandler.fileNameFor(palette)));
					paletteAsHeaderHandler.write(palette, outputStream);

					outputStream.putNextEntry(new ZipEntry(paletteAsCodeHandler.fileNameFor(palette)));
					paletteAsCodeHandler.write(palette, outputStream);
				}
			}

			final TileMapAsHeaderHandler tileMapAsHeaderHandler = new TileMapAsHeaderHandler();
			final TileMapAsCodeHandler tileMapAsCodeHandler = new TileMapAsCodeHandler();

			final List<MapAndInstances> maps = project.getMaps();
			for(int index = 0; index < maps.size(); index++) {
				outputStream.putNextEntry(new ZipEntry("map" + index + ".data"));
				final MapAndInstances mapAndInstances = maps.get(index);
				final TileMap tileMap = mapAndInstances.getTileMap();
				write(tileMap, outputStream);

				if (WRITE_MAP_AS_CODE && Names.normalizeName(tileMap, Names::toSnakeCase) != null) {
					outputStream.putNextEntry(new ZipEntry(tileMapAsHeaderHandler.fileNameFor(tileMap)));
					tileMapAsHeaderHandler.write(tileMap, outputStream);

					outputStream.putNextEntry(new ZipEntry(tileMapAsCodeHandler.fileNameFor(tileMap)));
					tileMapAsCodeHandler.write(tileMap, outputStream);
				}
			}

			final AnimationNameAsHeaderHandler animationNameAsHeaderHandler = new AnimationNameAsHeaderHandler();
			outputStream.putNextEntry(new ZipEntry(animationNameAsHeaderHandler.fileNameFor(project.getAnimationNames())));
			animationNameAsHeaderHandler.write(project.getAnimationNames(), outputStream);

			final List<Sprite> sprites = project.getSprites();
			for(int index = 0; index < sprites.size(); index++) {
				final Sprite sprite = sprites.get(index);
				BufferedImage spriteImage = renderSprite(sprite, project.getAnimationNames());
				if (spriteImage != null) {
					outputStream.putNextEntry(new ZipEntry("sprite-" + Names.normalizeName(sprite, Names::toSnakeCase) + "-table-" + sprite.getWidth() + '-' + sprite.getHeight() + ".png"));
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

	public static BufferedImage renderSprite(Sprite sprite, List<String> animationNames) {
		int frameCount = 0;
		for (final String animationName : animationNames) {
			final Animation animation = sprite.findByName(animationName);
			if (animation == null) {
				continue;
			}
			for (double angle : animation.getAnglesWithValue()) {
				final List<TileLayer> frames = animation.getFrames(angle);
				frameCount += frames.size();
			}
		}

		if (frameCount == 0) {
			return null;
		}

		final int spriteWidth = sprite.getWidth();
		final int spriteHeight = sprite.getHeight();

		final int tileCountPerLine = (int) Math.ceil(Math.sqrt(frameCount));

		final BufferedImage image = new BufferedImage(tileCountPerLine * spriteWidth, tileCountPerLine * spriteHeight, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setBackground(new Color(0, 0, 0, 0));

		final ColorPalette palette = sprite.getPalette();

		int frameIndex = 0;
		for (final String animationName : animationNames) {
			final Animation animation = sprite.findByName(animationName);
			if (animation == null) {
				continue;
			}
			for (double angle : animation.getAnglesWithValue()) {
				for (TileLayer frame : animation.getFrames(angle)) {
					int originY = (frameIndex / tileCountPerLine) * spriteHeight;
					int originX = (frameIndex % tileCountPerLine) * spriteWidth;
					for (int y = 0; y < frame.getHeight(); y++) {
						for(int x = 0; x < frame.getWidth(); x++) {
							palette.paintTile(graphics, frame.getTile(x, y), originX + x, originY + y, 1);
						}
					}
					frameIndex++;
				}
			}
		}

		graphics.dispose();
		return image;
	}
}
