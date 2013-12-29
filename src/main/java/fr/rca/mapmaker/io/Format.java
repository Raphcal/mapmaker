package fr.rca.mapmaker.io;

import fr.rca.mapmaker.model.project.Project;
import java.io.File;

public interface Format {
	
	void saveProject(Project project, File file);
	Project openProject(File file);
	void importFiles(File[] files, Project project);
	
	FormatFileFilter getFileFilter();
	File normalizeFile(File file);
	
	<T> DataHandler<T> getHandler(Class<? extends T> t);
	<T> DataHandler<T> getHandler(String name);
	
	boolean hasSupportFor(SupportedOperation operation);
	boolean canSaveFiles();
	boolean canLoadFiles();
	boolean canImportFiles();
	
	String getDefaultExtension();
}
