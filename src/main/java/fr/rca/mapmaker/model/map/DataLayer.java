package fr.rca.mapmaker.model.map;

import java.awt.Rectangle;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface DataLayer extends Layer {
	int[] copyData();
	/**
	 * Met à jour tout ou partie de la couche.
	 * 
	 * @param tiles Données modifiées.
	 * @param source Rectangle modifié.
	 */
	void restoreData(int[] tiles, Rectangle source);
	/**
	 * Remplace les données et la taille de la couche.
	 * 
	 * @param tiles Données de la couche.
	 * @param width Nouvelle largeur de la couche.
	 * @param height Nouvelle hauteur de la couche. 
	 */
	void restoreData(int[] tiles, int width, int height);
	
	/**
	 * Remplace les données et la taille de la couche par
	 * ceux de la couche donnée.
	 * 
	 * @param source Couche à copier.
	 */
	void restoreData(DataLayer source);
}
