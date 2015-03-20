package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.internal.ColorDataHandler;
import fr.rca.mapmaker.io.internal.ColorPaletteDataHandler;
import fr.rca.mapmaker.io.internal.EditableColorPaletteDataHandler;
import fr.rca.mapmaker.io.internal.EditableImagePaletteDataHandler;
import fr.rca.mapmaker.io.internal.LayerDataHandler;
import fr.rca.mapmaker.io.internal.PaletteReferenceDataHandler;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.EnumSet;
import java.util.zip.ZipOutputStream;

/**
 * Format destiné à l'export vers d'autres projets.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class MKZFormat extends AbstractFormat {

	private static final String EXTENSION = ".zip";
	
	public MKZFormat() {
		super(EXTENSION, "format.mkz.description", EnumSet.of(SupportedOperation.SAVE));
		
		addHandler(Project.class, new ProjectDataHandler(this));
		addHandler(Color.class, new ColorDataHandler());
		addHandler(Palette.class, new ImagePaletteDataHandler(this));
		addHandler(ColorPalette.class, new ColorPaletteDataHandler(this));
		addHandler(EditableColorPalette.class, new EditableColorPaletteDataHandler(this));
//		addHandler(ImagePalette.class, new ImagePaletteDataHandler(this));
		addHandler(EditableImagePalette.class, new EditableImagePaletteDataHandler(this));
		addHandler(PaletteReference.class, new PaletteReferenceDataHandler());
		addHandler(BufferedImage.class, new BufferedImageDataHandler());
		addHandler(TileLayer.class, new LayerDataHandler());
		addHandler(TileMap.class, new TileMapDataHandler(this));
		addHandler(Sprite.class, new SpriteDataHandler(this));
		addHandler(Instance.class, new InstanceDataHandler());
	}
	
	@Override
	public void saveProject(Project project, File file) {
		final DataHandler<Project> handler = getHandler(project.getClass());
		
		ZipOutputStream outputStream = null;
		try {
			outputStream = new ZipOutputStream(new FileOutputStream(file));
			
			handler.write(project, outputStream);
			outputStream.closeEntry();

		} catch (IOException ex) {
			Exceptions.showStackTrace(ex, null);

		} finally {
			if(outputStream != null) {
				try {
					outputStream.finish();
					outputStream.close();
				} catch (IOException ex) {
					Exceptions.showStackTrace(ex, null);
				}
			}
		}
	}

	@Override
	public Project openProject(File file) {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
