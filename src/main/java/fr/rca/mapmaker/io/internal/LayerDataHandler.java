package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LayerDataHandler implements DataHandler<TileLayer> {

	private final Format format;

	public LayerDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void write(TileLayer t, OutputStream outputStream) throws IOException {
		final DataHandler<ScrollRate> scrollRateHandler = format.getHandler(ScrollRate.class);
		
		Streams.write(t.toString(), outputStream);
		Streams.write(t.getWidth(), outputStream);
		Streams.write(t.getHeight(), outputStream);
		scrollRateHandler.write(t.getScrollRate(), outputStream);
		Streams.write(t.copyData(), outputStream);
	}

	@Override
	public TileLayer read(InputStream inputStream) throws IOException {
		final DataHandler<ScrollRate> scrollRateHandler = format.getHandler(ScrollRate.class);
		
		final String name = Streams.readString(inputStream);
		final int width = Streams.readInt(inputStream);
		final int height = Streams.readInt(inputStream);
		final ScrollRate scrollRate = scrollRateHandler.read(inputStream);
		final int[] tiles = Streams.readIntArray(inputStream);
		
		final TileLayer layer = new TileLayer(width, height);
		layer.setName(name);
		layer.setScrollRate(scrollRate);
		layer.restoreData(tiles, null);
		
		return layer;
	}
}
