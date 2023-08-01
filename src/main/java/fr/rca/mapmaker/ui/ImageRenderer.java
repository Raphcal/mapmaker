package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Flip;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.Flippable;
import java.awt.Dimension;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.util.Collections;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ImageRenderer extends AbstractLayerPainter {

	private Flip flip;
	private boolean forceSquare;
	private int padding;

	/**
	 * Dessine la grille dans une image.
	 *
	 * @return Une image contenant la grille.
	 */
	public BufferedImage renderImage(Grid grid, boolean opaque) {
		final int tileSize = grid.getTileSize();

		final TileMap tileMap = grid.getTileMap();

		final int imageWidth = tileMap.getWidth() * tileSize;
		final int imageHeight = tileMap.getHeight() * tileSize;

		return renderImage(tileMap.getLayers(), tileMap.getPalette(),
				new Dimension(imageWidth, imageHeight), tileSize, opaque);
	}

	/**
	 * Dessine la couche dans une image avec la palette donnée.
	 *
	 * @param layer Couche à dessiner.
	 * @param palette Palette à utiliser.
	 * @param tileSize Taille des tuiles.
	 * @return Une image contenant la couche.
	 */
	public BufferedImage renderImage(Layer layer, Palette palette, int tileSize) {
		return renderImage(Collections.singletonList(layer), palette,
				new Dimension(layer.getWidth() * (tileSize + padding + padding), layer.getHeight() * (tileSize + padding + padding)), tileSize, false);
	}

	/**
	 * Dessine la couche dans une image avec la palette donnée.
	 *
	 * @param layer Couche à dessiner.
	 * @param palette Palette à utiliser.
	 * @param bounds Surface en tuiles à dessiner.
	 * @param tileSize Taille des tuiles.
	 * @return Une image contenant la couche.
	 */
	public BufferedImage renderImage(TileLayer layer, Palette palette, Rectangle bounds, int tileSize) {
		final TileLayer copy = new TileLayer(layer.getTiles(), layer.getDimension(), bounds);
		return renderImage(copy, palette, tileSize);
	}

	/**
	 * Dessine une image transparente de la taille donnée.
	 *
	 * @param width Largeur de la couche.
	 * @param height Hauteur de la couche.
	 * @param tileSize Taille des tuiles.
	 * @return Une image contenant la couche.
	 */
	public BufferedImage renderImage(int width, int height, int tileSize) {
		return renderImage(Collections.<Layer>emptyList(), null,
				new Dimension(width * (tileSize + padding + padding), height * (tileSize + padding + padding)), tileSize, false);
	}

	/**
	 * Dessine la grille dans une image.
	 *
	 * @param layers Liste des couches à dessiner.
	 * @param palette Palette à utiliser.
	 * @param size Taille de l'image.
	 * @param tileSize taille des tuiles.
	 * @param opaque <code>true</code> pour afficher la couleur de fond,
	 * <code>false</code> pour garder le fond transparent.
	 * @return Une image contenant la grille.
	 */
	public BufferedImage renderImage(List<Layer> layers, Palette palette, Dimension size, int tileSize, boolean opaque) {
		final int width;
		final int height;

		if (forceSquare) {
			width = Math.max(size.width, size.height);
			height = Math.max(size.width, size.height);
		} else {
			width = size.width;
			height = size.height;
		}

		return renderImage(layers, palette, new Rectangle(0, 0, width, height), null, tileSize, opaque);
	}

	/**
	 * Dessine les couches données avec la palette donnée.
	 *
	 * @param layers Couches à dessiner.
	 * @param palette Palette à utiliser.
	 * @param bounds Surface à dessiner.
	 * @param cameraTopLeft Emplacement de la caméra.
	 * @param tileSize Taille des tuiles.
	 * @param opaque <code>true</code> pour dessiner une couleur de fond,
	 * <code>false</code> pour laisser le fond transparent.
	 * @return L'image dessinée.
	 */
	public BufferedImage renderImage(List<Layer> layers, Palette palette, Rectangle bounds, Point cameraTopLeft, int tileSize, boolean opaque) {
		final BufferedImage image = new BufferedImage(bounds.width, bounds.height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();

		if (opaque) {
			graphics.setColor(getBackground());
			graphics.fillRect(0, 0, bounds.width, bounds.height);
		}

		if (palette instanceof Flippable) {
			((Flippable) palette).flipTiles(flip);
		}

		for (final Layer layer : layers) {
			paintLayer(layer, palette, bounds, tileSize, padding, cameraTopLeft, graphics);
		}

		if (palette instanceof Flippable) {
			((Flippable) palette).flipTiles(Flip.NORMAL);
		}

		graphics.dispose();

		return image;
	}

	public void setFlip(Flip flip) {
		this.flip = flip;
	}

	public void setForceSquare(boolean forceSquare) {
		this.forceSquare = forceSquare;
	}

	public void setPadding(int padding) {
		this.padding = padding;
	}
}
