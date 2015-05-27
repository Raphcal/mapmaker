package fr.rca.mapmaker.model.composite;

import fr.rca.mapmaker.model.map.TileMap;
import java.awt.Point;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class Compositer {
	public static void compose(TileMap[] maps, Point[] topLeftPoints) {
		// TODO: Créer une nouvelle palette, pour chaque tuile l'ajouter que si elle a un contenu différent. Faire un mapping palette + n° tuile -> n° dans le composite.
		
		// TODO: Lister les layers à créer. Même nom + même vitesse = même layer.
		
		// TODO: Trier les layers en fonction du scrollrate puis (si égal) de leur index et enfin par le nom (?)
		
		// TODO: Placer les layers à x * scrollrate
		
		// TODO: Faire un éditeur graphique
	}
}
