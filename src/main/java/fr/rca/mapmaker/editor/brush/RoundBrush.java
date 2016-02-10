package fr.rca.mapmaker.editor.brush;

import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.TileLayer;
import java.awt.Point;

/**
 * Brosse ronde d'une seule couleur.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class RoundBrush implements Brush {

	private int radius;
	private int tile;
	
	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataLayer get() {
		final int diameter = radius + radius;
		final int squareDiameter = diameter * diameter;
		final int[] tiles = new int[diameter * diameter];
		
		for (int index = 0; index < tiles.length; index++) {
			final int x = index % diameter;
			final int y = index / diameter;
			
			tiles[index] = (x - radius) * (x - radius) + (y - radius) * (y - radius) <= squareDiameter ? tile : -1;
		}
		
		return new TileLayer(diameter, diameter, tiles);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point translate(Point point) {
		return new Point(point.x - radius, point.y - radius);
	}
	
}
