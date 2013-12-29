package fr.rca.mapmaker.io.lvl;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.EnumSet;
import java.util.HashSet;
import java.util.List;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PuzzleLevelFormat extends AbstractFormat {

	private static final String EXTENSION = ".lvl";
	private static final String CHARSET = "UTF-8";
	private static final String LINE_BREAK = "\r\n";
	
	public PuzzleLevelFormat() {
		super(EXTENSION, "format.lvl.description", EnumSet.of(SupportedOperation.SAVE, SupportedOperation.IMPORT));
	}
	
	@Override
	public void saveProject(Project project, File file) {
		final String baseName = getBaseName(file);

		final List<TileMap> maps = project.getMaps();
		for(int i = 0; i < maps.size(); i++) {
			final TileMap map = project.getMaps().get(i);
			final String mapName = baseName + (i + 1);

			final File mapFile = new File(file.getParentFile(), mapName + EXTENSION);
			FileOutputStream outputStream = null;
			try {
				outputStream = new FileOutputStream(mapFile);

				final List<Layer> layers = map.getLayers();
				for(int j = 0; j < layers.size(); j++) {
					final Layer layer = layers.get(j);
					writeLayer(mapName, layer, j, outputStream);
				}

			} catch(IOException e) {
				Exceptions.showStackTrace(e, null);
			} finally {
				if(outputStream != null) {
					try {
						outputStream.close();
					} catch (IOException e) {
						Exceptions.showStackTrace(e, null);
					}
				}
			}
		}
	}
	
	private String getBaseName(File file) {
		final String name = file.getName();
		final int extensionStartIndex = name.lastIndexOf('.');
		
		if(extensionStartIndex > 0) {
			return name.substring(0, extensionStartIndex);
		} else {
			return name;
		}
	}
	
	private void writeLayer(String mapName, Layer layer, int index, FileOutputStream outputStream) throws IOException {
		if(index > 0) {
			outputStream.write(LINE_BREAK.getBytes(CHARSET));
		}
		
		// En-tête
		outputStream.write("const unsigned char ".getBytes(CHARSET));
		outputStream.write(mapName.getBytes(CHARSET));
		outputStream.write("_map".getBytes(CHARSET));
		outputStream.write(Integer.toString(index).getBytes(CHARSET));
		outputStream.write("[".getBytes(CHARSET));
		outputStream.write(Integer.toString(layer.getHeight()).getBytes(CHARSET));
		outputStream.write("]".getBytes(CHARSET));
		outputStream.write("[".getBytes(CHARSET));
		outputStream.write(Integer.toString(layer.getWidth()).getBytes(CHARSET));
		outputStream.write("] = {".getBytes(CHARSET));
		outputStream.write(LINE_BREAK.getBytes(CHARSET));
		
		// Contenu
		for(int y = 0; y < layer.getHeight(); y++) {
			outputStream.write("{ ".getBytes(CHARSET));
			for(int x = 0; x < layer.getWidth(); x++) {
				if(x > 0) {
					outputStream.write(", ".getBytes(CHARSET));
				}
				outputStream.write(Integer.toString(layer.getTile(x, y)).getBytes(CHARSET));
			}
			if(y < layer.getHeight()  - 1) {
				outputStream.write(" },".getBytes(CHARSET));
			} else {
				outputStream.write(" }".getBytes(CHARSET));
			}
			outputStream.write(LINE_BREAK.getBytes(CHARSET));
		}
		
		// Pied de page
		outputStream.write("};".getBytes(CHARSET));
		outputStream.write(LINE_BREAK.getBytes(CHARSET));
	}

	@Override
	public void importFiles(File[] files, Project project) {
		for(final File file : files) {
			final TileMap tileMap = importFile(file);
			tileMap.setPalette(new PaletteReference(project, 0));
			project.addMap(tileMap);
		}
	}
	
	private TileMap importFile(File file) {
		final TileMap tileMap = new TileMap();
		
		FileInputStream inputStream = null;
		try {
			inputStream = new FileInputStream(file);
			
			int b = 0;
			while(b != -1) {
				final TileLayer layer = importLayer(inputStream);
				tileMap.add(layer);
				
				readUntil(inputStream, 'c');
				b = inputStream.read();
			}
			
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
		
		return tileMap;
	}
	
	private TileLayer importLayer(InputStream inputStream) throws IOException {
		// En-tête
		readUntil(inputStream, ' '); // const
		readUntil(inputStream, ' '); // unsigned
		readUntil(inputStream, ' '); // char
		final String name = readUntil(inputStream, '['); // Titre
		final String heightString = readUntil(inputStream, ']'); // _mapX
		readUntil(inputStream, '[');
		final String widthString = readUntil(inputStream, ']'); // _mapX
		readUntil(inputStream, '\n');
		
		try {
			final int height = Integer.parseInt(heightString);
			final int width = Integer.parseInt(widthString);
		
			final TileLayer layer = new TileLayer(width, height);
			layer.setName(name);
			
			for(int y = 0; y < height; y++) {
				readUntil(inputStream, '{');
				for(int x = 0; x < width; x++) {
					final String tileString = readUntil(inputStream, ',', '}');
					layer.setTile(x, y, Integer.parseInt(tileString));
				}
			}

			return layer;
			
		} catch(NumberFormatException e) {
			throw new IOException("Format du fichier invalide.");
		}
	}
	
	private String readUntil(InputStream inputStream, Character... chars) throws IOException {
		final ByteArrayOutputStream buffer = new ByteArrayOutputStream();
		
		final HashSet<Character> set = new HashSet<Character>(Arrays.asList(chars));
				
		int b = inputStream.read();
		while(b != -1 && !set.contains((char)b)) {
			buffer.write(b);
			b = inputStream.read();
		}
		
		return buffer.toString(CHARSET).trim();
	}
}
