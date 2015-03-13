package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
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
		
		for(int index = 0; index < maps.size(); index++) {
			final TileMap map = maps.get(index);
			
			final ZipEntry entry = new ZipEntry("map" + index + ".map");
			zipOutputStream.putNextEntry(entry);
			tileMapHandler.write(map, outputStream);
			zipOutputStream.closeEntry();
		}
		
		// Sprites
		final List<Sprite> sprites = t.getSprites();
		
		// Feuille contenant tous les sprites
		writeSpriteAtlas(sprites, zipOutputStream);
		
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
	
	private void writeSpriteAtlas(List<Sprite> sprites, ZipOutputStream zipOutputStream) throws IOException {
		final HashMap<TileLayer, TileMap> maps = new HashMap<TileLayer, TileMap>();
		
		for(final Sprite sprite : sprites) {
			for(final Animation animation : sprite.getAnimations()) {
				for(final List<TileLayer> frames : animation.getFrames().values()) {
					for(final TileLayer frame : frames) {
						maps.put(frame, new TileMap(frame, sprite.getPalette()));
					}
				}
			}
		}
		
		final PackMap packMap = PackMap.packAll(maps.values());
		if(packMap != null) {
			final DataHandler<BufferedImage> imageHandler = format.getHandler(BufferedImage.class);
			final BufferedImage atlasImage = packMap.renderImage();
			
			final ZipEntry atlasEntry = new ZipEntry("atlas.png");
			zipOutputStream.putNextEntry(atlasEntry);
			imageHandler.write(atlasImage, zipOutputStream);
			zipOutputStream.closeEntry();
			
			// TODO: Parcourir les sprites et écrire les informations de position pour chaque layer.
			final ZipEntry locationEntry = new ZipEntry("atlas.pos");
			zipOutputStream.putNextEntry(locationEntry);
			
			
			Streams.write(sprites.size(), zipOutputStream);
			
			int index = 0;
			for(final Sprite sprite : sprites) {
				Streams.write("sprite-" + index, zipOutputStream);
				Streams.write(sprite.getWidth(), zipOutputStream);
				Streams.write(sprite.getHeight(), zipOutputStream);
				
				index++;
				
				final Collection<Animation> animations = sprite.getAnimations();
				Streams.write(animations.size(), zipOutputStream);
				
				for(final Animation animation : animations) {
					Streams.write(animation.getName(), zipOutputStream);
					Streams.write(animation.getFrequency(), zipOutputStream);
					
					final Set<Map.Entry<Double, List<TileLayer>>> directions = animation.getFrames().entrySet();
					Streams.write(directions.size(), zipOutputStream);
					
					for(final Map.Entry<Double, List<TileLayer>> direction : directions) {
						Streams.write((double) direction.getKey(), zipOutputStream);
						
						final List<TileLayer> frames = direction.getValue();
						Streams.write(frames.size(), zipOutputStream);
						
						for(final TileLayer frame : frames) {
							final Point point = packMap.getPoint(maps.get(frame));
							
							Streams.write(point.x, zipOutputStream);
							Streams.write(point.y, zipOutputStream);
							Streams.write(frame.getWidth(), zipOutputStream);
							Streams.write(frame.getHeight(), zipOutputStream);
						}
					}
				}
			}
			
			zipOutputStream.closeEntry();
		}
	}

	@Override
	public Project read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("NIY");
	}
	
}
