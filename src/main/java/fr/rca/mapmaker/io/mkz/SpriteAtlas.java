package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Set;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class SpriteAtlas {
	
	private static final String IMAGE_FILE_NAME = "atlas.png";
	private static final String DATA_FILE_NAME = "atlas.sprites";
	
	private final Format format;

	public SpriteAtlas(Format format) {
		this.format = format;
	}
	
	public void write(List<Sprite> sprites, ZipOutputStream zipOutputStream) throws IOException {
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
		
		final PackMap packMap = PackMap.packAll(maps.values(), 1);
		if(packMap != null) {
			final DataHandler<BufferedImage> imageHandler = format.getHandler(BufferedImage.class);
			final BufferedImage atlasImage = packMap.renderImage();
			
			final ZipEntry atlasEntry = new ZipEntry(IMAGE_FILE_NAME);
			zipOutputStream.putNextEntry(atlasEntry);
			imageHandler.write(atlasImage, zipOutputStream);
			zipOutputStream.closeEntry();
			
			final ZipEntry locationEntry = new ZipEntry(DATA_FILE_NAME);
			zipOutputStream.putNextEntry(locationEntry);
			
			Streams.write(sprites.size(), zipOutputStream);
			
			for(final Sprite sprite : sprites) {
				Streams.writeNullable(sprite.getName(), zipOutputStream);
				Streams.write(sprite.getWidth(), zipOutputStream);
				Streams.write(sprite.getHeight(), zipOutputStream);
				Streams.write(sprite.getType(), zipOutputStream);
				Streams.writeNullable(sprite.getLoadScript(), zipOutputStream);
				Streams.writeNullable(sprite.getScriptFile(), zipOutputStream);
				
				final Animation[] defaultAnimations = Animation.getDefaultAnimations();
				Streams.write(defaultAnimations.length, zipOutputStream);
				
				for(Animation defaultAnimation : defaultAnimations) {
					final Animation animation = sprite.get(defaultAnimation.getName());
					
					Streams.write(animation != null, zipOutputStream);
					
					if(animation != null) {
						Streams.write(animation.getName(), zipOutputStream);
						Streams.write(animation.getFrequency(), zipOutputStream);
						Streams.write(animation.isLooping(), zipOutputStream);

						final Set<Double> directions = animation.getAnglesWithValue();
						Streams.write(directions.size(), zipOutputStream);

						for(final double direction : directions) {
							Streams.write(direction, zipOutputStream);

							final List<TileLayer> frames = animation.getFrames(direction);
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
			}
			
			zipOutputStream.closeEntry();
		}
	}
}
