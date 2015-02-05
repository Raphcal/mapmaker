package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Color;
import java.awt.Graphics;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileLayerList extends AbstractOrientableList<TileLayer> implements Orientable {
	private final int tileSize = 1;
	private Palette palette;

	public void setPalette(Palette palette) {
		this.palette = palette;
	}
	
	@Override
	protected void elementAdded(int index, TileLayer element) {
		element.addLayerChangeListener(listener);
	}

	@Override
	protected void paintElement(int index, Graphics g) {
		final TileLayer layer = elements.get(index);
		
		final int originX = orientation.getX(this, index);
		final int originY = orientation.getY(this, index);
		
		final int gridSize = getGridSize();
		
		g.setColor(Color.BLACK);
		g.drawRect(originX - 1, originY - 1, gridSize + 1, gridSize + 1);
		
		final int maxX = Math.min(gridSize / tileSize, layer.getWidth());
		final int maxY = Math.min(gridSize / tileSize, layer.getHeight());

		for(int y = 0; y < maxY; y++) {
			for(int x = 0; x < maxX; x++) {
				palette.paintTile(g, layer.getTile(x, y), originX + x * tileSize, originY + y * tileSize, tileSize);
			}
		}
	}
	
}
