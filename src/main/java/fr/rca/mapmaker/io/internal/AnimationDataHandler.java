package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class AnimationDataHandler implements DataHandler<Animation> {
	
	private final Format format;

	public AnimationDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void write(Animation t, OutputStream outputStream) throws IOException {
		Streams.write(t.getName(), outputStream);
		Streams.write(t.getFrequency(), outputStream);
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		
		t.overrideFrameNames();
		
		final Set<Map.Entry<Double, List<TileLayer>>> entries = t.getFrames().entrySet();
		Streams.write(entries.size(), outputStream);
		
		for(Map.Entry<Double, List<TileLayer>> entry : entries) {
			final double direction = entry.getKey();
			final List<TileLayer> frames = entry.getValue();
			
			Streams.write(direction, outputStream);
			Streams.write(frames.size(), outputStream);
			
			for(final TileLayer frame : frames) {
				tileLayerHandler.write(frame, outputStream);
			}
		}
	}

	@Override
	public Animation read(InputStream inputStream) throws IOException {
		final Animation animation = new Animation(Streams.readString(inputStream));
		animation.setFrequency(Streams.readInt(inputStream));
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		
		final int directionCount = Streams.readInt(inputStream);
		for(int i = 0; i < directionCount; i++) {
			final double direction = Streams.readDouble(inputStream);
			final int frameCount = Streams.readInt(inputStream);
			
			final ArrayList<TileLayer> frames = new ArrayList<TileLayer>();
			
			for(int frame = 0; frame < frameCount; frame++) {
				frames.add(tileLayerHandler.read(inputStream));
			}
			
			animation.setFrames(direction, frames);
		}
		
		return animation;
	}
}
