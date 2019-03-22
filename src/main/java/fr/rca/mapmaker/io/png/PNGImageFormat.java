package fr.rca.mapmaker.io.png;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.model.map.MapAndInstances;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.ImagePalette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JOptionPane;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PNGImageFormat extends AbstractFormat {

	private static final String EXTENSION = ".png";

	public PNGImageFormat() {
		super(EXTENSION, SupportedOperation.SAVE, SupportedOperation.IMPORT);
	}
	
	@Override
	public void saveProject(Project project, File file) {
		final ImageRenderer renderer = new ImageRenderer();
		
		final String fileName = file.getName();
		final String baseName = fileName.substring(0, fileName.lastIndexOf('.'));
		final File folder = file.getParentFile();
		
		final List<MapAndInstances> tileMaps = project.getMaps();
		
		for(int index = 0; index < tileMaps.size(); index++) {
			final TileMap tileMap = tileMaps.get(index).getTileMap();
			
			final Color backgroundColor = tileMap.getBackgroundColor();
			
			if(backgroundColor != null) {
				renderer.setBackground(backgroundColor);
			}
			
			final int tileSize = tileMap.getPalette().getTileSize();
			final BufferedImage image = renderer.renderImage(tileMap.getLayers(), tileMap.getPalette(), 
					new Dimension(tileMap.getWidth() * tileSize, tileMap.getHeight() * tileSize), tileSize,
					backgroundColor != null);
			
			try {
				ImageIO.write(image, "PNG", new File(folder, baseName + (index + 1) + EXTENSION));
				
			} catch(IOException e) {
				Exceptions.showStackTrace(e, null);
			}
		}
	}

	@Override
	public void importFiles(File[] files, Project project) {
		for(File file : files) {
			final String size = JOptionPane.showInputDialog(file.getName() + "- tile size ?");
			try {
				final int tileSize = Integer.parseInt(size);
				final ImagePalette palette = new ImagePalette(file, tileSize);
				project.addPalette(palette);
				
			} catch(NumberFormatException e) {
				Exceptions.showStackTrace(e, null);
			} catch(IOException e) {
				Exceptions.showStackTrace(e, null);
			}
		}
	}
}
