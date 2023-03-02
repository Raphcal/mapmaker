package fr.rca.mapmaker.io.autodeploy;

import com.google.gson.Gson;
import fr.rca.mapmaker.io.playdate.AnimationNamesAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.CodeDataHandler;
import fr.rca.mapmaker.io.playdate.Headers;
import fr.rca.mapmaker.io.playdate.InstancesHandler;
import fr.rca.mapmaker.io.playdate.Names;
import fr.rca.mapmaker.io.playdate.PaletteAsCodeHandler;
import fr.rca.mapmaker.io.playdate.PaletteAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.PaletteNamesAsCodeHandler;
import fr.rca.mapmaker.io.playdate.PaletteNamesAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.PlaydateExportConfiguration;
import fr.rca.mapmaker.io.playdate.PlaydateFormat;
import fr.rca.mapmaker.io.playdate.SpriteAsCodeHandler;
import fr.rca.mapmaker.io.playdate.SpriteAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.SpriteDefinitionsAsCodeHandler;
import fr.rca.mapmaker.io.playdate.SpriteDefinitionsAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.SpriteVariablesAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.TileMapHandler;
import fr.rca.mapmaker.io.playdate.TileMapsAsCodeHandler;
import fr.rca.mapmaker.io.playdate.TileMapsAsHeaderHandler;
import fr.rca.mapmaker.model.map.MapAndInstances;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
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

		PlaydateExportConfiguration configuration = new PlaydateExportConfiguration();
		final File configFile = new File(root, "mmkconfig.json");
		if (configFile.canRead()) {
			try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
 				configuration = new Gson().fromJson(reader, PlaydateExportConfiguration.class);
			}
		}

		final List<Palette> palettes = PlaydateFormat.palettesForProject(project);

		for(final Palette palette : palettes) {
			try (final BufferedOutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(
					new File(resourceDir, "palette-" + Names.normalizeName(palette, Names::toLowerCase) + "-table-" + palette.getTileSize() + '-' + palette.getTileSize() + ".png")))) {
				ImageIO.write(PlaydateFormat.renderPalette((EditableImagePalette) palette), "png", outputStream);
			}

			generateFile(generatedSourcesDir, new PaletteAsHeaderHandler(), palette, configuration);
			generateFile(generatedSourcesDir, new PaletteAsCodeHandler(), palette, configuration);
		}
		generateFile(generatedSourcesDir, new PaletteNamesAsHeaderHandler(), palettes, configuration);
		generateFile(generatedSourcesDir, new PaletteNamesAsCodeHandler(), palettes, configuration);

		final TileMapHandler tileMapHandler = new TileMapHandler();
		final InstancesHandler instancesHandler = new InstancesHandler();

		final List<MapAndInstances> maps = project.getMaps();
		for(int index = 0; index < maps.size(); index++) {
			final MapAndInstances mapAndInstances = maps.get(index);
			final TileMap tileMap = mapAndInstances.getTileMap();

			try (final BufferedOutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(
					new File(resourceDir, "map-" + Names.normalizeName(tileMap, Names::toLowerCase) + ".data")))) {
				tileMapHandler.write(tileMap, outputStream);
				instancesHandler.write(mapAndInstances.getSpriteInstances(), outputStream);
			}
		}
		generateFile(generatedSourcesDir, new TileMapsAsHeaderHandler(), maps, configuration);
		generateFile(generatedSourcesDir, new TileMapsAsCodeHandler(), maps, configuration);

		generateFile(generatedSourcesDir, new AnimationNamesAsHeaderHandler(), project.getAnimationNames(), configuration);

		final SpriteAsCodeHandler spriteAsCodeHandler = new SpriteAsCodeHandler(project.getAnimationNames());

		final List<Sprite> sprites = PlaydateFormat.spritesForProject(project);
		for(int index = 0; index < sprites.size(); index++) {
			final Sprite sprite = sprites.get(index);
			BufferedImage spriteImage = PlaydateFormat.renderSprite(sprite, project.getAnimationNames(), configuration.getEnableDithering() != null && configuration.getEnableDithering());
			if (spriteImage != null) {
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(
						new File(resourceDir, "sprite-" + Names.normalizeName(sprite, Names::toSnakeCase) + "-table-" + sprite.getWidth() + '-' + sprite.getHeight() + ".png")))) {
					ImageIO.write(spriteImage, "png", outputStream);
				}

				generateFile(generatedSourcesDir, new SpriteAsHeaderHandler(), sprite, configuration);
				generateFile(generatedSourcesDir, spriteAsCodeHandler, sprite, configuration);
			}
		}

		generateFile(generatedSourcesDir, new SpriteDefinitionsAsHeaderHandler(), sprites, configuration);
		generateFile(generatedSourcesDir, new SpriteDefinitionsAsCodeHandler(), sprites, configuration);

		generateFile(generatedSourcesDir, new SpriteVariablesAsHeaderHandler(), PlaydateFormat.variablesForSprites(project), configuration);
	}

	private <T> void generateFile(final File generatedSourcesDir, CodeDataHandler<T> handler, final T data, PlaydateExportConfiguration configuration) throws IOException {
		File file = new File(generatedSourcesDir, handler.fileNameFor(data));
		String generatedDate = Headers.getGeneratedDate(file);
		try (final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			handler
					.withConfiguration(configuration)
					.withGeneratedDate(generatedDate)
					.write(data, outputStream);
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
