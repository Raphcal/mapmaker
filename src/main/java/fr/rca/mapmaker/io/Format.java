package fr.rca.mapmaker.io;

import fr.rca.mapmaker.model.project.Project;
import java.io.File;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface Format {
	
	void saveProject(Project project, File file);
	@NotNull
	Project openProject(File file);
	void importFiles(File[] files, Project project);
	
	@NotNull
	FormatFileFilter getFileFilter();
	@NotNull
	File normalizeFile(File file);
	
	@Nullable
	<T> DataHandler<T> getHandler(Class<? extends T> t);
	@Nullable
	<T> DataHandler<T> getHandler(String name);
	
	boolean hasSupportFor(SupportedOperation operation);
	boolean canSaveFiles();
	boolean canLoadFiles();
	boolean canImportFiles();
	
	@NotNull
	String getDefaultExtension();
}
