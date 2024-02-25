package fr.rca.mapmaker.io.bundle;

import fr.rca.mapmaker.io.plist.Plists;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.HasProgress;
import fr.rca.mapmaker.io.ProgressTracker;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.ImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.util.CanBeDirty;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class BundleFormat extends AbstractFormat implements HasProgress {
	private static final Logger LOGGER = LoggerFactory.getLogger(BundleFormat.class);

	private static final String EXTENSION = ".mmkb";

	private static final String INFO_FILE = "Info.plist";
	private static final String PALETTE_FILE_FORMAT = "palette%d.pal";
	private static final String MAP_FILE_FORMAT = "map%d.map";
	private static final String INSTANCES_FILE_FORMAT = "map%d.ins";
	private static final String SPRITE_FILE_FORMAT = "sprite%d.spr";
	private static final String SCRIPT_FILE_FORMAT = "script%d.txt";

	private static final String[] OWN_FILE_EXTENSIONS = {".plist", ".pal", ".map", ".ins", ".spr", ".txt"};

	private static final String VERSION = "version";
	private static final String PALETTES = "palettes";
	private static final String MAPS = "maps";
	private static final String MAP = "map";
	private static final String INSTANCES = "instances";
	private static final String SPRITES = "sprites";
	private static final String NEXT_MAP = "next-map";
	private static final String ANIMATION_NAMES = "animation-names";
	private static final String SCRIPTS = "scripts";

	public BundleFormat() {
		super(EXTENSION, SupportedOperation.SAVE, SupportedOperation.LOAD);

		addHandler(Color.class, new fr.rca.mapmaker.io.internal.ColorDataHandler());
		addHandler(Palette.class, new fr.rca.mapmaker.io.internal.PaletteDataHandler(this));
		addHandler(ColorPalette.class, new fr.rca.mapmaker.io.internal.ColorPaletteDataHandler(this));
		addHandler(AlphaColorPalette.class, new fr.rca.mapmaker.io.internal.AlphaColorPaletteDataHandler(this));
		addHandler(EditableColorPalette.class, new fr.rca.mapmaker.io.internal.EditableColorPaletteDataHandler(this));
		addHandler(ImagePalette.class, new fr.rca.mapmaker.io.internal.ImagePaletteDataHandler(this));
		addHandler(EditableImagePalette.class, new fr.rca.mapmaker.io.internal.EditableImagePaletteDataHandler(this));
		addHandler(PaletteReference.class, new fr.rca.mapmaker.io.internal.PaletteReferenceDataHandler());
		addHandler(BufferedImage.class, new fr.rca.mapmaker.io.internal.BufferedImageDataHandler());
		addHandler(TileLayer.class, new fr.rca.mapmaker.io.internal.LayerDataHandler(this));
		addHandler(ScrollRate.class, new fr.rca.mapmaker.io.internal.ScrollRateDataHandler());
		addHandler(TileMap.class, new fr.rca.mapmaker.io.internal.TileMapDataHandler(this));
		addHandler(Sprite.class, new fr.rca.mapmaker.io.internal.SpriteDataHandler(this));
		addHandler(Animation.class, new fr.rca.mapmaker.io.internal.AnimationDataHandler(this));
		addHandler(Instance.class, new fr.rca.mapmaker.io.internal.InstanceDataHandler());
		addHandler(Rectangle.class, new fr.rca.mapmaker.io.internal.RectangleDataHandler());
	}

	@Override
	public void saveProject(Project project, File file) {
		saveProject(project, file, null);
	}

	@Override
	public void saveProject(Project project, File file, Listener progressListener) {
		final ProgressTracker progressTracker = new ProgressTracker(progressListener,
				"mkdir", "palettes", "maps", "instances", "sprites", "scripts", "info", "remove");

		setVersion(InternalFormat.LAST_VERSION);

		Integer oldVersion = null;
		if (file.isDirectory()) {
			try {
				final Map<String, Object> projectInfo = readProjectInfo(file);

				// Version
				final Integer version = (Integer) projectInfo.get(VERSION);
				oldVersion = version != null ? version : InternalFormat.VERSION_4;
			} catch (IOException e) {
				LOGGER.error("Erreur lors de la lecture de la précédente version", e);
			}
		}
		final boolean forceDirty = oldVersion == null || oldVersion != InternalFormat.LAST_VERSION;

		file.mkdir();
		final HashSet<File> files = new HashSet<File>(Arrays.asList(file.listFiles(BundleFormat::isOwnFile)));

		progressTracker.stepDidEnd("mkdir");

		// Écriture des nouveaux fichiers
		final LinkedHashMap<String, Object> projectMap = new LinkedHashMap<String, Object>();
		projectMap.put(VERSION, InternalFormat.LAST_VERSION);
		projectMap.put(NEXT_MAP, project.getNextMap());

		final List<String> palettes = new ArrayList<String>();
		final List<Map<String, Object>> maps = new ArrayList<Map<String, Object>>();
		final List<String> sprites = new ArrayList<String>();
		projectMap.put(PALETTES, palettes);
		projectMap.put(MAPS, maps);
		projectMap.put(SPRITES, sprites);
		projectMap.put(ANIMATION_NAMES, project.getAnimationNames());

		try {
			// Palettes
			write(project.getPalettes(), file, files, PALETTE_FILE_FORMAT, palettes, getHandler(Palette.class), forceDirty);
			progressTracker.stepDidEnd("palettes");

			// Cartes
			final DataHandler<TileMap> tileMapHandler = getHandler(TileMap.class);
			final DataHandler<Instance> instanceHandler = getHandler(Instance.class);
			progressTracker.stepHaveSubsteps(project.getMaps().size());
			for (final TileMap mapAndInstances : project.getMaps()) {
				final TileMap tileMap = mapAndInstances;

				final Map<String, Object> map = new HashMap<>();
				final String mapName = String.format(MAP_FILE_FORMAT, tileMap.getIndex());
				final String instancesName = String.format(INSTANCES_FILE_FORMAT, tileMap.getIndex());

				writeMap(tileMap, file, files, mapName, tileMapHandler, map, forceDirty);
				progressTracker.subStepDidEnd();

				// Instances
				final List<Instance> instances = mapAndInstances.getSpriteInstances();
				writeInstances(instances, file, files, instancesName, instanceHandler, map);
				progressTracker.subStepDidEnd();

				maps.add(map);
			}
			progressTracker.stepDidEnd("instances");

			// Sprites
			write(project.getSprites(), file, files, SPRITE_FILE_FORMAT, sprites, getHandler(Sprite.class), forceDirty);
			progressTracker.stepDidEnd("sprites");

			// Scripts
			// NOTE: Pourquoi réécrire les scripts s'ils ne sont pas modifiés par l'application ?
			final List<String> scriptNames = project.getScripts().keySet().stream()
					.sorted()
					.collect(Collectors.toList());
			progressTracker.stepHaveSubsteps(scriptNames.size());
			for (int index = 0; index < scriptNames.size(); index++) {
				final String script = project.getScripts().get(scriptNames.get(index));
				if (script != null && !script.isEmpty()) {
					write(script, file, String.format(SCRIPT_FILE_FORMAT, index), files);
				}
				progressTracker.subStepDidEnd();
			}
			projectMap.put(SCRIPTS, scriptNames);
			progressTracker.stepDidEnd("scripts");

			// Informations générales
			final File infoFile = new File(file, INFO_FILE);
			files.remove(infoFile);

			try (FileOutputStream projectOutputStream = new FileOutputStream(infoFile)) {
				Plists.write(projectMap, projectOutputStream);
			}
			progressTracker.stepDidEnd("info");

			// Suppression des fichiers restants
			progressTracker.stepHaveSubsteps(files.size());
			for (final File unusedFile : files) {
				unusedFile.delete();
				progressTracker.subStepDidEnd();
			}

			progressTracker.onEnd();

		} catch (IOException e) {
			Exceptions.showStackTrace(e, null);
		}
	}

	private void write(String value, File parent, String name, Set<File> files) throws IOException {
		final File file = new File(parent, name);
		files.remove(file);
		try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			outputStream.write(value.getBytes(StandardCharsets.UTF_8));
		}
	}

	private <T> void write(List<T> objects, File parent, Set<File> files, String format, List<String> infoEntries, DataHandler<T> handler, boolean forceDirty) throws IOException {
		for (int index = 0; index < objects.size(); index++) {
			final String name = String.format(format, index);
			final File file = new File(parent, name);
			files.remove(file);
			final T object = objects.get(index);
			final CanBeDirty canBeDirty = CanBeDirty.wrap(object);
			LOGGER.debug("{} {} {}", object.getClass().getSimpleName(), object.toString(), (canBeDirty.isDirty() ? "is dirty" : "has not changed"));
			if (forceDirty || canBeDirty.isDirty()) {
				try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
					handler.write(object, outputStream);
					canBeDirty.setDirty(false);
				}
			}
			infoEntries.add(name);
		}
	}

	private void writeMap(TileMap tileMap, File parent, Set<File> files, final String mapName, final DataHandler<TileMap> tileMapHandler, final Map<String, Object> map, boolean forceDirty) throws IOException, FileNotFoundException {
		// Carte
		final File file = new File(parent, mapName);
		files.remove(file);
		LOGGER.debug("TileMap {} {}", tileMap.getName(), (tileMap.isDirty() ? "is dirty" : "has not changed"));
		if (forceDirty || tileMap.isDirty()) {
			try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
				tileMapHandler.write(tileMap, outputStream);
				tileMap.setDirty(false);
			}
		}
		map.put(MAP, mapName);
	}

	private void writeInstances(final List<Instance> instances, File parent, Set<File> files, final String instancesName, final DataHandler<Instance> instanceHandler, final Map<String, Object> map) throws FileNotFoundException, IOException {
		final File file = new File(parent, instancesName);
		files.remove(file);
		try (BufferedOutputStream outputStream = new BufferedOutputStream(new FileOutputStream(file))) {
			Streams.write(instances.size(), outputStream);
			for (Instance instance : instances) {
				instanceHandler.write(instance, outputStream);
			}
			map.put(INSTANCES, instancesName);
		}
	}

	@Override
	public Project openProject(File file) {
		return openProject(file, null);
	}

	@Override
	public Project openProject(File file, Listener progressListener) {
		final Project project = new Project();
		final ProgressTracker progressTracker = new ProgressTracker(progressListener,
				"info", "version", "animationNames", "palettes", "maps", "instances", "scripts", "sprites");

		try {
			final Map<String, Object> projectInfo = readProjectInfo(file);
			progressTracker.stepDidEnd("info");

			// Version
			final Integer version = (Integer) projectInfo.get(VERSION);
			setVersion(version != null ? version : InternalFormat.VERSION_4);
			progressTracker.stepDidEnd("version");

			final boolean oldVersion = version == null || version < InternalFormat.LAST_VERSION;

			// Animation names
			List<String> animationNames = (List<String>) projectInfo.get(ANIMATION_NAMES);
			if (animationNames != null) {
				project.setAnimationNames(animationNames);
			}
			progressTracker.stepDidEnd("animationNames");

			// Palettes
			final DataHandler<Palette> paletteHandler = getHandler(Palette.class);
			final List<String> palettes = (List<String>) projectInfo.get(PALETTES);
			progressTracker.stepHaveSubsteps(palettes.size());
			for (final String palette : palettes) {
				project.addPalette(read(file, palette, paletteHandler, oldVersion));
				progressTracker.subStepDidEnd();
			}
			progressTracker.stepDidEnd("palettes");

			// Maps
			final DataHandler<TileMap> tileMapHandler = getHandler(TileMap.class);
			final DataHandler<Instance> instanceHandler = getHandler(Instance.class);

			final List<Map<String, Object>> maps = (List<Map<String, Object>>) projectInfo.get(MAPS);
			progressTracker.stepHaveSubsteps(maps.size());

			for (final Map<String, Object> map : maps) {
				// Map
				final TileMap tileMap = read(file, (String) map.get(MAP), tileMapHandler, oldVersion);
				progressTracker.subStepDidEnd();

				// Instances
				final List<Instance> instances = readInstances(file, (String) map.get(INSTANCES), project, instanceHandler);
				progressTracker.subStepDidEnd();

				project.addMap(tileMap, instances, false);
			}
			if (version >= InternalFormat.VERSION_7 && projectInfo.containsKey(NEXT_MAP)) {
				project.setNextMap((Integer) projectInfo.get(NEXT_MAP));
			}
			progressTracker.stepDidEnd("instances");

			// Scripts
			final List<String> scriptNames = (List<String>) projectInfo.get(SCRIPTS);
			if (scriptNames != null) {
				progressTracker.stepHaveSubsteps(scriptNames.size());
				for (int index = 0; index < scriptNames.size(); index++) {
					project.getScripts().put(scriptNames.get(index), read(file, String.format(SCRIPT_FILE_FORMAT, index)));
					progressTracker.subStepDidEnd();
				}
			}
			progressTracker.stepDidEnd("scripts");

			// Sprites
			final DataHandler<Sprite> spriteHandler = getHandler(Sprite.class);
			final List<Sprite> sprites = project.getSprites();
			sprites.clear();

			final List<String> spriteFiles = (List<String>) projectInfo.get(SPRITES);
			progressTracker.stepHaveSubsteps(spriteFiles.size());
			for (final String sprite : spriteFiles) {
				Sprite aSprite;
				try {
					aSprite = read(file, sprite, spriteHandler, oldVersion);
				} catch (FileNotFoundException e) {
					aSprite = new Sprite();
				}
				sprites.add(aSprite);
				progressTracker.subStepDidEnd();
			}

			progressTracker.onEnd();

			return project;

		} catch (IOException e) {
			Exceptions.showStackTrace(e, null);
		}

		return Project.createEmptyProject();
	}

	private Map<String, Object> readProjectInfo(File parent) throws IOException, FileNotFoundException {
		try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(parent, INFO_FILE)))) {
			return Plists.read(inputStream);
		}
	}

	private <T> T read(File parent, String name, DataHandler<T> handler, boolean dirty) throws FileNotFoundException, IOException {
		try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(new File(parent, name)))) {
			final T t = handler.read(inputStream);
			CanBeDirty.wrap(t).setDirty(dirty);
			return t;
		}
	}

	/**
	 * Lecture des instances contenues dans le fichier <code>name</code>.
	 *
	 * @param parent Paquet à ouvrir.
	 * @param name Nom du fichier d'instances.
	 * @param handler Gestionnaire d'instances.
	 * @return La liste d'instances contenue dans le fichier donné.
	 * @throws FileNotFoundException Si le fichier n'existe pas.
	 * @throws IOException En cas d'erreur de lecture.
	 */
	private List<Instance> readInstances(File parent, String name, Project project, DataHandler<Instance> handler) throws FileNotFoundException, IOException {
		final List<Instance> instances = new ArrayList<>();

		final File file = new File(parent, name);

		if (file.exists()) {
			try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
				final int size = Streams.readInt(inputStream);
				for (int index = 0; index < size; index++) {
					final Instance instance = handler.read(inputStream);
					instance.setProject(project);
					instances.add(instance);
				}
			}
		}

		return instances;
	}

	private String read(File parent, String name) throws IOException {
		final File file = new File(parent, name);
		if (!file.isFile() || !file.canRead()) {
			return null;
		}

		final byte[] data;
		try (BufferedInputStream inputStream = new BufferedInputStream(new FileInputStream(file))) {
			data = inputStream.readAllBytes();
		}
		return StandardCharsets.UTF_8.decode(ByteBuffer.wrap(data)).toString();
	}

	private static boolean isOwnFile(File dir, String fileName) {
		if (fileName == null) {
			return false;
		}
		for (String extension : OWN_FILE_EXTENSIONS) {
			if (fileName.endsWith(extension)) {
				return true;
			}
		}
		return false;
	}
}
