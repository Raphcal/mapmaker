package fr.rca.mapmaker.io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FormatFileFilter extends FileFilter {
	
	private final String description;
	private final String extension;
	private final Format format;

	public FormatFileFilter(String description, String extension, Format format) {
		this.description = description;
		this.extension = extension;
		this.format = format;
	}

	@Override
	public boolean accept(File f) {
		return f.getName().endsWith(extension) || (!OperatingSystem.IS_MAC_OS && f.isDirectory());
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public Format getFormat() {
		return format;
	}
}
