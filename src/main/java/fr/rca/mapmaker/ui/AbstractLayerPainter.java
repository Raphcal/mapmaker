package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 * Classe de base pour dessiner un Layer.
 * 
 * @author daeke
 */
public abstract class AbstractLayerPainter extends JComponent {
	
	/**
	 * Dessine la couche donnée sur l'image représentée par <code>g</code>.
	 * 
	 * @param layer Couche à dessiner.
	 * @param palette Palette à utiliser.
	 * @param clipBounds Surface à dessiner.
	 * @param tileSize Taille des blocs.
	 * @param viewpoint Point de vue (centre du scrolling). Utilisé pour le décalage des plans (parallaxe).
	 * @param g Image de destination.
	 */
	protected void paintLayer(final Layer layer, Palette palette, final Rectangle clipBounds,
			final int tileSize, Point viewpoint, Graphics g) {
		this.paintLayer(layer, palette, clipBounds, tileSize, 0, viewpoint, g);
	}
	
	protected void paintLayer(final Layer layer, Palette palette, final Rectangle clipBounds,
			final double tileSize, Point viewpoint, Graphics g) {
		this.paintLayer(layer, palette, clipBounds, tileSize, 0, viewpoint, g);
	}
	
	/**
	 * Dessine la couche donnée sur l'image représentée par <code>g</code>.
	 * 
	 * @param layer Couche à dessiner.
	 * @param palette Palette à utiliser.
	 * @param clipBounds Surface à dessiner.
	 * @param tileSize Taille des blocs.
	 * @param padding Espace horizontal et vertical entre chaque bloc
	 * @param viewpoint Point de vue (centre du scrolling). Utilisé pour le décalage des plans (parallaxe).
	 * @param g Image de destination.
	 */
	protected void paintLayer(final Layer layer, Palette palette, final Rectangle clipBounds,
			final int tileSize, final int padding, Point viewpoint, Graphics g) {
		// Définition du point de vue, si aucun n'a été donné.
		if(viewpoint == null) {
			viewpoint = new Point(0, 0);
		}
		
		// Emplacement du point supérieur gauche de la couche.
		// Utilisé pour centrer correctement les plans ayant une vitesse
		// de défilement différente de 1.
		final int originX = (int) (viewpoint.x * (1 - layer.getScrollRate().getX())) + padding;
		final int originY = (int) (viewpoint.y * (1 - layer.getScrollRate().getY())) + padding;
		
		// Coordonnées du premier point à afficher.
		final int startX = (int) ((clipBounds.x * layer.getScrollRate().getX()) / tileSize);
		final int startY = (int) ((clipBounds.y * layer.getScrollRate().getY()) / tileSize);
		
		// Coordonnées du dernier point à afficher.
		final int maxX = (int) Math.ceil((double) (clipBounds.x + clipBounds.width) / tileSize);
		final int maxY = (int) Math.ceil((double) (clipBounds.y + clipBounds.height) / tileSize);
		
		// Décalage entre chaque tuile
		final int spaceX = tileSize + padding + padding;
		final int spaceY = tileSize + padding + padding;
		
		// Affichage de la couche
		for(int y = startY; y < maxY; y++) {
			for(int x = startX; x < maxX; x++) {
				palette.paintTile(g, layer.getTile(x, y), originX + x * spaceX, originY + y * spaceY, tileSize);
			}
		}
	}
	
	// FIXME: Ne fonctionne pas correctement
	protected void paintLayer(final Layer layer, Palette palette, final Rectangle clipBounds,
			final double tileSize, final double padding, Point viewpoint, Graphics g) {
		// Définition du point de vue, si aucun n'a été donné.
		if(viewpoint == null) {
			viewpoint = new Point(0, 0);
		}
		
		// Emplacement du point supérieur gauche de la couche.
		// Utilisé pour centrer correctement les plans ayant une vitesse
		// de défilement différente de 1.
		final double originX = viewpoint.getX() * (1.0 - layer.getScrollRate().getX()) + padding;
		final double originY = viewpoint.getY() * (1.0 - layer.getScrollRate().getY()) + padding;
		
		// Coordonnées du premier point à afficher.
		final double startX = (clipBounds.getX() * layer.getScrollRate().getX()) / tileSize;
		final double startY = (clipBounds.getY() * layer.getScrollRate().getY()) / tileSize;
		
		// Coordonnées du dernier point à afficher.
		final double maxX = Math.min((clipBounds.getX() + clipBounds.getWidth()) / tileSize, (double) layer.getWidth());
		final double maxY = Math.min((clipBounds.getY() + clipBounds.getHeight()) / tileSize, (double) layer.getHeight());
		
		// Décalage entre chaque tuile
		final double spaceX = tileSize + padding + padding;
		final double spaceY = tileSize + padding + padding;
		
		// Affichage de la couche
		for(double y = startY; y < maxY; y++) {
			for(double x = startX; x < maxX; x++) {
				palette.paintTile(g, layer.getTile((int) x, (int) y), (int) (originX + x * spaceX), (int) (originY + y * spaceY), (int) tileSize);
			}
		}
	}
}
