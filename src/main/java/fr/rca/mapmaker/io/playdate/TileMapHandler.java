package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.awt.Point;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * Écrit une carte dans un fichier binaire avec toutes ses couches de tuiles.
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapHandler implements DataHandler<TileMap> {
	/**
	 * Nom de la couche contenant le rectangle d'eau pour cette carte.
	 */
	public static final String WATER_LAYER_NAME = "water";

	@Override
	public void write(TileMap t, OutputStream outputStream) throws IOException {
		Palette palette = t.getPalette();
		if (!(palette instanceof PaletteReference)) {
			throw new UnsupportedOperationException("TileMap palette should be a reference to allow palette index export");
		}
		Project project = ((PaletteReference) palette).getProject();
		final List<Palette> palettes = PlaydateFormat.palettesForProject(project);
		Streams.write((short) palettes.indexOf(palette), outputStream);

		final List<Layer> layers = new ArrayList<>(t.getLayers());
		final Rectangle waterArea = getWaterArea(layers);
		// Origine en haut à gauche pour simplifier le code.
		Streams.write(waterArea.x, outputStream);
		Streams.write(waterArea.y, outputStream);
		Streams.write(waterArea.width, outputStream);
		Streams.write(waterArea.height, outputStream);

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

	private Rectangle getWaterArea(final List<Layer> layers) {
		final Iterator<Layer> layerIterator = layers.iterator();
		while (layerIterator.hasNext()) {
			final Layer layer = layerIterator.next();
			if (WATER_LAYER_NAME.equalsIgnoreCase(layer.getName())) {
				layerIterator.remove();
				Point topLeft = new Point(layer.getWidth(), layer.getHeight());
				Point bottomRight = new Point();
				final int count = layer.getWidth() * layer.getHeight();
				for (int index = 0; index < count; index++) {
					final int x = index % layer.getWidth();
					final int y = index / layer.getWidth();
					if (layer.getTile(x, y) != TileLayer.EMPTY_TILE) {
						topLeft.x = Math.min(topLeft.x, x);
						topLeft.y = Math.min(topLeft.y, y);

						bottomRight.x = Math.max(bottomRight.x, x);
						bottomRight.y = Math.max(bottomRight.y, y);
					}
				}
				// TODO: Récupérer la taille des tuiles ?
				return new Rectangle(topLeft.x * 32, topLeft.y * 32, Math.max(bottomRight.x - topLeft.x, 0) * 32, Math.max(bottomRight.y - topLeft.y, 0) * 32);
			}
		}
		return new Rectangle();
	}
}
