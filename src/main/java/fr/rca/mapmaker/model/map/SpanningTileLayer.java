package fr.rca.mapmaker.model.map;

import java.awt.Point;
import java.awt.Rectangle;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpanningTileLayer implements DataLayer {
	
	private DataLayer[] layers;
	private int columns;
	private int rows;
	
	private int width;
	private int height;
	
	@Override
	public int[] copyData() {
		final int[] data = new int[width * height];
		
		int index = 0;
		for(int y = 0; y < height; y++) {
			for(int x = 0; x < width; x++) {
				data[index++] = getTile(x, y);
			}
		}
		
		return data;
	}

	@Override
	public void restoreData(int[] tiles, Rectangle source) {
		final int layerWidth = getLayerWidth();
		final int layerHeight = getLayerHeight();
		
		for(int row = 0; row < rows; row++) {
			for(int column = 0; column < columns; column++) {
				final int[] data = new int[layerWidth * layerHeight];
				
				for(int y = 0; y < layerHeight; y++) {
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
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public boolean isVisible() {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getTile(int x, int y) {
		final DataLayer layer = getLayer(x / getLayerWidth(), y / getLayerHeight());
		return layer.getTile(x % getLayerWidth(), y % getLayerHeight());
	}

	@Override
	public int getTile(Point p) {
		return getTile(p.x, p.y);
	}
	
	public void setSize(int columns, int rows) {
		this.columns = columns;
		this.rows = rows;
		this.layers = new DataLayer[columns * rows];
	}
	
	public void setLayer(DataLayer layer, int column, int row) {
		layers[row * columns + column] = layer;
	}
	
	public DataLayer getLayer(int column, int row) {
		return layers[row * columns + column];
	}
	
	private int getLayerWidth() {
		return layers[0].getWidth();
	}
	
	private int getLayerHeight() {
		return layers[0].getHeight();
	}
	
	public void updateSize() {
		if(layers != null && layers.length > 0) {
			this.width = getLayerWidth() * columns;
			this.height = getLayerHeight() * rows;
			
		} else {
			this.width = 0;
			this.height = 0;
		}
	}
	
}
