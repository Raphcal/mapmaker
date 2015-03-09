package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GridList extends AbstractOrientableList<TileMap> implements Orientable {

	private final int tileSize = 1;
	
	@Override
	protected void elementAdded(int index, TileMap element) {
		for(final Layer layer : element.getLayers()) {
			if(layer instanceof TileLayer) {
				final TileLayer tileLayer = (TileLayer) layer;
				tileLayer.addLayerChangeListener(listener);
				indexes.put(tileLayer, index);
			}
		}
	}

	@Override
	protected void paintElement(int index, Graphics g) {
		final TileMap map = elements.get(index);
		final Palette palette = map.getPalette();
		
		final int originX = orientation.getX(this, index);
		final int originY = orientation.getY(this, index);
		
		final int width = getElementWidth();
		final int height = getElementHeight();
		
		if(map.getBackgroundColor() != null) {
			g.setColor(map.getBackgroundColor());
			g.fillRect(originX, originY, width, height);
		}
		
		g.setColor(Color.BLACK);
		g.drawRect(originX - 1, originY - 1, width + 1, height + 1);
		
		for(final Layer layer : map.getLayers()) {
			if(layer.isVisible()) {
				final int maxX = Math.min(width / tileSize, layer.getWidth());
				final int maxY = Math.min(height / tileSize, layer.getHeight());
				
				for(int y = 0; y < maxY; y++) {
					for(int x = 0; x < maxX; x++) {
						palette.paintTile(g, layer.getTile(x, y), originX + x * tileSize, originY + y * tileSize, tileSize);
					}
				}
			}
		}
	}
}
