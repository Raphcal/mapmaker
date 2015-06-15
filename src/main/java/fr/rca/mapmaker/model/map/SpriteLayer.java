
package fr.rca.mapmaker.model.map;

import java.awt.Point;

/**
 * Couche affichant des sprites et des événements.
 * 
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class SpriteLayer implements Layer {
	
	/**
	 * Nom de la couche.
	 */
	private String name;

	/**
	 * Largeur de la couche.
	 */
	private int width;
	
	/**
	 * Hauteur de la couche.
	 */
	private int height;
	
	/**
	 * Visibilité de la couche.
	 */
	private boolean visible = true;
	
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
		return ScrollRate.IDENTITY;
	}

	@Override
	public boolean isVisible() {
		return visible;
	}

	/**
	 * Modifie le nom de ce calque.
	 * 
	 * @param name Nom du calque.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public int getTile(int x, int y) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getTile(Point p) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
}
