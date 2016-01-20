package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.LayerPlugin;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.SingleLayerTileMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PackMapDataHandler implements DataHandler<PackMap>, HasVersion {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PackMapDataHandler.class);
	
	private final Format format;
	private int version;

	public PackMapDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void write(PackMap t, OutputStream outputStream) throws IOException {
		final Collection<Sprite> sprites = t.getSprites();
		final Map<TileLayer, SingleLayerTileMap> maps = t.getTileLayerToTileMap();
		
		Streams.write(sprites.size(), outputStream);
		
		for(final Sprite sprite : sprites) {
			Streams.writeNullable(sprite.getName(), outputStream);
			Streams.write(sprite.getWidth(), outputStream);
			Streams.write(sprite.getHeight(), outputStream);
			Streams.write(sprite.getType(), outputStream);
			Streams.write(sprite.getDistance().ordinal(), outputStream);
			// INFO: Suppression du script de chargement.
			Streams.writeNullable(sprite.getScriptFile(), outputStream);

			final Animation[] defaultAnimations = Animation.getDefaultAnimations();
			Streams.write(defaultAnimations.length, outputStream);

			for(Animation defaultAnimation : defaultAnimations) {
				final Animation animation = sprite.get(defaultAnimation.getName());

				Streams.write(animation.getName(), outputStream);
				Streams.write(animation.getFrequency(), outputStream);
				Streams.write(animation.isLooping(), outputStream);
				
				if (!animation.isScrolling()) {
					writeAnimation(animation, t, maps, outputStream);
				} else {
					writeScrollingAnimation(animation, sprite, t, maps, outputStream);
				}
			}
		}
	}

	private void writeAnimation(final Animation animation, final PackMap t, final Map<TileLayer, SingleLayerTileMap> maps, final OutputStream outputStream) throws IOException {
		final Set<Double> directions = animation.getAnglesWithValue();
		Streams.write(directions.size(), outputStream);
		
		final DataHandler<Rectangle> rectangleHandler = format.getHandler(Rectangle.class);
		
		for(final double direction : directions) {
			Streams.write(direction, outputStream);
			
			final List<TileLayer> frames = animation.getFrames(direction);
			Streams.write(frames.size(), outputStream);
			
			for(final TileLayer frame : frames) {
				Point point = t.getPoint(maps.get(frame));
				
				if (point == null) {
					LOGGER.warn("Frame " + frame + " non trouvée dans PackMap.");
					point = new Point();
				}
				
				Streams.write(point.x, outputStream);
				Streams.write(point.y, outputStream);
				Streams.write(frame.getWidth(), outputStream);
				Streams.write(frame.getHeight(), outputStream);
				
				if (version >= InternalFormat.VERSION_8) {
					final LayerPlugin plugin = frame.getPlugin();
					final Rectangle hitbox;
					if (plugin instanceof HitboxLayerPlugin) {
						hitbox = ((HitboxLayerPlugin) plugin).getHitbox();
					} else {
						hitbox = null;
					}
					rectangleHandler.write(hitbox, outputStream);
				}
			}
		}
	}
	
	private void writeScrollingAnimation(final Animation animation, final Sprite sprite, final PackMap t, final Map<TileLayer, SingleLayerTileMap> maps, final OutputStream outputStream) throws IOException {
		// TODO: Gérer toutes les directions ?
		final List<TileLayer> frames = animation.getFrames(0.0);
		
		final DataHandler<Rectangle> rectangleHandler = format.getHandler(Rectangle.class);
		
		if (frames != null && !frames.isEmpty()) {
			Streams.write(1, outputStream);
			
			final TileLayer frame = frames.get(0);
			
			final int frameCount = Math.max(sprite.getHeight() - frame.getHeight(), 1);
			Streams.write(frameCount, outputStream);
			
			Point point = t.getPoint(maps.get(frame));
			
			if (point == null) {
				LOGGER.warn("Frame " + frame + " non trouvée dans PackMap.");
				point = new Point();
			}
			
			for (int index = 0; index < frameCount; index++) {
				Streams.write(point.x, outputStream);
				Streams.write(point.y + frame.getHeight() - sprite.getHeight() - index, outputStream);
				Streams.write(frame.getWidth(), outputStream);
				Streams.write(frame.getHeight(), outputStream);
				
				if (version >= InternalFormat.VERSION_8) {
					// Hitbox non supporté.
					rectangleHandler.write(null, outputStream);
				}
			}
			
		} else {
			Streams.write(0, outputStream);
		}
	}

	@Override
	public PackMap read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}
	
}
