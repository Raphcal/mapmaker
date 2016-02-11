package fr.rca.mapmaker.io.autodeploy;

import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.project.Project;
import java.io.File;
import java.io.IOException;

/**
 * Déploiement automatique pour PuzzleSuit.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PuzzleSuitAutoDeployer extends AutoDeployer {
	
	@Override
	public boolean accept(File file) {
		return file.isDirectory() && new File(file, "puzzlesuit.xcodeproj").isDirectory();
	}

	@Override
	public String getName() {
		return "PuzzleSuit";
	}

	@Override
	public void deployProjectInFolder(Project project, File root) throws IOException {
		setVersion(InternalFormat.LAST_VERSION);
		
		final File folder = new File(root, "puzzlesuit");
		
		deploySprites(project.getSprites(), folder);
		deployPalettes(project.getPalettes(), folder);
		deployMaps(project.getMaps(), project, folder);
	}

	@Override
	public String getDescription() {
		return "Dossier de PuzzleSuit";
	}
	
}
