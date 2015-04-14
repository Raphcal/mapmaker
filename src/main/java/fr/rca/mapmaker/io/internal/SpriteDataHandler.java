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
		// Nom
		final boolean hasName = t.getName() != null;
		Streams.write(hasName, outputStream);
		if(hasName) {
			Streams.write(t.getName(), outputStream);
		}
		
		// Général
		Streams.write(t.getWidth(), outputStream);
		Streams.write(t.getHeight(), outputStream);
		Streams.write(t.getType(), outputStream);
		
		// Script
		final boolean hasScriptFile = t.getScriptFile() != null;
		Streams.write(hasScriptFile, outputStream);
		if(hasScriptFile) {
			Streams.write(t.getScriptFile(), outputStream);
		}
		
		final Set<Animation> animations = t.getAnimations();
		Streams.write(animations.size(), outputStream);
		
		final DataHandler<Animation> animationHandler = format.getHandler(Animation.class);
		for(final Animation animation : animations) {
			animationHandler.write(animation, outputStream);
		}
	}
	
	@Override
	public Sprite read(InputStream inputStream) throws IOException {
		final String name;
		final int width, height;
		final int type;
		final String scriptFile;
		
		if(version >= InternalFormat.VERSION_4) {
			final boolean hasName = Streams.readBoolean(inputStream);
			if(hasName) {
				name = Streams.readString(inputStream);
			} else {
				name = null;
			}
			
			width = Streams.readInt(inputStream);
			height = Streams.readInt(inputStream);
			type = Streams.readInt(inputStream);
			
			final boolean hasScriptFile = Streams.readBoolean(inputStream);
			if(hasScriptFile) {
				scriptFile = Streams.readString(inputStream);
			} else {
				scriptFile = null;
			}
			
		} else if(version == InternalFormat.VERSION_4) {
			name = null;
			width = Streams.readInt(inputStream);
			height = Streams.readInt(inputStream);
			type = 0;
			scriptFile = null;
			
		} else {
			name = null;
			width = Streams.readInt(inputStream);
			height = width;
			type = 0;
			scriptFile = null;
		}
		
		final HashSet<Animation> animations = new HashSet<Animation>();
		
		final DataHandler<Animation> animationHandler = format.getHandler(Animation.class);
		final int animationCount = Streams.readInt(inputStream);
		
		for(int animation = 0; animation < animationCount; animation++) {
			animations.add(animationHandler.read(inputStream));
		}
		
		return new Sprite(name, width, height, type, scriptFile, animations);
	}
	
}
