package fr.rca.mapmaker.io.autodeploy;

import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.project.Project;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import javax.xml.stream.XMLStreamException;

/**
 * Déploiement automatique pour MeltedIce.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class MeltedIceAutoDeployer extends AutoDeployer {
	
	private static final String MAPS_FOLDER = "maps";
	private static final String SPRITES_FOLDER = "sprites";
	
	private static final String PROJECT_FILE = "MeltedIce.csproj";
	
	@Override
	public String getName() {
		return "MeltedIce";
	}

	@Override
	public void deployProjectInFolder(Project project, File root) throws IOException {
		final File mapsFolder = isCSharp(root) ? new File(root, MAPS_FOLDER) : new File(root, "MeltedIce");
		final File spritesFolder = isCSharp(root) ? new File(root, SPRITES_FOLDER) : new File(root, "MeltedIce");
		
		setVersion(InternalFormat.LAST_VERSION);
		
		final List<String> contents = new ArrayList<String>();
		
		deploySprites(project.getSprites(), spritesFolder, contents);
		deployPalettes(project.getPalettes(), mapsFolder, contents);
		deployMaps(project.getMaps(), project, mapsFolder, contents);
		
		if(isCSharp(root)) {
			try {
				VSProjectWriter.write(project, root, contents, new FileOutputStream(new File(root, PROJECT_FILE)));
			} catch (XMLStreamException ex) {
				throw new IOException("Erreur lors de l'écriture du fichier de projet VisualStudio.", ex);
			}
		}
	}

	@Override
	public String getDescription() {
		return "Dossier de MeltedIce";
	}
	
	@Override
	public boolean accept(File file) {
		return isCSharp(file) || isSwift(file);
	}
	
	private static boolean isCSharp(File file) {
		final File mapsFolder = new File(file, MAPS_FOLDER);
		final File spritesFolder = new File(file, SPRITES_FOLDER);
		
		return file.isDirectory() && mapsFolder.isDirectory() && spritesFolder.isDirectory();
	}
	
	private static boolean isSwift(File file) {
		return file.isDirectory() && new File(file, "MeltedIce.xcodeproj").isDirectory();
	}

}
