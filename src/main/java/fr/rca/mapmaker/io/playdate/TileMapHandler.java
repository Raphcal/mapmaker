package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileMap;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapHandler implements DataHandler<TileMap> {

	@Override
	public void write(TileMap t, OutputStream outputStream) throws IOException {
		final List<Layer> layers = t.getLayers();
		Streams.write(layers.size(), outputStream);

		for(final Layer layer : layers) {
			final int width = layer.getWidth();
			final int height = layer.getHeight();
			Streams.write(width, outputStream);
			Streams.write(height, outputStream);
			Streams.write((float)layer.getScrollRate().getX(), outputStream);
			Streams.write((float)layer.getScrollRate().getY(), outputStream);
			Streams.write(layer.isSolid(), outputStream);
			int max = width * height;
			for (int index = 0; index < max; index++) {
				Streams.write(layer.getTile(index % width, index / width), outputStream);
			}
		}
	}

	@Override
	public TileMap read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}
	
}
