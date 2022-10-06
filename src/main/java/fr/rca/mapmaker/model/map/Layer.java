package fr.rca.mapmaker.model.map;

import java.awt.Point;
import org.jetbrains.annotations.Nullable;

/**
 * Couche d'une carte.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface Layer {
	/**
	 * Récupère le nom de la couche ou <code>null</code> si elle n'a pas de
	 * nom.
	 *
	 * @return Le nom de la couche ou <code>null</code>.
	 */
	default @Nullable String getName() {
		return null;
	}
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
	ScrollRate getScrollRate();

	/**
	 * Indique si cette couche est solide (= peut être traversée) ou non.
	 *
	 * @return <code>true</code> si la couche est solide,
	 * <code>false</code> sinon (par défaut).
	 */
	public boolean isSolid();

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
