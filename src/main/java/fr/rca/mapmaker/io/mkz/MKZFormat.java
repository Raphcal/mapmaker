package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.zip.ZipOutputStream;

/**
 * Format destiné à l'export vers d'autres projets.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class MKZFormat extends AbstractFormat {

	private static final String EXTENSION = ".zip";
	
	public MKZFormat() {
		super(EXTENSION, SupportedOperation.SAVE);
		
		addHandler(Project.class, new ProjectDataHandler(this));
		addHandler(Palette.class, new ImagePaletteDataHandler());
		addHandler(BufferedImage.class, new BufferedImageDataHandler());
		addHandler(TileMap.class, new TileMapDataHandler(this));
		addHandler(Sprite.class, new SpriteDataHandler(this));
		addHandler(Instance.class, new InstanceDataHandler());
		addHandler(PackMap.class, new PackMapDataHandler(this));
		
		// Handlers du format interne.
		addHandler(Color.class, new fr.rca.mapmaker.io.internal.ColorDataHandler());
		addHandler(TileLayer.class, new fr.rca.mapmaker.io.internal.LayerDataHandler(this));
		addHandler(ScrollRate.class, new fr.rca.mapmaker.io.internal.ScrollRateDataHandler());
		addHandler(Rectangle.class, new fr.rca.mapmaker.io.internal.RectangleDataHandler());
	}
	
	@Override
	public void saveProject(Project project, File file) {
		setVersion(InternalFormat.LAST_VERSION);
		
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
