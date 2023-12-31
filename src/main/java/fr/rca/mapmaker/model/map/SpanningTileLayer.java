package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.operation.OperationParser;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.Arrays;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.LoggerFactory;

/**
 * Couche regroupant les données de plusieurs autres couches.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpanningTileLayer implements DataLayer, HasLayerPlugin {

	private DataLayer[] layers;
	private int columns;
	private int rows;

	private int width;
	private int height;

	private final Map<String, LayerPlugin> plugins = new HashMap<String, LayerPlugin>();

	private int[] tiles;

	@Override
	public int[] copyData() {
		if (tiles == null) {
			final int[] data = new int[width * height];

			int index = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					data[index++] = getTile(x, y);
				}
			}
			tiles = data;
		}
		final int[] copy = new int[tiles.length];
		System.arraycopy(tiles, 0, copy, 0, tiles.length);
		return copy;
	}

	@Override
	public void restoreData(int[] tiles, Rectangle source) {
		final int layerWidth = getLayerWidth();
		final int layerHeight = getLayerHeight();

		for (int row = 0; row < rows; row++) {
			for (int column = 0; column < columns; column++) {
				final int[] data = new int[layerWidth * layerHeight];

				for (int y = 0; y < layerHeight; y++) {
					System.arraycopy(tiles, (row * layerHeight + y) * width + column * layerWidth, data, y * layerWidth, layerWidth);
				}

				getLayer(column, row).restoreData(data, null);
			}
		}
	}

	@Override
	public void restoreData(int[] tiles, int width, int height) {
		if (width != this.width || height != this.height) {
			LoggerFactory.getLogger(SpanningTileLayer.class).warn("restoreData(int[], int, int) n'est pas supporté par SpanningTileLayer.");
		}
		restoreData(tiles, null);
	}

	@Override
	public void restoreData(DataLayer source) {
		restoreData(source.copyData(), source.getWidth(), source.getHeight());
		if (source instanceof HasLayerPlugin) {
			for (final LayerPlugin plugin : ((HasLayerPlugin) source).getPlugins()) {
				setPlugin(plugin);
			}
		}
	}

	@Override
	public int getWidth() {
		return width;
	}

	@Override
	public int getHeight() {
		return height;
	}

	@Override
	public ScrollRate getScrollRate() {
		return new ScrollRate();
	}

	@Override
	public boolean isSolid() {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public int getTile(int x, int y) {
		final DataLayer layer = getLayer(x / getLayerWidth(), y / getLayerHeight());
		return layer != null
				? layer.getTile(x % getLayerWidth(), y % getLayerHeight())
				: -1;
	}

	@Override
	public int getTile(Point p) {
		return getTile(p.x, p.y);
	}

	@Override
	public <L extends LayerPlugin> L getPlugin(Class<L> clazz) {
		return getPlugin(clazz, LayerPlugins.nameOf(clazz));
	}

	@Override
	public <L extends LayerPlugin> L getPlugin(Class<L> clazz, String name) {
		return (L) plugins.get(name);
	}

	@Override
	public void setPlugin(LayerPlugin plugin) {
		plugins.put(plugin.name(), plugin);

		if (plugin instanceof FunctionLayerPlugin) {
			final String function = ((FunctionLayerPlugin) plugin).getFunction();

			if (function == null || function.trim().isEmpty()) {
				return;
			}

			int y = 0;
			for (int row = 0; row < rows; row++) {
				int x = 0;
				for (int column = 0; column < columns; column++) {
					final DataLayer layer = getLayer(column, row);
					if (layer instanceof HasLayerPlugin) {
						((HasLayerPlugin) layer).setPlugin(new FunctionLayerPlugin(OperationParser.shift(function, x, y)));
					}
					x += getLayerWidth();
				}
				y += getLayerHeight();
			}
		}
	}

	@Override
	public <L extends LayerPlugin> void removePlugin(Class<L> clazz) {
		plugins.remove(LayerPlugins.nameOf(clazz));
	}

	@Override
	public Collection<LayerPlugin> getPlugins() {
		return plugins.values();
	}

	public void setSize(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		this.layers = new DataLayer[columns * rows];
	}

	public void setLayer(DataLayer layer, int column, int row) {
		layers[row * columns + column] = layer;
	}

	public List<DataLayer> getLayers() {
		return Arrays.asList(layers);
	}

	public DataLayer getLayer(int column, int row) {
		if (column >= 0 && column < columns && row >= 0 && row < rows) {
			return layers[row * columns + column];
		} else {
			return null;
		}
	}

	private int getLayerWidth() {
		return layers[0].getWidth();
	}

	private int getLayerHeight() {
		return layers[0].getHeight();
	}

	public void updateSize() {
		if (layers != null && layers.length > 0) {
			this.width = getLayerWidth() * columns;
			this.height = getLayerHeight() * rows;

		} else {
			this.width = 0;
			this.height = 0;
		}
	}

}
