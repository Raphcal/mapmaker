package fr.rca.mapmaker.io;

import fr.rca.mapmaker.model.project.Project;
import java.io.File;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.ResourceBundle;

/**
 * Classe de base pour la création d'un format de fichier.
 * <br/>
 * L'implémentation par défaut des méthodes 
 * {@link #saveProject(fr.rca.mapmaker.model.project.Project, java.io.File)},
 * {@link #openProject(java.io.File)} et 
 * {@link #importFiles(java.io.File[], fr.rca.mapmaker.model.project.Project)}
 * lance une exception {@code UnsupportedOperationException}.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class AbstractFormat implements Format {
	protected static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("resources/language");
	
	private static final String DESCRIPTION_PREFIX = "format";
	private static final String DESCRIPTION_SUFFIX = ".description";
	private static final String EXTENSION_PREFIX = " (*";
	private static final char EXTENSION_SUFFIX = ')';
	
	private static final String UNSUPPORTED_MESSAGE = "Not supported by this format.";
	
	private final HashMap<String, DataHandler<?>> handlers = new HashMap<String, DataHandler<?>>();
	private final String defaultExtension;
	private final FormatFileFilter fileFilter;
	private final EnumSet<SupportedOperation> supportedOperations;
	

	public AbstractFormat(String defaultExtension, SupportedOperation... supportedOperations) {
		this.defaultExtension = defaultExtension;
		this.supportedOperations = EnumSet.copyOf(Arrays.asList(supportedOperations));
		
		final String descriptionRef = DESCRIPTION_PREFIX + defaultExtension + DESCRIPTION_SUFFIX;
		
		final StringBuilder descriptionBuilder = new StringBuilder()
			.append(LANGUAGE.getString(descriptionRef))
			.append(EXTENSION_PREFIX)
			.append(defaultExtension)
			.append(EXTENSION_SUFFIX);
				
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
	
	/**
	 * Défini le numéro de version à utiliser pour les instances de {@link 
	 * DataHandler} qui supportent différentes versions.
	 * 
	 * @param version Numéro de version à définir.
	 */
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
		if(!file.getName().endsWith(defaultExtension)) {
			return new File(file.getParent(), file.getName() + defaultExtension);
		} else {
			return file;
		}
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
		throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
	}

	@Override
	public boolean canLoadFiles() {
		return supportedOperations.contains(SupportedOperation.LOAD);
	}

	@Override
	public Project openProject(File file) {
		throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE);
	}

	@Override
	public boolean canImportFiles() {
		return supportedOperations.contains(SupportedOperation.IMPORT);
	}

	@Override
	public void importFiles(File[] files, Project project) {
		throw new UnsupportedOperationException(UNSUPPORTED_MESSAGE); 
	}
}
