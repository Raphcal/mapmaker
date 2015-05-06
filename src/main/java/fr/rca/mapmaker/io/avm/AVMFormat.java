package fr.rca.mapmaker.io.avm;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.ImagePalette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.awt.Color;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class AVMFormat extends AbstractFormat {
	
	private static final String EXTENSION = ".avm";
	
	public AVMFormat() {
		super(EXTENSION, SupportedOperation.LOAD);
		
		addHandler(TileLayer.class, new TileLayerDataHandler());
	}
	
	@Override
	public void saveProject(Project project, File file) {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public Project openProject(File file) {
		
		final Project project = new Project();
		
		try {
			final ImagePalette palette = new ImagePalette(getPaletteFile(file), Color.MAGENTA, 32);
			project.addPalette(palette);
			
			final TileMap tileMap = new TileMap();
			tileMap.setPalette(new PaletteReference(project, 0));
			
			final DataHandler<TileLayer> tileLayerHandler = getHandler(TileLayer.class);
			
			final List<TileLayer> layers = new ArrayList<TileLayer>();
			
			final AVMFileIterator iterator = new AVMFileIterator(file);
			while(iterator.hasNext()) {
				final File layerFile = iterator.next();
				
				final FileInputStream inputStream = new FileInputStream(layerFile);
				layers.add(tileLayerHandler.read(inputStream));
				inputStream.close();
			}
			
			for(int index = layers.size() - 1; index >= 0; index--) {
				tileMap.add(layers.get(index));
			}
			
			project.addMap(tileMap);
			
		} catch (IOException ex) {
			Exceptions.showStackTrace(ex, null);
		}
		
		
		return project;
	}
	
	private File getPaletteFile(File source) {
		
		final String baseName = source.getName().substring(0, source.getName().lastIndexOf('.'));
		return new File(source.getParentFile(), baseName + ".bmp");
	}
}
