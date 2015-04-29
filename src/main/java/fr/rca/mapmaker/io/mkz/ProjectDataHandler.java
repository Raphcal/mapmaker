package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectDataHandler implements DataHandler<Project> {
	
	private final Format format;

	public ProjectDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void write(Project t, OutputStream outputStream) throws IOException {
		final ZipOutputStream zipOutputStream = (ZipOutputStream)outputStream;
		
		// Palettes
		final List<Palette> palettes = t.getPalettes();
		
		final DataHandler<Palette> paletteDataHandler = format.getHandler(Palette.class);
		
		for(int index = 0; index < palettes.size(); index++) {
			final Palette palette = palettes.get(index);
			
			final ZipEntry entry = new ZipEntry("palette" + index + ".pal");
			zipOutputStream.putNextEntry(entry);
			paletteDataHandler.write(palette, outputStream);
			zipOutputStream.closeEntry();
		}
		
		// Cartes
		final List<TileMap> maps = t.getMaps();
		
		final DataHandler<TileMap> tileMapHandler = format.getHandler(TileMap.class);
		final DataHandler<Instance> instanceHandler = format.getHandler(Instance.class);
		
		for(int index = 0; index < maps.size(); index++) {
			final TileMap map = maps.get(index);
			final List<Instance> instances = t.getAllInstances().get(index);
			
			// Écriture de la carte
			final ZipEntry mapEntry = new ZipEntry("map" + index + ".map");
			zipOutputStream.putNextEntry(mapEntry);
			tileMapHandler.write(map, outputStream);
			zipOutputStream.closeEntry();
			
			// Écriture des instances
			final ZipEntry instancesEntry = new ZipEntry("map" + index + ".sprites");
			zipOutputStream.putNextEntry(instancesEntry);
			
			Streams.write(instances.size(), outputStream);
			for(final Instance instance : instances) {
				instanceHandler.write(instance, outputStream);
			}
			zipOutputStream.closeEntry();
		}
		
		// Sprites
		final List<Sprite> sprites = t.getSprites();
		
		// Feuille contenant tous les sprites
		final SpriteAtlas atlas = new SpriteAtlas(format);
		atlas.write(sprites, zipOutputStream);
		
		// Feuilles de sprites individuelles
		final DataHandler<Sprite> spriteHandler = format.getHandler(Sprite.class);
		
		for(int index = 0; index < sprites.size(); index++) {
			final Sprite sprite = sprites.get(index);
			
			if(!sprite.isEmpty()) {
				final ZipEntry entry = new ZipEntry("sprite" + index + ".png");
				zipOutputStream.putNextEntry(entry);
				spriteHandler.write(sprite, outputStream);
				zipOutputStream.closeEntry();
			}
		}
	}
	
	@Override
	public Project read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("NIY");
	}
	
}
