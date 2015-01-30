package fr.rca.mapmaker.model.selection;

import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Graphics;

public interface SelectionStyle {
	/**
	 * Dessine le curseur à l'emplacement donné.
	 * 
	 * @param g Contexte graphique où dessiner le curseur.
	 * @param palette Palette à utiliser.
	 * @param x Abscisse du curseur.
	 * @param y Ordonnée du curseur.
	 * @param size Taille du curseur.
	 */
	void paintCursor(Graphics g, Palette palette, int x, int y, int size);
}
