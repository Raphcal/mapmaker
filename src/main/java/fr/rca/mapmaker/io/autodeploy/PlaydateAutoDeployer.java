package fr.rca.mapmaker.io.autodeploy;

import com.google.gson.Gson;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.playdate.AnimationNamesAsHeaderHandler;
import fr.rca.mapmaker.io.playdate.CodeDataHandler;
import fr.rca.mapmaker.io.playdate.FontHandler;
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
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
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
		resourceDir.mkdir();
		generatedSourcesDir.mkdir();

		PlaydateExportConfiguration configuration = new PlaydateExportConfiguration();
		final File configFile = new File(root, "mmkconfig.json");
		if (configFile.canRead()) {
			try (InputStreamReader reader = new InputStreamReader(new FileInputStream(configFile), StandardCharsets.UTF_8)) {
 				configuration = new Gson().fromJson(reader, PlaydateExportConfiguration.class);
			} catch (Exception e) {
				if (!isHeadless()) {
					Exceptions.showStackTrace(e, null);
					return;
				} else {
					throw new IOException("Unable to read mmkconfig", e);
				}
			}
		}

		final boolean flattenLayers = Optional.ofNullable(configuration)
				.map(PlaydateExportConfiguration::getMaps)
				.map(PlaydateExportConfiguration.Maps::getFlattenLayers)
				.orElse(false);

		final boolean createDirectories = Optional.ofNullable(configuration)
				.map(PlaydateExportConfiguration::getCreateDirectories)
				.orElse(false);

		final List<Palette> palettes = PlaydateFormat.palettesForProject(project);

		final File mapDir = createDirectories
				? new File(resourceDir, "maps")
				: resourceDir;
		mapDir.mkdirs();

		for(final Palette palette : palettes) {
			if (!flattenLayers) {
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(
						new File(mapDir, "palette-" + Names.normalizeName(palette, Names::toLowerCase) + "-table-" + palette.getTileSize() + '-' + palette.getTileSize() + ".png")))) {
					ImageIO.write(PlaydateFormat.renderPalette((EditableImagePalette) palette), "png", outputStream);
				}
			}

			generateFile(generatedSourcesDir, new PaletteAsHeaderHandler(), palette, configuration);
			generateFile(generatedSourcesDir, new PaletteAsCodeHandler(), palette, configuration);
		}
		generateFile(generatedSourcesDir, new PaletteNamesAsHeaderHandler(), palettes, configuration);
		generateFile(generatedSourcesDir, new PaletteNamesAsCodeHandler(), palettes, configuration);

		final TileMapHandler tileMapHandler = new TileMapHandler().withConfiguration(configuration);
		final InstancesHandler instancesHandler = new InstancesHandler().withConfiguration(configuration);

		final List<TileMap> maps = PlaydateFormat.mapsForProject(project);
		for(int index = 0; index < maps.size(); index++) {
			final TileMap mapAndInstances = maps.get(index);
			final TileMap tileMap = mapAndInstances;

			try (final BufferedOutputStream outputStream = new BufferedOutputStream(
					new FileOutputStream(
					new File(mapDir, "map-" + Names.normalizeName(tileMap, Names::toLowerCase) + ".data")))) {
				tileMapHandler.write(tileMap, outputStream);
				final List<Instance> instances = mapAndInstances.getSpriteInstances().stream()
						.filter(instance -> instance.getSprite().isExportable())
						.collect(Collectors.toList());
				instancesHandler.write(instances, outputStream);
			}

			if (flattenLayers) {
				// Exporte chaque couches séparement dans un PNG chacun.
				final ImageRenderer renderer = new ImageRenderer();
				final Palette palette = tileMap.getPalette();
				final ArrayList<Layer> layers = tileMap.getLayers();
				for (int layerIndex = 0; layerIndex < layers.size(); layerIndex++) {
					final Layer layer = layers.get(layerIndex);
					Rectangle size = TileMapHandler.getLayerSize(layer);
					if (size.width == 0 || size.height == 0) {
						continue;
					}
					try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(
						new File(mapDir, "map-" + Names.normalizeName(tileMap, Names::toLowerCase) + "-layer-" + layerIndex + ".png")))) {
						ImageIO.write(renderer.renderImage((TileLayer) layer, palette, size, palette.getTileSize()), "png", outputStream);
					}
				}
			}
		}
		generateFile(generatedSourcesDir, new TileMapsAsHeaderHandler().withConfiguration(configuration), maps, configuration);
		generateFile(generatedSourcesDir, new TileMapsAsCodeHandler(resourceDir).withConfiguration(configuration), maps, configuration);

		generateFile(generatedSourcesDir, new AnimationNamesAsHeaderHandler(), project.getAnimationNames(), configuration);

		final SpriteAsCodeHandler spriteAsCodeHandler = new SpriteAsCodeHandler(project.getAnimationNames());
		final FontHandler fontHandler = new FontHandler(project.getAnimationNames());

		final File spriteDir = createDirectories
				? new File(resourceDir, "sprites")
				: resourceDir;
		spriteDir.mkdirs();

		final List<Sprite> sprites = PlaydateFormat.spritesForProject(project);
		for(int index = 0; index < sprites.size(); index++) {
			final Sprite sprite = sprites.get(index);
			BufferedImage spriteImage = PlaydateFormat.renderSprite(sprite, project.getAnimationNames(), configuration.getEnableDithering() != null && configuration.getEnableDithering());
			if (spriteImage != null) {
				try (final BufferedOutputStream outputStream = new BufferedOutputStream(
						new FileOutputStream(
						new File(spriteDir, "sprite-" + Names.normalizeName(sprite, Names::toSnakeCase) + "-table-" + sprite.getWidth() + '-' + sprite.getHeight() + ".png")))) {
					ImageIO.write(spriteImage, "png", outputStream);
				}

				generateFile(generatedSourcesDir, new SpriteAsHeaderHandler(), sprite, configuration);
				generateFile(generatedSourcesDir, spriteAsCodeHandler, sprite, configuration);
			}
		}

		generateFile(generatedSourcesDir, new SpriteDefinitionsAsHeaderHandler(), sprites, configuration);
		generateFile(generatedSourcesDir, new SpriteDefinitionsAsCodeHandler(), sprites, configuration);

		final File fontDir = createDirectories
				? new File(resourceDir, "fonts")
				: resourceDir;
		fontDir.mkdirs();

		final List<Sprite> fonts = PlaydateFormat.fontsForProject(project);
		for(Sprite font : fonts) {
			generateFile(fontDir, fontHandler, font, configuration);
		}

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

	private <T> void generateFile(final File generatedSourcesDir, DataHandler<T> handler, final T data, PlaydateExportConfiguration configuration) throws IOException {
		File file = new File(generatedSourcesDir, handler.fileNameFor(data));
		try (final BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			handler.write(data, outputStream);
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
