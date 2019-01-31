package fr.rca.mapmaker.io.pixellogic;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.awt.Color;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;

/**
 * Format dédié à l'export vers PixelLogic.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PixelLogicFormat extends AbstractFormat {

	private static final String EXTENSION = ".pxl";
	
	public PixelLogicFormat() {
		super(EXTENSION, SupportedOperation.SAVE);
	}
	
	@Override
	public void saveProject(Project project, File file) {
		
		try {
			final FileOutputStream outputStream = new FileOutputStream(file);
			
			final TileMap tileMap = project.getMaps().get(0);
			final Layer layer = tileMap.getLayers().get(0);

			// Ecriture de la grille
			Streams.write(layer.getWidth() * layer.getHeight(), outputStream);
			
			for(int y = 0; y < layer.getHeight(); y++) {
				for (int x = 0; x < layer.getWidth(); x++) {
					Streams.write(layer.getTile(x, y), outputStream);
				}
			}
			
			// Ecriture de la palette
			Palette palette = tileMap.getPalette();
			
			if(palette instanceof PaletteReference) {
				final int index = ((PaletteReference)palette).getPaletteIndex();
				palette = project.getPalette(index);
			}
			
			if(palette instanceof ColorPalette) {
				final ColorPalette colorPalette = (ColorPalette)palette;
				
				Streams.write(colorPalette.size(), outputStream);
				
				for(int index = 0; index < colorPalette.size(); index++) {
					Color color = colorPalette.getColor(index);
					
					if(color == null) {
						color = Color.BLACK;
					}
					
					Streams.write(color.getRed(), outputStream);
					Streams.write(color.getGreen(), outputStream);
					Streams.write(color.getBlue(), outputStream);
				}
			}
			
		} catch(IOException e) {
			Exceptions.showStackTrace(e, null);
		}
	}

	@Override
	public Project openProject(File file) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
