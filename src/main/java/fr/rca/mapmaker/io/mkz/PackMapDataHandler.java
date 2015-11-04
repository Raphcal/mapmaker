package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.SingleLayerTileMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
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
public class PackMapDataHandler implements DataHandler<PackMap> {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(PackMapDataHandler.class);
	
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
			Streams.writeNullable(sprite.getLoadScript(), outputStream);
			Streams.writeNullable(sprite.getScriptFile(), outputStream);

			final Animation[] defaultAnimations = Animation.getDefaultAnimations();
			Streams.write(defaultAnimations.length, outputStream);

			for(Animation defaultAnimation : defaultAnimations) {
				final Animation animation = sprite.get(defaultAnimation.getName());

				Streams.write(animation != null, outputStream);

				if(animation != null) {
					Streams.write(animation.getName(), outputStream);
					Streams.write(animation.getFrequency(), outputStream);
					Streams.write(animation.isLooping(), outputStream);

					final Set<Double> directions = animation.getAnglesWithValue();
					Streams.write(directions.size(), outputStream);

					for(final double direction : directions) {
						Streams.write(direction, outputStream);

						final List<TileLayer> frames = animation.getFrames(direction);
						Streams.write(frames.size(), outputStream);

						for(final TileLayer frame : frames) {
							final Point point = t.getPoint(maps.get(frame));
							
							Streams.write(point.x, outputStream);
							Streams.write(point.y, outputStream);
							Streams.write(frame.getWidth(), outputStream);
							Streams.write(frame.getHeight(), outputStream);
						}
					}
				} else {
					LOGGER.warn("Animation '" + defaultAnimation + "' vide pour le sprite '" + sprite + "'.");
				}
			}
		}
	}

	@Override
	public PackMap read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
