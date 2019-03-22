package fr.rca.mapmaker.io.bundle;

import fr.rca.mapmaker.io.plist.Plists;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.HasProgress;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.map.MapAndInstances;
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
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class BundleFormat extends AbstractFormat implements HasProgress {
	
	private static final String EXTENSION = ".mmkb";
	
	private static final String INFO_FILE = "Info.plist";
	private static final String PALETTE_FILE_FORMAT = "palette%d.pal";
	private static final String MAP_FILE_FORMAT = "map%d.map";
	private static final String INSTANCES_FILE_FORMAT = "map%d.ins";
	private static final String SPRITE_FILE_FORMAT = "sprite%d.spr";
	
	private static final String VERSION = "version";
	private static final String PALETTES = "palettes";
	private static final String MAPS = "maps";
	private static final String MAP = "map";
	private static final String INSTANCES = "instances";
	private static final String SPRITES = "sprites";
	private static final String NEXT_MAP = "next-map";
	
	private static final int FULL_PROGRESS = 100;
	private static final int READ_STEPS = 5;
	private static final int READ_ELEMENT_PROGRESS = FULL_PROGRESS / READ_STEPS;
	private static final int WRITE_STEPS = 6;
	private static final int WRITE_ELEMENT_PROGRESS = FULL_PROGRESS / WRITE_STEPS;

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
		setVersion(InternalFormat.LAST_VERSION);
		progress(0, progressListener);
		
		file.mkdir();
		
		final HashSet<File> files = new HashSet<File>(Arrays.asList(file.listFiles()));
		
		progress(WRITE_ELEMENT_PROGRESS, progressListener);
		
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
		
		try {
			// Palettes
			write(project.getPalettes(), file, files, PALETTE_FILE_FORMAT, palettes, getHandler(Palette.class));
			int progress = progress(WRITE_ELEMENT_PROGRESS * 2, progressListener);

			// Cartes
			final DataHandler<TileMap> tileMapHandler = getHandler(TileMap.class);
			final DataHandler<Instance> instanceHandler = getHandler(Instance.class);
			final int size = project.getMaps().size();
			for(final MapAndInstances mapAndInstances : project.getMaps()) {
				final TileMap tileMap = mapAndInstances.getTileMap();
				
				final Map<String, Object> map = new HashMap<>();
				final String mapName =  String.format(MAP_FILE_FORMAT, tileMap.getIndex());
				final String instancesName =  String.format(INSTANCES_FILE_FORMAT, tileMap.getIndex());
				
				writeMap(tileMap, file, files, mapName, tileMapHandler, map);
				progress = progress(progress + WRITE_ELEMENT_PROGRESS / size, progressListener);
				
				// Instances
				final List<Instance> instances = mapAndInstances.getSpriteInstances();
				writeInstances(instances, file, files, instancesName, instanceHandler, map);
				progress = progress(progress + WRITE_ELEMENT_PROGRESS / size, progressListener);
				
				maps.add(map);
			}
			progress(WRITE_ELEMENT_PROGRESS * 4, progressListener);

			// Sprites
			write(project.getSprites(), file, files, SPRITE_FILE_FORMAT, sprites, getHandler(Sprite.class));
			progress(WRITE_ELEMENT_PROGRESS * 5, progressListener);
			
			// Informations générales
			final File infoFile = new File(file, INFO_FILE);
			files.remove(infoFile);
			
			try (FileOutputStream projectOutputStream = new FileOutputStream(infoFile)) {
				Plists.write(projectMap, projectOutputStream);
			}
			
			// Suppression des fichiers restants
			for (final File unusedFile : files) {
				unusedFile.delete();
			}
			
			progress(FULL_PROGRESS, progressListener);
			
		} catch(IOException e) {
			Exceptions.showStackTrace(e, null);
		}
	}

	private <T> void write(List<T> objects, File parent, Set<File> files, String format, final List<String> infoEntries, DataHandler<T> handler) throws IOException {
		for(int index = 0; index < objects.size(); index++) {
			final String name = String.format(format, index);
			final File file = new File(parent, name);
			files.remove(file);
			try (FileOutputStream outputStream = new FileOutputStream(file)) {
				handler.write(objects.get(index), outputStream);
				infoEntries.add(name);
			}
		}
	}
	
	private void writeMap(TileMap tileMap, File parent, Set<File> files, final String mapName, final DataHandler<TileMap> tileMapHandler, final Map<String, Object> map) throws IOException, FileNotFoundException {
		// Carte
		final File file = new File(parent, mapName);
		files.remove(file);
		try (FileOutputStream mapOutputStream = new FileOutputStream(file)) {
			tileMapHandler.write(tileMap, mapOutputStream);
			map.put(MAP, mapName);
		}
	}
	
	private void writeInstances(final List<Instance> instances, File parent, Set<File> files, final String instancesName, final DataHandler<Instance> instanceHandler, final Map<String, Object> map) throws FileNotFoundException, IOException {
		final File file = new File(parent, instancesName);
		files.remove(file);
		try (FileOutputStream instancesOutputStream = new FileOutputStream(file)) {
			Streams.write(instances.size(), instancesOutputStream);
			for(Instance instance : instances) {
				instanceHandler.write(instance, instancesOutputStream);
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
		progress(0, progressListener);
		
		try {
			final Map<String, Object> projectInfo = readProjectInfo(file);
			int progress = progress(READ_ELEMENT_PROGRESS, progressListener);
			
			// Version
			final Integer version = (Integer) projectInfo.get(VERSION);
			setVersion(version != null ? version : InternalFormat.VERSION_4);
			
			// Palettes
			final DataHandler<Palette> paletteHandler = getHandler(Palette.class);
			final List<String> palettes = (List<String>) projectInfo.get(PALETTES);
			for(final String palette : palettes) {
				project.addPalette(read(file, palette, paletteHandler));
				
				progress = progress(progress + READ_ELEMENT_PROGRESS / palettes.size(), progressListener);
			}
			progress = progress(READ_ELEMENT_PROGRESS * 2, progressListener);
			
			// Maps
			final DataHandler<TileMap> tileMapHandler = getHandler(TileMap.class);
			final DataHandler<Instance> instanceHandler = getHandler(Instance.class);
			
			final List<Map<String, Object>> maps = (List<Map<String, Object>>) projectInfo.get(MAPS);
			
			for(final Map<String, Object> map : maps) {
				// Map
				final TileMap tileMap = read(file, (String) map.get(MAP), tileMapHandler);
				progress = progress(progress + READ_ELEMENT_PROGRESS / maps.size(), progressListener);
				
				// Instances
				final List<Instance> instances = readInstances(file, (String) map.get(INSTANCES), project, instanceHandler);
				progress = progress(progress + READ_ELEMENT_PROGRESS / maps.size(), progressListener);
				
				project.addMap(tileMap, instances);
			}
			if(version >= InternalFormat.VERSION_7 && projectInfo.containsKey(NEXT_MAP)) {
				project.setNextMap((Integer) projectInfo.get(NEXT_MAP));
			}
			progress = progress(READ_ELEMENT_PROGRESS * 4, progressListener);
			
			// Sprites
			final DataHandler<Sprite> spriteHandler = getHandler(Sprite.class);
			final List<Sprite> sprites = project.getSprites();
			sprites.clear();
			
			final List<String> spriteFiles = (List<String>) projectInfo.get(SPRITES);
			
			for(final String sprite : spriteFiles) {
				sprites.add(read(file, sprite, spriteHandler));
				progress = progress(progress + READ_ELEMENT_PROGRESS / spriteFiles.size(), progressListener);
			}
			
			progress(FULL_PROGRESS, progressListener);

			return project;
			
		} catch(IOException e) {
			Exceptions.showStackTrace(e, null);
		}
		
		return Project.createEmptyProject();
	}
	
	private int progress(int value, HasProgress.Listener listener) {
		if (listener != null) {
			listener.onProgress(value);
		}
		return value;
	}

	private Map<String, Object> readProjectInfo(File parent) throws IOException, FileNotFoundException {
		try (FileInputStream inputStream = new FileInputStream(new File(parent, INFO_FILE))) {
			return Plists.read(inputStream);
		}
	}
	
	private <T> T read(File parent, String name, DataHandler<T> handler) throws FileNotFoundException, IOException {
		try (FileInputStream inputStream = new FileInputStream(new File(parent, name))) {
			return handler.read(inputStream);
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
		
		if(file.exists()) {
			try (FileInputStream inputStream = new FileInputStream(file)) {
				final int size = Streams.readInt(inputStream);
				for(int index = 0; index < size; index++) {
					final Instance instance = handler.read(inputStream);
					instance.setProject(project);
					instances.add(instance);
				}
			}
		}
		
		return instances;
	}
}
