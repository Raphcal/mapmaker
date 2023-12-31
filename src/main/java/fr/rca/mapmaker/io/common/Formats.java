package fr.rca.mapmaker.io.common;

import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.avm.AVMFormat;
import fr.rca.mapmaker.io.bundle.BundleFormat;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.io.lvl.PuzzleLevelFormat;
import fr.rca.mapmaker.io.mkz.MKZFormat;
import fr.rca.mapmaker.io.mml.MMLFormat;
import fr.rca.mapmaker.io.pixellogic.PixelLogicFormat;
import fr.rca.mapmaker.io.playdate.PlaydateFormat;
import fr.rca.mapmaker.io.png.PNGImageFormat;
import fr.rca.mapmaker.io.shmup.ShmupFormat;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Formats {
	
	private static final Map<String, Format> FORMATS;
	private static final Format INTERNAL_FORMAT;
	
	static {
		INTERNAL_FORMAT = new InternalFormat();
		
		final LinkedHashMap<String, Format> formats = new LinkedHashMap<String, Format>();
		addFormat(formats, INTERNAL_FORMAT);
		addFormat(formats, new BundleFormat());
		addFormat(formats, new MKZFormat());
		addFormat(formats, new MMLFormat());
		addFormat(formats, new PNGImageFormat());
		addFormat(formats, new AVMFormat());
		addFormat(formats, new PixelLogicFormat());
		addFormat(formats, new PuzzleLevelFormat());
		addFormat(formats, new ShmupFormat());
		addFormat(formats, new PlaydateFormat());

		FORMATS = formats;
	}
	private static void addFormat(Map<String, Format> map, Format format) {
		map.put(format.getDefaultExtension(), format);
	}
	
	private Formats() {}

	public static Collection<Format> getFormats() {
		return FORMATS.values();
	}
	
	public static @Nullable Format getFormat(String name) {
		final int extensionStart = name.lastIndexOf('.');
		if(extensionStart > -1) {
			final String extension = name.substring(name.lastIndexOf('.'));
			return FORMATS.get(extension);
		}
		return null;
	}
	
	public static Format getInternalFormat() {
		return INTERNAL_FORMAT;
	}
}
