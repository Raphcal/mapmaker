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
import java.util.Comparator;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class MeltedIceAutoDeploy {
	private static final String SOURCE_PATH = "/Users/daeke/Documents/Java/mapmaker/mmkb/MeltedIce.mmkb";
	private static final String DEPLOY_PATH = "/Users/daeke/Documents/C#/MeltedIce";
	private static final String MAPS_FOLDER = "maps";
	private static final String SPRITES_FOLDER = "sprites";
	
	private static final String[] MAP_NAMES = {
		"title",
		"map1",
		"map2",
		"map3",
		"map4",
		"map5"
	};
	
	private static final String[] PALETTE_NAMES = {
		null,
		"palette2.pal",
		"title.pal",
		"palette3.pal",
		"palette4.pal",
		"palette5.pal"
	};
	
	private static final MKZFormat FORMAT = new MKZFormat();
	
	public static void main(String[] args) throws IOException {
		final Project project = open(new File(SOURCE_PATH));
		final File root = new File(DEPLOY_PATH);
		
		deploy(project, root);
	}
	
	public static void deploy(Project project, File root) throws IOException {
		final File mapsFolder = new File(root, MAPS_FOLDER);
		final File spritesFolder = new File(root, SPRITES_FOLDER);
		
		FORMAT.setVersion(InternalFormat.LAST_VERSION);
		
		deploySprites(project.getSprites(), spritesFolder);
		deployPalettes(project.getPalettes(), mapsFolder);
		deployMaps(project.getMaps(), project, mapsFolder);
	}
	
	public static boolean accept(File file) {
		final File mapsFolder = new File(file, MAPS_FOLDER);
		final File spritesFolder = new File(file, SPRITES_FOLDER);
		
		return file.isDirectory() && mapsFolder.isDirectory() && spritesFolder.isDirectory();
	}
	
	private static void deploySprites(Collection<Sprite> sprites, File folder) throws IOException {
		final PackMap packMap = PackMap.packSprites(sprites, 1);
		if(packMap != null) {
			// Image
			final DataHandler<BufferedImage> imageDataHandler = FORMAT.getHandler(BufferedImage.class);
			
			final FileOutputStream imageOutputStream = new FileOutputStream(new File(folder, fr.rca.mapmaker.io.mkz.ProjectDataHandler.IMAGE_FILE_NAME));
			try {
				imageDataHandler.write(packMap.renderImage(), imageOutputStream);
			} finally {
				imageOutputStream.close();
			}
			
			// Atlas
			final DataHandler<PackMap> packMapDataHandler = FORMAT.getHandler(PackMap.class);
			
			final FileOutputStream dataOutputStream = new FileOutputStream(new File(folder, fr.rca.mapmaker.io.mkz.ProjectDataHandler.DATA_FILE_NAME));
			try {
				packMapDataHandler.write(packMap, dataOutputStream);
			} finally {
				dataOutputStream.close();
			}
		}
	}
	
	private static void deployPalettes(List<Palette> palettes, File folder) throws IOException {
		final DataHandler<Palette> paletteDataHandler = FORMAT.getHandler(Palette.class);
		final DataHandler<BufferedImage> imageHandler = FORMAT.getHandler(BufferedImage.class);
		
		for(int index = 0; index < palettes.size(); index++) {
			final String name = index < PALETTE_NAMES.length ? PALETTE_NAMES[index] : null;
			if(name != null) {
				final Palette palette = palettes.get(index);

				final FileOutputStream paletteOutputStream = new FileOutputStream(new File(folder, name));
				try {
					paletteDataHandler.write(palette, paletteOutputStream);
				} finally {
					paletteOutputStream.close();
				}

				// Écriture des images.
				final BufferedImage image = fr.rca.mapmaker.io.mkz.ProjectDataHandler.renderPalette(palette, palette.getTileSize());
				final FileOutputStream imageOutputStream = new FileOutputStream(new File(folder, palette.toString() + '-' + palette.getTileSize() + ".png"));
				try {
					imageHandler.write(image, imageOutputStream);
				} finally {
					imageOutputStream.close();
				}
			}
		}
	}
	
	private static void deployMaps(List<TileMap> maps, Project project, File folder) throws IOException {
		final DataHandler<TileMap> tileMapHandler = FORMAT.getHandler(TileMap.class);
		final DataHandler<Instance> instanceHandler = FORMAT.getHandler(Instance.class);
		
		for(int index = 0; index < maps.size(); index++) {
			final String baseName = index < MAP_NAMES.length ? MAP_NAMES[index] : null;
			
			if(baseName != null) {
				final TileMap map = maps.get(index);
				final List<Instance> instances = new ArrayList<Instance>(project.getAllInstances().get(index));
				instances.sort(new Comparator<Instance>() {

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
				final FileOutputStream mapOutputStream = new FileOutputStream(new File(folder, baseName + ".map"));
				try {
					tileMapHandler.write(map, mapOutputStream);
				} finally {
					mapOutputStream.close();
				}

				// Écriture des instances
				final FileOutputStream instancesOutputStream = new FileOutputStream(new File(folder, baseName + ".sprites"));
				try {
					Streams.write(instances.size(), instancesOutputStream);
					for(final Instance instance : instances) {
						instanceHandler.write(instance, instancesOutputStream);
					}
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
}
