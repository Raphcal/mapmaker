package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.model.map.ScrollRate;
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
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipFile;

/**
 *
 * @author daeke
 */
public class InternalFormat extends AbstractFormat {

	private static final String EXTENSION = ".mmk";
	private static final String DATA_ENTRY = "data";

	private static final int HEADER_LENGTH = 4;

	public static final int VERSION_3 = 3;
	public static final int VERSION_4 = 4;
	public static final int VERSION_5 = 5;
	public static final int VERSION_6 = 6;
	public static final int VERSION_7 = 7;
	public static final int VERSION_8 = 8;
	public static final int VERSION_9 = 9;
	public static final int VERSION_10 = 10;

	/**
	 * Version 11. Depuis le 18/03/2019.
	 * <p/>
	 * Ajoute les fonctionnalités suivantes :<ul>
	 * <li>Sprites globaux</li>
	 * <li>Instances liées à une couche</li>
	 * </ul>
	 */
	public static final int VERSION_11 = 11;

	/**
	 * Version 12. Depuis le 30/03/2022.
	 * <p/>
	 * Ajoute les fonctionnalités suivantes :<ul>
	 * <li>Propriété "solid" des layers</li>
	 * </ul>
	 */
	public static final int VERSION_12 = 12;

	/**
	 * Version 13. Depuis le 25/06/2022.
	 * <p/>
	 * Ajoute les fonctionnalités suivantes :<ul>
	 * <li>Ajout d'une fonction Y pour les tuiles</li>
	 * <li>Ajout d'un type de sprite "police d'écriture"</li>
	 * </ul>
	 */
	public static final int VERSION_13 = 13;

	public static final int LAST_VERSION = VERSION_13;
	public static final String HEADER_LAST_VERSION = "MM" + LAST_VERSION;

	public InternalFormat() {
		super(EXTENSION, SupportedOperation.LOAD, SupportedOperation.SAVE);

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
		addHandler(TileLayer.class, new LayerDataHandler(this));
		addHandler(ScrollRate.class, new ScrollRateDataHandler());
		addHandler(TileMap.class, new TileMapDataHandler(this));
		addHandler(Sprite.class, new SpriteDataHandler(this));
		addHandler(Animation.class, new AnimationDataHandler(this));
		addHandler(Instance.class, new InstanceDataHandler());
		addHandler(Rectangle.class, new RectangleDataHandler());
	}

	@Override
	public void saveProject(Project project, File file) {
		final DataHandler<Project> handler = getHandler(project.getClass());

		setVersion(LAST_VERSION);

		try {
			final FileOutputStream outputStream = new FileOutputStream(file);
			try {
				writeHeader(HEADER_LAST_VERSION, outputStream);
				handler.write(project, outputStream);

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
		setVersion(version);

		try {
			final InputStream inputStream = openInputStream(file);
			try {
				if (version >= VERSION_3) {
					readHeader(inputStream);
				}
				project = handler.read(inputStream);

			} finally {
				inputStream.close();
			}

		} catch (IOException e) {
			Exceptions.showStackTrace(e, null);
		}

		return project;
	}

	private void writeHeader(String header, OutputStream outputStream) throws IOException {
		for (final char c : header.toCharArray()) {
			Streams.write(c, outputStream);
		}
	}

	private String readHeader(InputStream inputStream) throws IOException {
		final char[] header = new char[HEADER_LENGTH];
		for (int index = 0; index < HEADER_LENGTH; index++) {
			header[index] = Streams.readChar(inputStream);
		}
		return new String(header);
	}

	private int getVersion(File file) {
		try {
			final InputStream inputStream = openInputStream(file);
			try {
				final String header = readHeader(inputStream);
				if (header.length() == HEADER_LENGTH && header.startsWith("MMK")) {
					return header.charAt(3) - '0';
				} else if (header.length() == HEADER_LENGTH && header.startsWith("MM")) {
					return (header.charAt(2) - '0') * 10 + header.charAt(3) - '0';
				} else {
					throw new IllegalArgumentException("Bad file header: " + header);
				}
			} finally {
				inputStream.close();
			}

		} catch (IOException e) {
			Exceptions.showStackTrace(e, null);
		}
		return 0;
	}

	private InputStream openInputStream(File file) {
		try {
			final ZipFile zipFile = new ZipFile(file);
			final ZipEntry entry = zipFile.getEntry(DATA_ENTRY);
			final InputStream inputStream = zipFile.getInputStream(entry);

			return new InputStream() {

				@Override
				public int available() throws IOException {
					return inputStream.available();
				}

				@Override
				public int read() throws IOException {
					return inputStream.read();
				}

				@Override
				public int read(byte[] b) throws IOException {
					return inputStream.read(b);
				}

				@Override
				public int read(byte[] b, int off, int len) throws IOException {
					return inputStream.read(b, off, len);
				}

				@Override
				public long skip(long n) throws IOException {
					return inputStream.skip(n); //To change body of generated methods, choose Tools | Templates.
				}

				@Override
				public synchronized void reset() throws IOException {
					inputStream.reset();
				}

				@Override
				public void close() throws IOException {
					inputStream.close();
					zipFile.close();
				}

			};

		} catch (IOException e) {
			// Ignoré.
		}

		try {
			return new FileInputStream(file);

		} catch (IOException e) {
			// Ignoré.
		}

		return null;
	}
}
