package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class AnimationDataHandler implements DataHandler<Animation> {
	
	@Override
	public void write(Animation animation, OutputStream outputStream) throws IOException {
		Streams.write(animation.getName(), outputStream);
		Streams.write(animation.getFrequency(), outputStream);
		Streams.write(animation.isLooping(), outputStream);

		final Set<Map.Entry<Double, List<TileLayer>>> directions = animation.getFrames().entrySet();
		Streams.write(directions.size(), outputStream);

		for(final Map.Entry<Double, List<TileLayer>> direction : directions) {
			Streams.write((double) direction.getKey(), outputStream);

			final List<TileLayer> frames = direction.getValue();
			Streams.write(frames.size(), outputStream);

			for(final TileLayer frame : frames) {
				// TODO: Passer PackMap d'une certaine façon ou intégrer le point directement dans la frame.
//				final Point point = packMap.getPoint(maps.get(frame));
//
//				Streams.write(point.x, outputStream);
//				Streams.write(point.y, outputStream);
//				Streams.write(frame.getWidth(), outputStream);
//				Streams.write(frame.getHeight(), outputStream);
			}
		}
	}

	@Override
	public Animation read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}
	
}
