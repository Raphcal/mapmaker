package fr.rca.mapmaker.io.autodeploy;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Formats;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.io.mkz.MKZFormat;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.xml.stream.XMLStreamException;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class MeltedIceAutoDeploy {
	private static final String SOURCE_PATH = "/Users/daeke/Documents/Java/mapmaker/mmkb/MeltedIce.mmkb";
	private static final String DEPLOY_PATH = "/Users/daeke/Documents/C#/MeltedIce";
	private static final String MAPS_FOLDER = "maps";
	private static final String SPRITES_FOLDER = "sprites";
	
	private static final String PROJECT_FILE = "MeltedIce.csproj";
	private static final String PALETTE_IMAGE_EXTENSION = ".png";
	private static final String PALETTE_EXTENSION = ".pal";
	private static final String MAP_INSTANCES_EXTENSION = ".sprites";
	private static final String MAP_EXTENSION = ".map";
	
	private static final MKZFormat FORMAT = new MKZFormat();
	
	public static void main(String[] args) throws IOException {
		final Project project = open(new File(SOURCE_PATH));
		final File root = new File(DEPLOY_PATH);
		
		deploy(project, root);
	}
	
	public static void deploy(Project project, File root) throws IOException {
		final File mapsFolder = isCSharp(root) ? new File(root, MAPS_FOLDER) : root;
		final File spritesFolder = isCSharp(root) ? new File(root, SPRITES_FOLDER) : root;
		
		FORMAT.setVersion(InternalFormat.LAST_VERSION);
		
		final List<String> contents = new ArrayList<String>();
		
		deploySprites(project.getSprites(), spritesFolder, contents);
		deployPalettes(project.getPalettes(), mapsFolder, contents);
		deployMaps(project.getMaps(), project, mapsFolder, contents);
		
		if(isCSharp(root)) {
			try {
				VSProjectWriter.write(project, root, contents, new FileOutputStream(new File(root, PROJECT_FILE)));
			} catch (XMLStreamException ex) {
				throw new IOException("Erreur lors de l'écriture du fichier de projet VisualStudio.", ex);
			}
		}
	}
	
	public static boolean accept(File file) {
		return isCSharp(file) || isSwift(file);
	}
	
	private static boolean isCSharp(File file) {
		final File mapsFolder = new File(file, MAPS_FOLDER);
		final File spritesFolder = new File(file, SPRITES_FOLDER);
		
		return file.isDirectory() && mapsFolder.isDirectory() && spritesFolder.isDirectory();
	}
	
	private static boolean isSwift(File file) {
		return file.isDirectory() && new File(file, "Images.xcassets").isDirectory();
	}
	
	private static void deploySprites(Collection<Sprite> sprites, File folder, List<String> files) throws IOException {
		final PackMap packMap = PackMap.packSprites(sprites, 1);
		if(packMap != null) {
			// Image
			final DataHandler<BufferedImage> imageDataHandler = FORMAT.getHandler(BufferedImage.class);
			
			final File imageFile = new File(folder, fr.rca.mapmaker.io.mkz.ProjectDataHandler.IMAGE_FILE_NAME);
			final FileOutputStream imageOutputStream = new FileOutputStream(imageFile);
			try {
				imageDataHandler.write(packMap.renderImage(), imageOutputStream);
				addFile(imageFile, files);
			} finally {
				imageOutputStream.close();
			}
			
			// Atlas
			final DataHandler<PackMap> packMapDataHandler = FORMAT.getHandler(PackMap.class);
			
			final File dataFile = new File(folder, fr.rca.mapmaker.io.mkz.ProjectDataHandler.DATA_FILE_NAME);
			final FileOutputStream dataOutputStream = new FileOutputStream(dataFile);
			try {
				packMapDataHandler.write(packMap, dataOutputStream);
				addFile(dataFile, files);
			} finally {
				dataOutputStream.close();
			}
		}
	}
	
	private static void deployPalettes(List<Palette> palettes, File folder, List<String> files) throws IOException {
		final DataHandler<Palette> paletteDataHandler = FORMAT.getHandler(Palette.class);
		final DataHandler<BufferedImage> imageHandler = FORMAT.getHandler(BufferedImage.class);
		
		for (Palette palette : palettes) {
			final String baseName = getBaseName(palette);
			
			final File paletteFile = new File(folder, baseName + PALETTE_EXTENSION);
			final FileOutputStream paletteOutputStream = new FileOutputStream(paletteFile);
			try {
				paletteDataHandler.write(palette, paletteOutputStream);
				addFile(paletteFile, files);
			} finally {
				paletteOutputStream.close();
			}

			// Écriture des images.
			final File imageFile = new File(folder, palette.toString() + '-' + palette.getTileSize() + PALETTE_IMAGE_EXTENSION);
			final BufferedImage image = fr.rca.mapmaker.io.mkz.ProjectDataHandler.renderPalette(palette, palette.getTileSize());
			final FileOutputStream imageOutputStream = new FileOutputStream(imageFile);
			try {
				imageHandler.write(image, imageOutputStream);
				addFile(imageFile, files);
			} finally {
				imageOutputStream.close();
			}
		}
	}
	
	private static void deployMaps(List<TileMap> maps, Project project, File folder, List<String> files) throws IOException {
		final DataHandler<TileMap> tileMapHandler = FORMAT.getHandler(TileMap.class);
		final DataHandler<Instance> instanceHandler = FORMAT.getHandler(Instance.class);
		
		for(int index = 0; index < maps.size(); index++) {
			final TileMap map = maps.get(index);
			final String baseName = getBaseName(map);
			
			if(baseName != null) {
				final List<Instance> instances = new ArrayList<Instance>(project.getAllInstances().get(index));
				Collections.sort(instances, new Comparator<Instance>() {

					@Override
					public int compare(Instance o1, Instance o2) {
						int order = Integer.valueOf(o1.getX()).compareTo(o2.getX());
						if(order == 0) {
							return Integer.valueOf(o1.getY()).compareTo(o2.getY());
						} else {
							return order;
						}
					}
				});


				// Écriture de la carte
				final File mapFile = new File(folder, baseName + MAP_EXTENSION);
				final FileOutputStream mapOutputStream = new FileOutputStream(mapFile);
				try {
					tileMapHandler.write(map, mapOutputStream);
					addFile(mapFile, files);
				} finally {
					mapOutputStream.close();
				}

				// Écriture des instances
				final File instancesFile = new File(folder, baseName + MAP_INSTANCES_EXTENSION);
				final FileOutputStream instancesOutputStream = new FileOutputStream(instancesFile);
				try {
					Streams.write(instances.size(), instancesOutputStream);
					for(final Instance instance : instances) {
						instanceHandler.write(instance, instancesOutputStream);
					}
					addFile(instancesFile, files);
				} finally {
					instancesOutputStream.close();
				}
			}
		}
	}
	
	private static Project open(File file) {
		final Format format = Formats.getFormat(file.getName());
		
		if(format != null) {
			return format.openProject(file);
		} else {
			throw new UnsupportedOperationException("Format non reconnu.");
		}
	}
	
	private static void addFile(File file, List<String> files) {
		files.add(file.getParentFile().getName() + '\\' + file.getName());
	}
	
	private static String getBaseName(TileMap map) {
		return getBaseName(map.getName());
	}
	
	private static String getBaseName(Palette palette) {
		return getBaseName(palette.toString());
	}
	
	private static String getBaseName(String name) {
		if(name == null) {
			return name;
		}
		
		return name.toLowerCase().replace(' ', '-');
	}
}
