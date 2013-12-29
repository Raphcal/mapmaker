package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Flip;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.Flippable;
import java.awt.Dimension;
import java.awt.Graphics2D;
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
	 * Dessine la grille dans une image.
	 * 
	 * @param bounds Rectangle à afficher.
	 * @param tileSize taille des tuiles.
	 * @param opaque <code>true</code> pour afficher la couleur de fond,
	 * <code>false</code> pour garder le fond transparent.
	 * @return Une image contenant la grille.
	 */
	public BufferedImage renderImage(List<Layer> layers, Palette palette, Dimension size, int tileSize, boolean opaque) {
		final int width;
		final int height;
		
		if(forceSquare) {
			width = Math.max(size.width, size.height);
			height = Math.max(size.width, size.height);
			
		} else {
			width = size.width;
			height = size.height;
		}
		
		final BufferedImage image = new BufferedImage(width, height, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		
		if(opaque) {
			graphics.setColor(getBackground());
			graphics.fillRect(0, 0, size.width, size.height);
		}
		
		if(palette instanceof Flippable) {
			((Flippable)palette).flipTiles(flip);
		}
		
		for(final Layer layer : layers) {
			paintLayer(layer, palette, new Rectangle(size), tileSize, padding, null, graphics);
		}
		
		if(palette instanceof Flippable) {
			((Flippable)palette).flipTiles(Flip.NORMAL);
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
