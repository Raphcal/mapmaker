package fr.rca.mapmaker.io;

import fr.rca.mapmaker.model.project.Project;
import java.io.File;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class AbstractFormat implements Format {
	protected static final ResourceBundle language = ResourceBundle.getBundle("resources/language");
	
	private final HashMap<String, DataHandler<?>> handlers = new HashMap<String, DataHandler<?>>();
	private final String defaultExtension;
	private final FormatFileFilter fileFilter;
	private final EnumSet<SupportedOperation> supportedOperations;
	

	public AbstractFormat(String defaultExtension, String descriptionRef, EnumSet<SupportedOperation> supportedOperations) {
		this.defaultExtension = defaultExtension;
		this.supportedOperations = supportedOperations;
		
		final StringBuilder descriptionBuilder = new StringBuilder()
				.append(language.getString(descriptionRef))
				.append(" (*")
				.append(defaultExtension)
				.append(')');
				
		this.fileFilter = new FormatFileFilter(descriptionBuilder.toString(), defaultExtension, this);
	}
	
	protected <T> void addHandler(Class<T> clazz, DataHandler<T> handler) {
		handlers.put(clazz.getName(), handler);
	}
	
	@Override
	public <T> DataHandler<T> getHandler(Class<? extends T> t) {
		return (DataHandler<T>) handlers.get(t.getName());
	}
	
	@Override
	public <T> DataHandler<T> getHandler(String name) {
		return (DataHandler<T>) handlers.get(name);
	}
	
	public void setVersion(int version) {
		for(final DataHandler<?> handler : handlers.values()) {
			if(handler instanceof HasVersion) {
				((HasVersion)handler).setVersion(version);
			}
		}
	}

	@Override
	public String getDefaultExtension() {
		return defaultExtension;
	}

	@Override
	public File normalizeFile(File file) {
		
		if(!file.getName().endsWith(defaultExtension))
			return new File(file.getParent(), file.getName() + defaultExtension);
		else
			return file;
	}

	@Override
	public FormatFileFilter getFileFilter() {
		return fileFilter;
	}

	@Override
	public boolean hasSupportFor(SupportedOperation operation) {
		return supportedOperations.contains(operation);
	}
	
	@Override
	public boolean canSaveFiles() {
		return supportedOperations.contains(SupportedOperation.SAVE);
	}

	@Override
	public void saveProject(Project project, File file) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean canLoadFiles() {
		return supportedOperations.contains(SupportedOperation.LOAD);
	}

	@Override
	public Project openProject(File file) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public boolean canImportFiles() {
		return supportedOperations.contains(SupportedOperation.IMPORT);
	}

	@Override
	public void importFiles(File[] files, Project project) {
		throw new UnsupportedOperationException("Not supported yet."); 
	}
}
