package fr.rca.mapmaker.model.palette;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Toolkit;
import java.awt.image.BufferedImage;
import java.awt.image.FilteredImageSource;
import java.awt.image.ImageFilter;
import java.awt.image.ImageProducer;
import java.awt.image.RGBImageFilter;
import java.io.File;
import java.io.IOException;
import java.net.URL;

import javax.imageio.ImageIO;

public class ImagePalette implements Palette, Flippable {
	
	private int tileSize;
	private BufferedImage tiles;
	private int selectedTile;
	
	private final int width;
	private final int length;
	
	private String name;
	
	private int deformX1;
	private int deformX2;
	private int deformY1;
	private int deformY2;
	
	public ImagePalette(BufferedImage image, int tileSize) {
		this.tileSize = tileSize;
		this.tiles = image;
		this.width = image.getWidth() / tileSize;
		this.length = width * (image.getHeight() / tileSize);
	}
	
	public ImagePalette(URL url, int tileSize) throws IOException {
		this(readAndConvertImage(ImageIO.read(url)), tileSize);
	}
	
	public ImagePalette(File file, int tileSize) throws IOException {
		this(readAndConvertImage(ImageIO.read(file)), tileSize);
	}
	
	public ImagePalette(File file, Color transparentColor, int tileSize) throws IOException {
		this(readAndFilterImage(file, transparentColor), tileSize);
		name = file.getName();
	}
	
	@Override
	public boolean isEditable() {
		return false;
	}
	
	@Override
	public void refresh() {
	}
	
	private static BufferedImage readAndConvertImage(BufferedImage rawImage) throws IOException {
		// Conversion de l'image de base au format ARGB (0xAARRGGBB)
		final BufferedImage image = new BufferedImage(rawImage.getWidth(), rawImage.getHeight(), BufferedImage.TYPE_INT_ARGB);
		image.setAccelerationPriority(1.0f);
		
		final Graphics2D graphics2d = image.createGraphics();
		graphics2d.drawImage(rawImage, 0, 0, null);
		graphics2d.dispose();
				
		return image;
	}
	
	private static BufferedImage readAndFilterImage(File file, Color transparentColor) throws IOException {
		// Conversion de l'image de base au format ARGB (0xAARRGGBB)
		final BufferedImage rawTiles = ImageIO.read(file);
		final BufferedImage opaqueImage = new BufferedImage(rawTiles.getWidth(), rawTiles.getHeight(), BufferedImage.TYPE_INT_ARGB);
		
		Graphics2D graphics2d = opaqueImage.createGraphics();
		graphics2d.drawImage(rawTiles, 0, 0, null);
		graphics2d.dispose();
		
		// Supprime tous les pixels de la couleur à rendre transparente.
		final int transparentRGB = transparentColor.getRGB();
		final ImageFilter colorFilter = new RGBImageFilter() {
			@Override
			public int filterRGB(int x, int y, int rgb) {
				
				if(rgb == transparentRGB)
					// Garde la teinte mais défini la valeur alpha à 0.
					return rgb & 0x00FFFFFF;
				else
					return rgb;
			}
	    };

	    final ImageProducer producer = new FilteredImageSource(opaqueImage.getSource(), colorFilter);
	    final Image filteredImage = Toolkit.getDefaultToolkit().createImage(producer);

	    // Créé l'image finale
	    final BufferedImage image = new BufferedImage(rawTiles.getWidth(), rawTiles.getHeight(), BufferedImage.TYPE_INT_ARGB);
	    image.setAccelerationPriority(1.0f);
	    graphics2d = image.createGraphics();
		graphics2d.drawImage(filteredImage, 0, 0, null);
		graphics2d.dispose();
		
		return image;
	}
	
	@Override
	public void paintTile(Graphics g, int tile, int x, int y, int size) {
		if(tile >= 0 && tile < length) {
			final int sourceX = (tile % width) * tileSize;
			final int sourceY = (tile / width) * tileSize;
			
			g.drawImage(
					tiles, x + deformX1, y + deformY1,
					x + size + deformX2, y + size + deformY2,
					sourceX, sourceY, sourceX + tileSize, sourceY + tileSize, null);
		}
	}

	@Override
	public int getTileSize() {
		return tileSize;
	}

	@Override
	public int getTileSize(int tile) {
		return getTileSize();
	}
	
	@Override
	public int size() {
		return length;
	}
	
	@Override
	public int getSelectedTile() {
		return selectedTile;
	}
	
	@Override
	public void setSelectedTile(int tile) {
		this.selectedTile = tile;
	}

	public BufferedImage getTiles() {
		return tiles;
	}

	@Override
	public void flipTiles(Flip flip) {
		switch(flip) {
			case NORMAL:
				deformX1 = 0;
				deformX2 = 0;
				deformY1 = 0;
				deformY2 = 0;
				break;
				
			case HORIZONTAL:
				deformX1 = tileSize;
				deformX2 = -tileSize;
				break;
				
			case VERTICAL:
				deformY1 = tileSize;
				deformY2 = -tileSize;
				break;
		}
	}
	
	@Override
	public String toString() {
		if(name != null)
			return name;
		else
			return super.toString();
	}
}
