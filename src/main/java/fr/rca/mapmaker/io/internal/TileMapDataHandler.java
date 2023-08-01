package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileMapDataHandler implements DataHandler<TileMap>, HasVersion {

	private final Format format;
	private int version;

	public TileMapDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public void write(TileMap t, OutputStream outputStream) throws IOException {
		if (version >= InternalFormat.VERSION_7) {
			Streams.write(t.getIndex(), outputStream);
		}

		if (version >= InternalFormat.VERSION_6) {
			Streams.writeNullable(t.getName(), outputStream);
		}

		// Fond
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
		Color backgroundColor = t.getBackgroundColor();
		if (backgroundColor == null) {
			// TODO: Plutôt faire un booléen ou équivalent
			backgroundColor = new Color(255, 255, 255);
		}
		colorHandler.write(backgroundColor, outputStream);

		// Palette
		final DataHandler<Palette> paletteHandler = format.getHandler(Palette.class);
		paletteHandler.write(t.getPalette(), outputStream);

		// Layers
		final DataHandler<TileLayer> layerHandler = format.getHandler(TileLayer.class);

		final List<Layer> layers = t.getLayers();
		Streams.write(layers.size(), outputStream);

		for (final Layer layer : layers) {
			layerHandler.write((TileLayer) layer, outputStream);
		}

		if (version >= InternalFormat.VERSION_16) {
			Streams.write(t.isExportable(), outputStream);
		}
	}

	@Override
	public TileMap read(InputStream inputStream) throws IOException {
		final TileMap tileMap = new TileMap();

		// Index
		if (version >= InternalFormat.VERSION_7) {
			tileMap.setIndex(Streams.readInt(inputStream));
		}

		// Nom
		if (version >= InternalFormat.VERSION_6) {
			tileMap.setName(Streams.readNullableString(inputStream));
		}

		// Fond
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
		tileMap.setBackgroundColor(colorHandler.read(inputStream));

		// Palette
		final DataHandler<Palette> paletteHandler = format.getHandler(Palette.class);
		tileMap.setPalette(paletteHandler.read(inputStream));

		// Layers
		final DataHandler<TileLayer> layerHandler = format.getHandler(TileLayer.class);

		final int layerCount = Streams.readInt(inputStream);
		for (int index = 0; index < layerCount; index++) {
			final TileLayer layer = layerHandler.read(inputStream);
//			layer.setParent(tileMap);

			tileMap.add(layer);
		}

		if (version >= InternalFormat.VERSION_16) {
			tileMap.setExportable(Streams.readBoolean(inputStream));
		} else {
			tileMap.setExportable(true);
		}

		return tileMap;
	}

}
