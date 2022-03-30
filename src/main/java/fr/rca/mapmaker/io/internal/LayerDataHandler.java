package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Gère la lecture et l'écriture de <code>TileLayer</code>.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LayerDataHandler implements DataHandler<TileLayer>, HasVersion {

	private final Format format;
	private int version;

	public LayerDataHandler(Format format) {
		this.format = format;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void write(TileLayer t, OutputStream outputStream) throws IOException {
		final DataHandler<ScrollRate> scrollRateHandler = format.getHandler(ScrollRate.class);

		Streams.write(t.toString(), outputStream);
		Streams.write(t.getWidth(), outputStream);
		Streams.write(t.getHeight(), outputStream);
		scrollRateHandler.write(t.getScrollRate(), outputStream);
		if (version >= InternalFormat.VERSION_12) {
			Streams.write(t.isSolid(), outputStream);
		}
		Streams.write(t.copyData(), outputStream);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public TileLayer read(InputStream inputStream) throws IOException {
		final DataHandler<ScrollRate> scrollRateHandler = format.getHandler(ScrollRate.class);

		// TODO: trouver comment identifier les versions 0 sans nom.
		final String name;
		if (version >= 0) {
			name = Streams.readString(inputStream);
		} else {
			name = "Sans nom";
		}
		final int width = Streams.readInt(inputStream);
		final int height = Streams.readInt(inputStream);
		final ScrollRate scrollRate = scrollRateHandler.read(inputStream);
		final boolean solid = version >= InternalFormat.VERSION_12
				? Streams.readBoolean(inputStream)
				: false;
		final int[] tiles = Streams.readIntArray(inputStream);

		final TileLayer layer = new TileLayer(width, height, tiles);
		layer.setName(name);
		layer.setScrollRate(scrollRate);
		layer.setSolid(solid);

		return layer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void setVersion(int version) {
		this.version = version;
	}

}
