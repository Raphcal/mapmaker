package fr.rca.mapmaker.editor.brush;

import fr.rca.mapmaker.model.map.DataLayer;
import java.awt.Point;

/**
 * Brosse composé d'une couche.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class LayerBrush implements Brush {
	
	private DataLayer layer;

	/**
	 * {@inheritDoc}
	 */
	@Override
	public DataLayer get() {
		return layer;
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public Point translate(Point point) {
		return point;
	}
	
}
