package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;

/**
 * Écrit une carte dans un fichier binaire avec toutes ses couches de tuiles.
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapHandler implements DataHandler<TileMap> {

	@Override
	public void write(TileMap t, OutputStream outputStream) throws IOException {
		Palette palette = t.getPalette();
		if (!(palette instanceof PaletteReference)) {
			throw new UnsupportedOperationException("TileMap palette should be a reference to allow palette index export");
		}
		Project project = ((PaletteReference) palette).getProject();
		final List<Palette> palettes = PlaydateFormat.palettesForProject(project);
		Streams.write((short) palettes.indexOf(palette), outputStream);

		final List<Layer> layers = t.getLayers();
		Streams.write(layers.size(), outputStream);

		for(final Layer layer : layers) {
			final int width = layer.getWidth();
			final int height = layer.getHeight();
			Streams.write(width, outputStream);
			Streams.write(height, outputStream);
			Streams.write((float)layer.getScrollRate().getX(), outputStream);
			Streams.write((float)layer.getScrollRate().getY(), outputStream);
			Streams.write(layer.isSolid(), outputStream);
			int max = width * height;
			for (int index = 0; index < max; index++) {
				Streams.writeUnsignedShort(layer.getTile(index % width, index / width), outputStream);
			}
		}
	}

	@Override
	public TileMap read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}
	
}
