package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectDataHandler implements DataHandler<Project> {
	
	private final Format format;
	private int version;

	public ProjectDataHandler(Format format) {
		this.format = format;
	}
	
	public void setVersion(int version) {
		this.version = version;
	}

	@Override
	public void write(Project t, OutputStream outputStream) throws IOException {
		// Palettes
		final List<Palette> palettes = t.getPalettes();
		
		final DataHandler<Palette> paletteDataHandler = format.getHandler(Palette.class);
		Streams.write(palettes.size(), outputStream);
		for(final Palette palette : palettes)
			paletteDataHandler.write(palette, outputStream);
		
		// Cartes
		final List<TileMap> maps = t.getMaps();
		
		final DataHandler<TileMap> tileMapHandler = format.getHandler(TileMap.class);
		Streams.write(maps.size(), outputStream);
		for(final TileMap map : maps) {
			tileMapHandler.write(map, outputStream);
		}
		
		if(version == 3) {
			// Sprites
			final List<Sprite> sprites = t.getSprites();
			sprites.clear();
			
			final DataHandler<Sprite> spriteHandler = format.getHandler(Sprite.class);
			Streams.write(sprites.size(), outputStream);
			for(final Sprite sprite : sprites) {
				spriteHandler.write(sprite, outputStream);
			}
		}
	}

	@Override
	public Project read(InputStream inputStream) throws IOException {
		final Project project = new Project();
		
		// Palettes
		final DataHandler<Palette> paletteDataHandler = format.getHandler(Palette.class);
		
		final int paletteCount = Streams.readInt(inputStream);
		for(int index = 0; index < paletteCount; index++) {
			project.addPalette(paletteDataHandler.read(inputStream));
		}
		
		// Cartes
		final DataHandler<TileMap> tileMapHandler = format.getHandler(TileMap.class);
		
		final int mapCount = Streams.readInt(inputStream);
		for(int index = 0; index < mapCount; index++) {
			project.addMap(tileMapHandler.read(inputStream));
		}
		
		if(version == 3) {
			// Sprites
			final DataHandler<Sprite> spriteHandler = format.getHandler(Sprite.class);
			final List<Sprite> sprites = project.getSprites();
			
			final int spriteCount = Streams.readInt(inputStream);
			for(int index = 0; index < spriteCount; index++) {
				sprites.add(spriteHandler.read(inputStream));
			}
		}
		
		return project;
	}
	
}
