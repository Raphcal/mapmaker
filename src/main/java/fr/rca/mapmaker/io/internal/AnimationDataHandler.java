package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import java.awt.Rectangle;
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
public class AnimationDataHandler implements DataHandler<Animation>, HasVersion {
	
	private final Format format;
	private int version;

	public AnimationDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public void write(Animation t, OutputStream outputStream) throws IOException {
		Streams.write(t.getName(), outputStream);
		Streams.write(t.getFrequency(), outputStream);
		if(version >= InternalFormat.VERSION_4) {
			Streams.write(t.isLooping(), outputStream);
		}
		if(version >= InternalFormat.VERSION_9) {
			Streams.write(t.isScrolling(), outputStream);
		}
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		final DataHandler<Rectangle> rectangleHandler = format.getHandler(Rectangle.class);
		
		t.overrideFrameNames();
		
		final Set<Map.Entry<Double, List<TileLayer>>> directions = t.getFrames().entrySet();
		Streams.write(directions.size(), outputStream);
		
		for(Map.Entry<Double, List<TileLayer>> entry : directions) {
			final double direction = entry.getKey();
			final List<TileLayer> frames = entry.getValue();
			
			Streams.write(direction, outputStream);
			Streams.write(frames.size(), outputStream);
			
			for(final TileLayer frame : frames) {
				tileLayerHandler.write(frame, outputStream);
				
				if (version >= InternalFormat.VERSION_8) {
					final HitboxLayerPlugin plugin = frame.getPlugin(HitboxLayerPlugin.class);
					final Rectangle hitbox;
					if (plugin != null) {
						hitbox = plugin.getHitbox();
					} else {
						hitbox = null;
					}
					rectangleHandler.write(hitbox, outputStream);
				}
			}
		}
	}

	@Override
	public Animation read(InputStream inputStream) throws IOException {
		final Animation animation = new Animation(Streams.readString(inputStream));
		animation.setFrequency(Streams.readInt(inputStream));
		if(version >= InternalFormat.VERSION_4) {
			animation.setLooping(Streams.readBoolean(inputStream));
		}
		if(version >= InternalFormat.VERSION_9) {
			animation.setScrolling(Streams.readBoolean(inputStream));
		}
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		final DataHandler<Rectangle> rectangleHandler = format.getHandler(Rectangle.class);
		
		final int directionCount = Streams.readInt(inputStream);
		for(int i = 0; i < directionCount; i++) {
			final double direction = Streams.readDouble(inputStream);
			final int frameCount = Streams.readInt(inputStream);
			
			final List<TileLayer> frames = new ArrayList<TileLayer>();
			
			for(int frame = 0; frame < frameCount; frame++) {
				final TileLayer frameLayer = tileLayerHandler.read(inputStream);
				
				final HitboxLayerPlugin hitboxPlugin = new HitboxLayerPlugin();
				if (version >= InternalFormat.VERSION_8) {
					hitboxPlugin.setHitbox(rectangleHandler.read(inputStream));
				}
				
				frameLayer.setPlugin(hitboxPlugin);
				frames.add(frameLayer);
			}
			
			animation.setFrames(direction, frames);
		}
		
		return animation;
	}
}
