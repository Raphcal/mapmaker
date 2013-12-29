package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.ImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.EnumSet;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author daeke
 */
public class InternalFormat extends AbstractFormat {
	
	private static final String EXTENSION = ".mmk";
	private static final String DATA_ENTRY = "data";
			

	public InternalFormat() {
		super(EXTENSION, "format.internal.description", EnumSet.of(SupportedOperation.LOAD, SupportedOperation.SAVE));
		
		addHandler(Project.class, new ProjectDataHandler(this));
		addHandler(Color.class, new ColorDataHandler());
		addHandler(Palette.class, new PaletteDataHandler(this));
		addHandler(ColorPalette.class, new ColorPaletteDataHandler(this));
		addHandler(AlphaColorPalette.class, new AlphaColorPaletteDataHandler(this));
		addHandler(EditableColorPalette.class, new EditableColorPaletteDataHandler(this));
		addHandler(ImagePalette.class, new ImagePaletteDataHandler(this));
		addHandler(EditableImagePalette.class, new EditableImagePaletteDataHandler(this));
		addHandler(PaletteReference.class, new PaletteReferenceDataHandler());
		addHandler(BufferedImage.class, new BufferedImageDataHandler());
		addHandler(TileLayer.class, new LayerDataHandler());
		addHandler(TileMap.class, new TileMapDataHandler(this));
	}

	@Override
	public void saveProject(Project project, File file) {
		final DataHandler<Project> handler = getHandler(project.getClass());
		
		ZipOutputStream outputStream = null;
		try {
			outputStream = new ZipOutputStream(new FileOutputStream(file));
			
			outputStream.putNextEntry(new ZipEntry(DATA_ENTRY));
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
		Project project = null;
		final DataHandler<Project> handler = getHandler(Project.class);
		
		ZipFile zipFile = null;
		try {
			zipFile = new ZipFile(file);
			final ZipEntry entry = zipFile.getEntry(DATA_ENTRY);
		
			InputStream inputStream = null;
			try {

				inputStream = zipFile.getInputStream(entry);

				project = handler.read(inputStream);

			} catch (IOException ex) {
				Exceptions.showStackTrace(ex, null);

			} finally {
				if(inputStream != null) {
					try {
						inputStream.close();
					} catch (IOException ex) {
						Exceptions.showStackTrace(ex, null);
					}
				}
			}
			
		} catch(IOException e) {
			Exceptions.showStackTrace(e, null);
			
		} finally {
			if(zipFile != null) {
				try {
					zipFile.close();
				} catch (IOException ex) {
					Exceptions.showStackTrace(ex, null);
				}
			}
		}
		
		return project;
	}
}
