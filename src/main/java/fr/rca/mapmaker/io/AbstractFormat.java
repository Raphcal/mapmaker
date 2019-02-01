package fr.rca.mapmaker.io;

import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.project.Project;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashMap;
import java.util.Map;
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
	
	private final Map<String, DataHandler<?>> handlers = new HashMap<String, DataHandler<?>>();
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
	
	protected final <T> void addHandler(Class<T> clazz, DataHandler<T> handler) {
		handlers.put(clazz.getName(), handler);
	}
    
    protected final <T> void addNamedHandler(Class<T> clazz, String name, DataHandler<T> handler) {
		handlers.put(clazz.getName() + '@' + name, handler);
	}
	
	@Override
	public <T> DataHandler<T> getHandler(Class<? extends T> t) {
		final DataHandler<T> handler = (DataHandler<T>) handlers.get(t.getName());
		
		if(handler == null) {
			throw new IllegalStateException("Le handler de la classe '" + t + "' n'existe pas pour le format '" + getDefaultExtension() + "'.");
		}
		
		return handler;
	}
	
	public <T> DataHandler<T> getNamedHandler(Class<? extends T> t, String name) {
		final DataHandler<T> handler = (DataHandler<T>) handlers.get(t.getName() + '@' + name);
		
		if(handler == null) {
			throw new IllegalStateException("Le handler de la classe '" + t + "' avec le nom '" + name + "' n'existe pas pour le format '" + getDefaultExtension() + "'.");
		}
		
		return handler;
	}
    
	@Override
	public <T> DataHandler<T> getHandler(String name) {
		return (DataHandler<T>) handlers.get(name);
	}
	
    public <T> void write(T t, OutputStream outputStream) throws IOException {
        final DataHandler<T> dataHandler = getHandler((Class<T>) t.getClass());
        dataHandler.write(t, outputStream);
    }
	
    public <T> void writeNullable(T t, OutputStream outputStream) throws IOException {
        Streams.write(t != null, outputStream);
        if (t != null) {
            final DataHandler<T> dataHandler = getHandler((Class<T>) t.getClass());
            dataHandler.write(t, outputStream);
        }
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
