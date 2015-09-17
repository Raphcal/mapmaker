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
	 * @param width Largeur du curseur.
	 * @param height Hauteur du curseur.
	 */
	void paintCursor(Graphics g, Palette palette, int x, int y, int width, int height);
	
	/**
	 * Dessine le curseur à l'emplacement donné.
	 * 
	 * @param g Contexte graphique où dessiner le curseur.
	 * @param palette Palette à utiliser.
	 * @param size Taille de chaque élément de la grille.
	 * @param x Abscisse du curseur.
	 * @param y Ordonnée du curseur.
	 * @param width Largeur du curseur.
	 * @param height Hauteur du curseur.
	 */
	void paintCursor(Graphics g, Palette palette, int size, int x, int y, int width, int height);
}
