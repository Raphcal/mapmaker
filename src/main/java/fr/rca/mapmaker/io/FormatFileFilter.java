package fr.rca.mapmaker.io;

import java.io.File;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FormatFileFilter extends FileFilter {
	
	private String description;
	private String extension;
	private Format format;

	public FormatFileFilter(String description, String extension, Format format) {
		this.description = description;
		this.extension = extension;
		this.format = format;
	}

	@Override
	public boolean accept(File f) {
		return f.isDirectory() || f.getName().endsWith(extension);
	}

	@Override
	public String getDescription() {
		return description;
	}
	
	public Format getFormat() {
		return format;
	}
}
