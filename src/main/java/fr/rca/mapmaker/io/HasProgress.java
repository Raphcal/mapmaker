package fr.rca.mapmaker.io;

import fr.rca.mapmaker.model.project.Project;
import java.io.File;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface HasProgress {
	void saveProject(Project project, File file, Listener progressListener);
	@NotNull
	Project openProject(File file, Listener progressListener);
	
	interface Listener {
		void onProgress(int value);
	}
}
