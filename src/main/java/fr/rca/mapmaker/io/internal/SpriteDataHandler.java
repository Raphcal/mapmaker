package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;

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
		
		final Set<Animation> animations = t.getAnimations();
		Streams.write(animations.size(), outputStream);
		
		final DataHandler<Animation> animationHandler = format.getHandler(Animation.class);
		for(final Animation animation : animations) {
			animationHandler.write(animation, outputStream);
		}
	}
	
	@Override
	public Sprite read(InputStream inputStream) throws IOException {
		final int size = Streams.readInt(inputStream);
		
		final HashSet<Animation> animations = new HashSet<Animation>();
		
		final HashMap<String, Animation> defaultAnimations = new HashMap<String, Animation>();
		for(final Animation defaultAnimation : Animation.getDefaultAnimations()) {
			defaultAnimations.put(defaultAnimation.getName(), defaultAnimation);
		}
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		
		for(int animationCount = Streams.readInt(inputStream); animationCount > 0; animationCount--) {
			final String name = Streams.readString(inputStream);
			
			final int separatorIndex = name.indexOf('-');
			final Animation animation = defaultAnimations.get(name.substring(0, separatorIndex));
			animations.add(animation);
			
			final double direction = Double.valueOf(name.substring(separatorIndex + 1));
			
			final ArrayList<TileLayer> frames = new ArrayList<TileLayer>();
			for(int frameCount = Streams.readInt(inputStream); frameCount > 0; frameCount--) {
				frames.add(tileLayerHandler.read(inputStream));
			}
			
			animation.setFrames(direction, frames);
		}
		
		return new Sprite(size, animations);
		
//		final int size = Streams.readInt(inputStream);
//		
//		final HashSet<Animation> animations = new HashSet<Animation>();
//		
//		final DataHandler<Animation> animationHandler = format.getHandler(Animation.class);
//		for(int animationCount = Streams.readInt(inputStream); animationCount > 0; animationCount--) {
//			animations.add(animationHandler.read(inputStream));
//		}
//		
//		return new Sprite(size, animations);
	}
	
}
