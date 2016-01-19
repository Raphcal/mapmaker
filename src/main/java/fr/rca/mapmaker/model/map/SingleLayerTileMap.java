package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Graphics;
import java.awt.Point;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class SingleLayerTileMap implements Comparable<SingleLayerTileMap> {
	/**
	 * Palette utilisée pour afficher la grille.
	 */
	private final Palette palette;
	/**
	 * Données de la grille.
	 */
	private final Layer layer;

	public SingleLayerTileMap(Layer layer, Palette palette) {
		this.layer = layer;
		this.palette = palette;
	}
	
	public int getWidth() {
		return layer.getWidth();
	}
	
	public int getHeight() {
		return layer.getHeight();
	}
	
	public void paintAtLocation(Point location, Graphics graphics) {
		for(int y = 0; y < layer.getHeight(); y++) {
			for(int x = 0; x < layer.getWidth(); x++) {
				palette.paintTile(graphics, layer.getTile(x, y), x + location.x, y + location.y, 1);
			}
		}
	}

	@Override
	public int hashCode() {
		int hash = 3;
		if (layer != null) {
			hash = 79 * hash + layer.getWidth();
			hash = 79 * hash + layer.getHeight();
			hash = 79 * hash + layerHashCode();
		}
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final SingleLayerTileMap other = (SingleLayerTileMap) obj;
		return layerEquals(layer, other.layer);
	}
	
	@Override
	public int compareTo(SingleLayerTileMap other) {
		final int compareHeight = Integer.valueOf(other.getHeight()).compareTo(getHeight());
				
		if(compareHeight == 0) {
			final int compareWidth = Integer.valueOf(other.getWidth()).compareTo(getWidth());

			if(compareWidth == 0) {
				return Integer.valueOf(layerHashCode()).compareTo(other.layerHashCode());
			} else {
				return compareWidth;
			}
		} else {
			return compareHeight;
		}
	}
	
	private int layerHashCode() {
		if (layer == null) {
            return 0;
		}
		
		final int[] primes = {3, 5, 7, 11, 13, 17, 31};

        int result = 1;
        for (int y = 0; y < layer.getHeight(); y++) {
			for (int x = 0; x < layer.getWidth(); x++) {
				result = primes[(y * layer.getWidth() + x) % primes.length] * result + layer.getTile(x, y);
			}
		}

        return result;
	}
	
	private boolean layerEquals(Layer layer1, Layer layer2) {
		if (layer1 == layer2) {
			return true;
		}
		if (layer1 == null || layer2 == null) {
			return false;
		}
		if (layer1.getWidth() != layer2.getWidth()) {
			return false;
		}
		if (layer1.getHeight() != layer2.getHeight()) {
			return false;
		}
		for (int y = 0; y < layer1.getHeight(); y++) {
			for (int x = 0; x < layer1.getWidth(); x++) {
				if (layer1.getTile(x, y) != layer2.getTile(x, y)) {
					return false;
				}
			}
		}
		return true;
	}
	
}
