package fr.rca.mapmaker.io.autodeploy;

import fr.rca.mapmaker.io.playdate.AnimationNameAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.Headers;
import fr.rca.mapmaker.io.playdate.Names;
import fr.rca.mapmaker.io.playdate.PaletteAsCodeHandler;
import fr.rca.mapmaker.io.playdate.PaletteAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.PlaydateFormat;
import fr.rca.mapmaker.io.playdate.SpriteAsCodeHandler;
import fr.rca.mapmaker.io.playdate.SpriteAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.TileMapHandler;
import fr.rca.mapmaker.model.map.MapAndInstances;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class PlaydateAutoDeployer extends AutoDeployer {

	@Override
	public String getName() {
		return "Playdate";
	}

	@Override
	public void deployProjectInFolder(Project project, File root) throws IOException {
		final File resourceDir = new File(root, "Source");
		final File generatedSourcesDir = new File(root, "gen");
		generatedSourcesDir.mkdir();

		File file;
		String generatedDate;

		final PaletteAsHeaderHandler paletteAsHeaderHandler = new PaletteAsHeaderHandler();
		final PaletteAsCodeHandler paletteAsCodeHandler = new PaletteAsCodeHandler();

		final List<Palette> palettes = project.getPalettes();
		for(int index = 0; index < palettes.size(); index++) {
			final Palette palette = palettes.get(index);
			if (palette instanceof EditableImagePalette) {
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(
						new File(resourceDir, "palette" + Names.normalizeName(palette, Names::toLowerCase) + "-table-" + palette.getTileSize() + '-' + palette.getTileSize() + ".png")))) {
					ImageIO.write(PlaydateFormat.renderPalette((EditableImagePalette) palette), "png", outputStream);
				}

				file = new File(generatedSourcesDir, paletteAsHeaderHandler.fileNameFor(palette));
				generatedDate = Headers.getGeneratedDate(file);
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(file))) {
					paletteAsHeaderHandler
							.withGeneratedDate(generatedDate)
							.write(palette, outputStream);
				}

				file = new File(generatedSourcesDir, paletteAsCodeHandler.fileNameFor(palette));
				generatedDate = Headers.getGeneratedDate(file);
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(file))) {
					paletteAsCodeHandler
							.withGeneratedDate(generatedDate)
							.write(palette, outputStream);
				}
			}
		}

		final TileMapHandler tileMapHandler = new TileMapHandler();

		final List<MapAndInstances> maps = project.getMaps();
		for(int index = 0; index < maps.size(); index++) {
			final MapAndInstances mapAndInstances = maps.get(index);
			final TileMap tileMap = mapAndInstances.getTileMap();

			try (final BufferedOutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(
					new File(resourceDir, "map" + Names.normalizeName(tileMap, Names::toLowerCase) + ".data")))) {
				tileMapHandler.write(tileMap, outputStream);
			}
		}

		final AnimationNameAsHeaderHandler animationNameAsHeaderHandler = new AnimationNameAsHeaderHandler();
		file = new File(generatedSourcesDir, animationNameAsHeaderHandler.fileNameFor(project.getAnimationNames()));
		generatedDate = Headers.getGeneratedDate(file);
		try (final BufferedOutputStream outputStream = new BufferedOutputStream(
				new FileOutputStream(file))) {
			animationNameAsHeaderHandler
					.withGeneratedDate(generatedDate)
					.write(project.getAnimationNames(), outputStream);
		}

		final SpriteAsHeaderHandler spriteAsHeaderHandler = new SpriteAsHeaderHandler();
		final SpriteAsCodeHandler spriteAsCodeHandler = new SpriteAsCodeHandler(project.getAnimationNames());

		final List<Sprite> sprites = project.getSprites();
		for(int index = 0; index < sprites.size(); index++) {
			final Sprite sprite = sprites.get(index);
			BufferedImage spriteImage = PlaydateFormat.renderSprite(sprite, project.getAnimationNames());
			if (spriteImage != null) {
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(
						new File(resourceDir, "sprite-" + Names.normalizeName(sprite, Names::toSnakeCase) + "-table-" + sprite.getWidth() + '-' + sprite.getHeight() + ".png")))) {
					ImageIO.write(spriteImage, "png", outputStream);
				}

				file = new File(generatedSourcesDir, spriteAsHeaderHandler.fileNameFor(sprite));
				generatedDate = Headers.getGeneratedDate(file);
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(file))) {
					spriteAsHeaderHandler
							.withGeneratedDate(generatedDate)
							.write(sprite, outputStream);
				}

				file = new File(generatedSourcesDir, spriteAsCodeHandler.fileNameFor(sprite));
				generatedDate = Headers.getGeneratedDate(file);
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(file))) {
					spriteAsCodeHandler
							.withGeneratedDate(generatedDate)
							.write(sprite, outputStream);
				}
			}
		}
	}

	@Override
	public boolean accept(File f) {
		final File resourceDir = new File(f, "Source");
		final File pdxInfo = new File(resourceDir, "pdxinfo");
		return resourceDir.isDirectory() && pdxInfo.isFile();
	}

	@Override
	public String getDescription() {
		return "Dossier d'un projet Playdate";
	}
	
}
