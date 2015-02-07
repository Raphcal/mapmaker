package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class SpriteDataHandler implements DataHandler<Sprite> {
	
	private final Format format;

	public SpriteDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void write(Sprite t, OutputStream outputStream) throws IOException {
		Streams.write(t.getSize(), outputStream);
		
		final Map<String, List<TileLayer>> animations = t.getAnimations();
		Streams.write(animations.size(), outputStream);
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		
		for(Map.Entry<String, List<TileLayer>> animation : animations.entrySet()) {
			Streams.write(animation.getKey(), outputStream);
			Streams.write(animation.getValue().size(), outputStream);
			
			for(final TileLayer frame : animation.getValue()) {
				tileLayerHandler.write(frame, outputStream);
			}
		}
	}
	
	@Override
	public Sprite read(InputStream inputStream) throws IOException {
		final int size = Streams.readInt(inputStream);
		
		final Map<String, List<TileLayer>> animations = new HashMap<String, List<TileLayer>>();
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		
		for(int animationCount = Streams.readInt(inputStream); animationCount > 0; animationCount--) {
			final String name = Streams.readString(inputStream);
			
			final ArrayList<TileLayer> frames = new ArrayList<TileLayer>();
			for(int frameCount = Streams.readInt(inputStream); frameCount > 0; frameCount--) {
				frames.add(tileLayerHandler.read(inputStream));
			}
			
			animations.put(name, frames);
		}
		
		return new Sprite(size, animations);
	}
	
}
