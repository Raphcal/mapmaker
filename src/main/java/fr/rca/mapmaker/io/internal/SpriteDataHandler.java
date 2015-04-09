package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
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
public class SpriteDataHandler implements DataHandler<Sprite>, HasVersion {
	
	private final Format format;
	private int version;

	public SpriteDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public void write(Sprite t, OutputStream outputStream) throws IOException {
		Streams.write(t.getWidth(), outputStream);
		Streams.write(t.getHeight(), outputStream);
		
		final Set<Animation> animations = t.getAnimations();
		Streams.write(animations.size(), outputStream);
		
		final DataHandler<Animation> animationHandler = format.getHandler(Animation.class);
		for(final Animation animation : animations) {
			animationHandler.write(animation, outputStream);
		}
	}
	
	@Override
	public Sprite read(InputStream inputStream) throws IOException {
		final int width, height;
		
		if(version >= InternalFormat.VERSION_4) {
			width = Streams.readInt(inputStream);
			height = Streams.readInt(inputStream);
			
		} else {
			width = Streams.readInt(inputStream);
			height = width;
		}
		
		final HashSet<Animation> animations = new HashSet<Animation>();
		
		final DataHandler<Animation> animationHandler = format.getHandler(Animation.class);
		final int animationCount = Streams.readInt(inputStream);
		
		for(int animation = 0; animation < animationCount; animation++) {
			animations.add(animationHandler.read(inputStream));
		}
		
		return new Sprite(width, height, animations);
	}
	
}
