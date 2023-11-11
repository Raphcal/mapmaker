package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.mkz.BufferedImageDataHandler;
import fr.rca.mapmaker.io.mkz.InstanceDataHandler;
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
import fr.rca.mapmaker.model.sprite.SpriteType;
import fr.rca.mapmaker.util.Dithering;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Set;
import java.util.TreeSet;
import java.util.stream.Collectors;
import java.util.stream.Stream;
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

			final List<Palette> palettes = project.getPalettes().stream()
					.filter(palette -> palette instanceof EditableImagePalette)
					.collect(Collectors.toList());
			
			for(int index = 0; index < palettes.size(); index++) {
				final Palette palette = palettes.get(index);
				outputStream.putNextEntry(new ZipEntry("palette" + index + ".data"));
				write(palette, outputStream);

				outputStream.putNextEntry(new ZipEntry("palette" + index + "-table-" + palette.getTileSize() + '-' + palette.getTileSize() + ".png"));
				write(renderPalette((EditableImagePalette) palette), outputStream);

				outputStream.putNextEntry(new ZipEntry(paletteAsHeaderHandler.fileNameFor(palette)));
				paletteAsHeaderHandler.write(palette, outputStream);

				outputStream.putNextEntry(new ZipEntry(paletteAsCodeHandler.fileNameFor(palette)));
				paletteAsCodeHandler.write(palette, outputStream);
			}
			// TODO: Ajouter le fichier avec les noms des palettes

			final TileMapAsHeaderHandler tileMapAsHeaderHandler = new TileMapAsHeaderHandler();
			final TileMapAsCodeHandler tileMapAsCodeHandler = new TileMapAsCodeHandler();

			final List<TileMap> maps = mapsForProject(project);
			for(int index = 0; index < maps.size(); index++) {
				outputStream.putNextEntry(new ZipEntry("map" + index + ".data"));
				final TileMap mapAndInstances = maps.get(index);
				final TileMap tileMap = mapAndInstances;
				write(tileMap, outputStream);

				if (WRITE_MAP_AS_CODE && Names.normalizeName(tileMap, Names::toSnakeCase) != null) {
					outputStream.putNextEntry(new ZipEntry(tileMapAsHeaderHandler.fileNameFor(tileMap)));
					tileMapAsHeaderHandler.write(tileMap, outputStream);

					outputStream.putNextEntry(new ZipEntry(tileMapAsCodeHandler.fileNameFor(tileMap)));
					tileMapAsCodeHandler.write(tileMap, outputStream);
				}
			}

			final AnimationNamesAsHeaderHandler animationNameAsHeaderHandler = new AnimationNamesAsHeaderHandler();
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
			if (!hasTranslucentColor(p.getSource(index), p.getColorPalette())) {
				p.paintTile(graphics, index, x, y, tileSize);
			}
		}

		graphics.dispose();
		return image;
	}

	public static BufferedImage renderSprite(Sprite sprite, List<String> animationNames) {
		return renderSprite(sprite, animationNames, false, true);
	}

	public static BufferedImage renderSprite(Sprite sprite, List<String> animationNames, boolean dither) {
		return renderSprite(sprite, animationNames, dither, true);
	}

	public static BufferedImage renderSprite(Sprite sprite, List<String> animationNames, boolean dither, boolean distinct) {
		Stream<TileLayer> frameStream = animationNames.stream()
				.map(sprite::findByName)
				.filter(Objects::nonNull)
				.flatMap(animation -> animation.getAnglesWithValue().stream()
					.map(animation::getFrames))
				.flatMap(List::stream);

		final int frameCount = distinct
				? (int)frameStream.distinct().count()
				: (int)frameStream.count();

		if (frameCount == 0) {
			return null;
		}

		final int spriteWidth = sprite.getWidth();
		final int spriteHeight = sprite.getHeight();

		final Dimension grid = getGridSize(frameCount);

		final BufferedImage image = new BufferedImage(grid.width * spriteWidth, grid.height * spriteHeight, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setBackground(new Color(0, 0, 0, 0));

		final ColorPalette palette = sprite.getPalette();

		final HashMap<TileLayer, Integer> indexForTile = new HashMap<>(frameCount);
		int total = 0;
		for (final String animationName : animationNames) {
			final Animation animation = sprite.findByName(animationName);
			if (animation == null) {
				continue;
			}
			for (double angle : animation.getAnglesWithValue()) {
				for (TileLayer frame : animation.getFrames(angle)) {
					Integer frameIndex = indexForTile.get(frame);
					if (frameIndex == null || !distinct) {
						frameIndex = distinct ? indexForTile.size() : total++;
						indexForTile.put(frame, frameIndex);

						if (dither) {
							frame = Dithering.dither(frame, palette);
						}
						int originY = (frameIndex / grid.width) * spriteHeight;
						int originX = (frameIndex % grid.width) * spriteWidth;
						for (int y = 0; y < frame.getHeight(); y++) {
							for(int x = 0; x < frame.getWidth(); x++) {
								palette.paintTile(graphics, frame.getTile(x, y), originX + x, originY + y, 1);
							}
						}
					}
				}
			}
		}

		graphics.dispose();
		return image;
	}

	private static Dimension getGridSize(int count) {
		if (count == 1) {
			return new Dimension(1, 1);
		}
		final double root = Math.sqrt(count);
		final int integerValue = (int) root;
		final double floatingValue = root - integerValue;
		if (floatingValue == 0.0) {
			return new Dimension(integerValue, integerValue);
		} else if ((count % integerValue) == 0) {
			return new Dimension(count / integerValue, integerValue);
		} else {
			return new Dimension(count, 1);
		}
	}

	public static List<TileMap> mapsForProject(Project project) {
		return project.getMaps().stream()
				.filter(map -> map.isExportable())
				.collect(Collectors.toList());
	}

	public static List<Palette> palettesForProject(Project project) {
		return project.getPalettes().stream()
				.filter(palette -> palette instanceof EditableImagePalette)
				.collect(Collectors.toList());
	}

	public static List<Sprite> spritesForProject(Project project) {
		return project.getSprites().stream()
				.filter(sprite -> sprite.isExportable() && sprite.getType() != SpriteType.FONT.ordinal() && !sprite.isEmpty())
				.collect(Collectors.toList());
	}

	public static List<Sprite> fontsForProject(Project project) {
		return project.getSprites().stream()
				.filter(sprite -> sprite.isExportable() && sprite.getType() == SpriteType.FONT.ordinal() && !sprite.isEmpty())
				.collect(Collectors.toList());
	}

	/**
	 * Renvoi une table associant un nom de script vers les variables possibles.
	 *
	 * @param project Projet.
	 * @return Une table associant un nom de script aux variables déclarées.
	 */
	public static Map<String, Set<String>> variablesForSprites(Project project) {
		final HashMap<String, Set<String>> result = new HashMap<>();
		project.getAllInstances().stream()
				.flatMap(List::stream)
				.filter(instance -> instance.getSprite().getScriptFile() != null && instance.hasVariable())
				.forEach(instance -> result
						.computeIfAbsent(instance.getSprite().getScriptFile(), key -> new TreeSet<>())
						.addAll(instance.getVariables().keySet()));
		return result;
	}

	private static boolean hasTranslucentColor(TileLayer tile, ColorPalette palette) {
		final int count = tile.getWidth() * tile.getHeight();
		for (int index = 0; index < count; index++) {
			final Point point = new Point(index % tile.getWidth(), index / tile.getWidth());
			final int colorIndex = tile.getTile(point);
			final Color color = palette.getColor(colorIndex);
			if (color != null && color.getAlpha() > 0 && color.getAlpha() < 255) {
				return true;
			}
		}
		return false;
	}

}
