package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Streams;
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
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
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
	
	private static final int HEADER_LENGTH = 4;
	private static final int HEADER_LAST_VERSION = 3;
	private static final String HEADER_VERSION_3 = "MMK3";
			

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
		addHandler(Sprite.class, new SpriteDataHandler(this));
		addHandler(Animation.class, new AnimationDataHandler(this));
		addHandler(Instance.class, new InstanceDataHandler());
	}

	@Override
	public void saveProject(Project project, File file) {
		final DataHandler<Project> handler = getHandler(project.getClass());
		
		((ProjectDataHandler)handler).setVersion(HEADER_LAST_VERSION);
		
		try {
			final ZipOutputStream outputStream = new ZipOutputStream(new FileOutputStream(file));
			try {
				outputStream.putNextEntry(new ZipEntry(DATA_ENTRY));
				writeHeader(HEADER_VERSION_3, outputStream);
				handler.write(project, outputStream);
				outputStream.closeEntry();
				
			} finally {
				outputStream.close();
			}

		} catch (IOException ex) {
			Exceptions.showStackTrace(ex, null);
		}
	}

	@Override
	public Project openProject(File file) {
		Project project = null;
		final DataHandler<Project> handler = getHandler(Project.class);
		
		// Définition du numéro de version.
		final int version = getVersion(file);
		((ProjectDataHandler)handler).setVersion(version);
		
		try {
			final ZipFile zipFile = new ZipFile(file);
			try {
				final ZipEntry entry = zipFile.getEntry(DATA_ENTRY);

				final InputStream inputStream = zipFile.getInputStream(entry);
				try {
					if(version == 3) {
						readHeader(inputStream);
					}
					project = handler.read(inputStream);

				} finally {
					inputStream.close();
				}
			} finally {
				zipFile.close();
			}
			
		} catch(IOException e) {
			Exceptions.showStackTrace(e, null);
		}
		
		return project;
	}
	
	private void writeHeader(String header, OutputStream outputStream) throws IOException {
		for(final char c : header.toCharArray()) {
			Streams.write(c, outputStream);
		}
	}
	
	private String readHeader(InputStream inputStream) throws IOException {
		final char[] header = new char[HEADER_LENGTH];
		for(int index = 0; index < HEADER_LENGTH; index++) {
			header[index] = Streams.readChar(inputStream);
		}
		return new String(header);
	}
	
	private int getVersion(File file) {
		try {
			final ZipFile zipFile = new ZipFile(file);
			try {
				final ZipEntry entry = zipFile.getEntry(DATA_ENTRY);
				final InputStream inputStream = zipFile.getInputStream(entry);
				try {
					final String header = readHeader(inputStream);
					if(HEADER_VERSION_3.equals(header)) {
						return 3;
					}
					
				} finally {
					inputStream.close();
				}
				
			} finally {
				zipFile.close();
			}
			
		} catch(IOException e) {
			Exceptions.showStackTrace(e, null);
		}
		return 0;
	}
}
