package fr.rca.mapmaker.model.map;

import java.awt.Point;

/**
 * Couche d'une carte.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface Layer {
	
	/**
	 * Récupère la largeur de la couche.
	 *
	 * @return La largeur.
	 */
	int getWidth();
	
	/**
	 * Récupère la hauteur de la couche.
	 *
	 * @return La hauteur.
	 */
	int getHeight();

	/**
	 * Récupère la vitesse de défilement associée à la couche.
	 *
	 * @return La vitesse de défilement.
	 */
	float getScrollRate();
	
	/**
	 * Récupère la visibilité de la couche.
	 *
	 * @return <code>true</code> si la couche est visible,
	 * <code>false</code> sinon.
	 */
	boolean isVisible();

	/**
	 * Récupère la tuile à l'emplacement donné.
	 *
	 * @param x Abscisse de la tuile.
	 * @param y Ordonnée de la tuile.
	 * @return Le numéro de la tuile à l'emplacement donné.
	 */
	int getTile(int x, int y);

	/**
	 * Récupère la tuile à l'emplacement donné.
	 *
	 * @param p Coordonnées de la tuile.
	 * @return Le numéro de la tuile à l'emplacement donné.
	 */
	int getTile(Point p);
}
