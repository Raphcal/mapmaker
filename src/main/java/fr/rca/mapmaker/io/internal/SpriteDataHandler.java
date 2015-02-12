package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
		
		final DataHandler<Animation> animationHandler = format.getHandler(Animation.class);
		for(int animationCount = Streams.readInt(inputStream); animationCount > 0; animationCount--) {
			animations.add(animationHandler.read(inputStream));
		}
		
		return new Sprite(size, animations);
	}
	
}
