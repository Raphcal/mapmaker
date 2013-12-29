package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import java.awt.Color;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileMapDataHandler implements DataHandler<TileMap> {

	private Format format;

	public TileMapDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void write(TileMap t, OutputStream outputStream) throws IOException {
		
		// Fond
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
		Color backgroundColor = t.getBackgroundColor();
		if(backgroundColor == null) {
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
		
		for(final Layer layer : layers)
			layerHandler.write((TileLayer)layer, outputStream);
	}

	@Override
	public TileMap read(InputStream inputStream) throws IOException {
		
		final TileMap tileMap = new TileMap();
		
		// Fond
		final DataHandler<Color> colorHandler = format.getHandler(Color.class);
		tileMap.setBackgroundColor(colorHandler.read(inputStream));
		
		// Palette
		final DataHandler<Palette> paletteHandler = format.getHandler(Palette.class);
		tileMap.setPalette(paletteHandler.read(inputStream));
		
		// Layers
		final DataHandler<TileLayer> layerHandler = format.getHandler(TileLayer.class);
		
		final int layerCount = Streams.readInt(inputStream);
		for(int index = 0; index < layerCount; index++) {
			final TileLayer layer = layerHandler.read(inputStream);
//			layer.setParent(tileMap);
			
			tileMap.add(layer);
		}
		
		return tileMap;
	}
	
}
