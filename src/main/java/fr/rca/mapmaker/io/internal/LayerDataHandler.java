package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.TileLayer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LayerDataHandler implements DataHandler<TileLayer> {

	@Override
	public void write(TileLayer t, OutputStream outputStream) throws IOException {
		Streams.write(t.toString(), outputStream);
		Streams.write(t.getWidth(), outputStream);
		Streams.write(t.getHeight(), outputStream);
		Streams.write(t.getScrollRate(), outputStream);
		Streams.write(t.copyData(), outputStream);
	}

	@Override
	public TileLayer read(InputStream inputStream) throws IOException {
		final String name = Streams.readString(inputStream);
		final int width = Streams.readInt(inputStream);
		final int height = Streams.readInt(inputStream);
		final float scrollRate = Streams.readFloat(inputStream);
		final int[] tiles = Streams.readIntArray(inputStream);
		
		final TileLayer layer = new TileLayer(width, height);
		layer.setName(name);
		layer.setScrollRate(scrollRate);
		layer.restoreData(tiles, null);
		
		return layer;
	}
}
