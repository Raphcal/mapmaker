package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.model.project.Project;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectDataHandler implements DataHandler<Project> {
	
	private Format format;

	public ProjectDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void write(Project t, OutputStream outputStream) throws IOException {
		final ZipOutputStream zipOutputStream = (ZipOutputStream)outputStream;
		
		// Palettes
		final List<Palette> palettes = t.getPalettes();
		
		final DataHandler<Palette> paletteDataHandler = format.getHandler(Palette.class);
		
		for(int index = 0; index < palettes.size(); index++) {
			final Palette palette = palettes.get(index);
			
			final ZipEntry entry = new ZipEntry("palette" + index + ".pal");
			zipOutputStream.putNextEntry(entry);
			paletteDataHandler.write(palette, outputStream);
			zipOutputStream.closeEntry();
		}
		
		// Cartes
		final List<TileMap> maps = t.getMaps();
		
		final DataHandler<TileMap> tileMapHandler = format.getHandler(TileMap.class);
		
		for(int index = 0; index < maps.size(); index++) {
			final TileMap map = maps.get(index);
			
			final ZipEntry entry = new ZipEntry("map" + index + ".map");
			zipOutputStream.putNextEntry(entry);
			tileMapHandler.write(map, outputStream);
			zipOutputStream.closeEntry();
		}
	}

	@Override
	public Project read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("NIY");
	}
	
}
