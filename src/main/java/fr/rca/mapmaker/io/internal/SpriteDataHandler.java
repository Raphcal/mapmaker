package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Distance;
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
		Streams.writeNullable(t.getName(), outputStream);
		
		// Général
		Streams.write(t.getWidth(), outputStream);
		Streams.write(t.getHeight(), outputStream);
		Streams.write(t.getType(), outputStream);
		
		if (version >= InternalFormat.VERSION_9) {
			Streams.write(t.getDistance().ordinal(), outputStream);
		}
		
		// Script
		Streams.writeNullable(t.getLoadScript(), outputStream);
		Streams.writeNullable(t.getScriptFile(), outputStream);
		
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
		final Distance distance;
		final String loadScript;
		final String scriptFile;
		
		if(version >= InternalFormat.VERSION_4) {
			name = Streams.readNullableString(inputStream);
			width = Streams.readInt(inputStream);
			height = Streams.readInt(inputStream);
			type = Streams.readInt(inputStream);
			if (version >= InternalFormat.VERSION_9) {
				distance = Distance.values()[Streams.readInt(inputStream)];
			} else {
				distance = Distance.BEHIND;
			}
			loadScript = Streams.readNullableString(inputStream);
			scriptFile = Streams.readNullableString(inputStream);
			
		} else if(version == InternalFormat.VERSION_4) {
			name = null;
			width = Streams.readInt(inputStream);
			height = Streams.readInt(inputStream);
			type = 0;
			distance = Distance.BEHIND;
			loadScript = null;
			scriptFile = null;
			
		} else {
			name = null;
			width = Streams.readInt(inputStream);
			height = width;
			type = 0;
			distance = Distance.BEHIND;
			loadScript = null;
			scriptFile = null;
		}
		
		final Set<Animation> animations = new HashSet<Animation>();
		
		final DataHandler<Animation> animationHandler = format.getHandler(Animation.class);
		final int animationCount = Streams.readInt(inputStream);
		
		for(int animation = 0; animation < animationCount; animation++) {
			animations.add(animationHandler.read(inputStream));
		}
		
		return new Sprite(name, width, height, type, distance, loadScript, scriptFile, animations);
	}
	
}
