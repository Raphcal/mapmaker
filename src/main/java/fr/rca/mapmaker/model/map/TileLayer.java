package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.LayerChangeListener;
import fr.rca.mapmaker.model.SizeChangeListener;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


public class TileLayer implements Layer, HasSizeChangeListeners {

	private String name;
	/**
	 * Largeur de la couche.
	 */
	private int width;
	
	/**
	 * Hauteur de la couche.
	 */
	private int height;
	
	/**
	 * Liste des tuiles.
	 */
	private int[] tiles;
	
	/**
	 * Vitesse de défilement pour le parallaxe. 
	 */
	private float scrollRate = 1.0f;
	
	/**
	 * Visibilité de la couche.
	 */
	private boolean visible = true;
	
	/**
	 * Liste de listeners.
	 */
	private ArrayList<LayerChangeListener> listeners = new ArrayList<LayerChangeListener>();
	private ArrayList<SizeChangeListener> sizeChangeListeners = new ArrayList<SizeChangeListener>();
	
	/**
	 * Créé une nouvelle couche de la taille donnée.
	 * 
	 * @param width Largeur de la couche.
	 * @param height Hauteur de la couche.
	 */
	public TileLayer(int width, int height) {
		this.width = width;
		this.height = height;
		this.tiles = new int[width * height];
		Arrays.fill(this.tiles, -1);
	}

	/**
	 * Modifie le nom de ce calque.
	 */
	public void setName(String name) {
		this.name = name;
	}
	
	/**
	 * Récupère la tuile à l'emplacement donné.
	 * 
	 * @param x Abscisse de la tuile.
	 * @param y Ordonnée de la tuile.
	 * @return Le numéro de la tuile à l'emplacement donné.
	 */
	@Override
	public int getTile(int x, int y) {
		return tiles[y * width + x];
	}
	
	/**
	 * Récupère la tuile à l'emplacement donné.
	 * 
	 * @param p Coordonnées de la tuile.
	 * @return Le numéro de la tuile à l'emplacement donné.
	 */
	@Override
	public int getTile(Point p) {
		return tiles[p.y * width + p.x];
	}
	
	/**
	 * Défini la tuile à l'emplacement donné.
	 * 
	 * @param x Abscisse de la tuile.
	 * @param y Ordonnée de la tuile.
	 * @param tile Numéro de la tuile.
	 */
	public void setTile(int x, int y, int tile) {
		
		if(y >= 0 && y < height && x >= 0 && x < width) {
			tiles[y * width + x] = tile;
			fireLayerChanged(new Rectangle(x, y, 1, 1));
		}
	}
	
	/**
	 * Défini la tuile à l'emplacement donné.
	 * 
	 * @param x Abscisse de la tuile.
	 * @param y Ordonnée de la tuile.
	 * @param tile Numéro de la tuile.
	 */
	public void setTile(Point p, int tile) {
		tiles[p.y * width + p.x] = tile;
		
		fireLayerChanged(new Rectangle(p.x, p.y, 1, 1));
	}
	
	/**
	 * Défini la tuile aux emplacements définis par la forme donnée.
	 * 
	 * @param shape Forme à modifier.
	 * @param tile Numéro de la tuile.
	 */
	public void setTiles(Shape shape, int tile) {
		
		final Rectangle bounds = shape.getBounds();
		
		final int minX = Math.max(0, bounds.x);
		final int minY = Math.max(0, bounds.y);
		
		final int maxX = Math.min(width, (int) bounds.getMaxX());
		final int maxY = Math.min(height, (int) bounds.getMaxY());
		
		for(int y = minY; y < maxY; y++) {
			for(int x = minX; x < maxX; x++) {
				if(shape.contains(x, y)) {
					tiles[y * width + x] = tile;
				}
			}
		}
		
		fireLayerChanged(bounds);
	}
	
	/**
	 * Trace un trait avec la tuile sélectionnée du point 1 au point 2.
	 * 
	 * @param p1 Première extrémité du trait.
	 * @param p2 Deuxième extrémité du trait.
	 * @param tile Numéro de la tuile.
	 */
	public void setTiles(Point p1, Point p2, int tile) {
		
		final int w = p2.x - p1.x;
		final int h = p2.y - p1.y;
		final double length = Math.sqrt(w * w + h * h);
		
		final double stepX = (double) w / length;
		final double stepY = (double) h / length;
		
		double x = p1.x;
		double y = p1.y;
		
		final int max = (int) Math.ceil(length);
		for(int i = 0; i < max; i++) {
			tiles[(int) (Math.round(y) * this.width + Math.round(x))] = tile;
			
			x += stepX;
			y += stepY;
		}
		
		fireLayerChanged(new Rectangle(
				Math.min(p1.x, p2.x), Math.min(p1.y, p2.y),
				Math.abs(p1.x - p2.x) + 1, Math.abs(p1.y - p2.y) + 1));
	}
	
	/**
	 * Récupère la largeur de la couche.
	 * 
	 * @return La largeur.
	 */
	@Override
	public int getWidth() {
		return width;
	}
	
	/**
	 * Récupère la hauteur de la couche.
	 * 
	 * @return La hauteur.
	 */
	@Override
	public int getHeight() {
		return height;
	}
	
	/**
	 * Récupère la vitesse de défilement associée à la couche.
	 * 
	 * @return La vitesse de défilement.
	 */
	@Override
	public float getScrollRate() {
		return scrollRate;
	}
	
	/**
	 * Défini la vitesse de défilement associée à la couche.
	 * 
	 * @param scrollRate La vitesse de défilement.
	 */
	public void setScrollRate(float scrollRate) {
		this.scrollRate = scrollRate;
	}
	
	/**
	 * Récupère la visibilité de la couche.
	 * 
	 * @return <code>true</code> si la couche est visible,
	 * <code>false</code> sinon.
	 */
	@Override
	public boolean isVisible() {
		return visible;
	}
	
	/**
	 * Permet d'afficher ou de masquer la couche.
	 * 
	 * @param visible <code>true</code> pour afficher la couche,
	 * <code>false</code> pour la masquer.
	 */
	public void setVisible(boolean visible) {
		this.visible = visible;
	}
	
	/**
	 * Redimensionne la couche à la taille donnée.
	 * 
	 * @param width Nouvelle largeur.
	 * @param height Nouvelle hauteur.
	 */
	public void resize(int width, int height) {
		
		final int[] resizedTiles = new int[width * height];
		Arrays.fill(resizedTiles, -1);

		final int minHeight = Math.min(height, this.height);
		final int minWidth = Math.min(width, this.width);
		
		for(int y = 0; y < minHeight; y++) {
			for(int x = 0; x < minWidth; x++) {
				resizedTiles[y * width + x] = tiles[y * this.width + x];
			}
		}
		
		final Dimension oldDimension = new Dimension(this.width, this.height);
		
		this.tiles = resizedTiles;
		this.width = width;
		this.height = height;
		
		fireSizeChanged(oldDimension, new Dimension(width, height));
	}
	
	/**
	 * Déplace le contenu de la couche.
	 * 
	 * @param offsetX Décalage horizontal.
	 * @param offsetY Décalage vertical.
	 */
	public void translate(int offsetX, int offsetY) {
		
		final int[] translatedTiles = new int[width * height];
		Arrays.fill(translatedTiles, -1);
		
		final int sourceX = Math.max(0, -offsetX);
		final int sourceY = Math.max(0, -offsetY);
		final int destinationX = Math.max(0, offsetX);
		final int destinationY = Math.max(0, offsetY);
		final int copyWidth = width - Math.abs(offsetX);
		final int copyHeight = height - Math.abs(offsetY);
		
		for(int j = 0; j < copyHeight; j++) {
			System.arraycopy(
					this.tiles, (j + sourceY) * width + sourceX,
					translatedTiles, (j + destinationY) * width + destinationX, copyWidth);
		}
		
		this.tiles = translatedTiles;
		fireLayerChanged(new Rectangle(0, 0, width, height));
	}
	
	/**
	 * Vide le contenu de la couche.
	 */
	public void clear() {
		
		Arrays.fill(this.tiles, -1);
		fireLayerChanged(new Rectangle(0, 0, width, height));
	}
	
	/**
	 * Vide le contenu de la forme donnée.
	 * 
	 * @param shape Forme à vider.
	 */
	public void clear(Shape shape) {
		
		setTiles(shape, -1);
	}
	
	/**
	 * Vide le contenu à l'emplacement donné.
	 * 
	 * @param x Abscisse de la tuile.
	 * @param y Ordonnée de la tuile.
	 */
	public void clear(int x, int y) {
		
		setTile(x, y, -1);
	}
	
	/**
	 * Copie le contenu de la couche donnée sur la couche actuelle.
	 * Les points transparents (-1) ne seront pas copiés.
	 * 
	 * @param layer Couche à recopier.
	 */
	public void merge(TileLayer layer) {
		
		final int minHeight = Math.min(this.height, layer.height);
		final int minWidth = Math.min(this.width, layer.width);
		
		int minDirtyX = Integer.MAX_VALUE;
		int minDirtyY = Integer.MAX_VALUE;
		int maxDirtyX = Integer.MIN_VALUE;
		int maxDirtyY = Integer.MIN_VALUE;
		
		for(int y = 0; y < minHeight; y++) {
			for(int x = 0; x < minWidth; x++) {
				int tile = layer.tiles[y * layer.width + x];
				
				if(tile != -1) {
					if(tile == -2)
						tile = -1;
					
					final int index = y * this.width + x;
					
					if(this.tiles[index] != tile) {
						this.tiles[index] = tile;
						
						// Calcul du rectangle modifié
						minDirtyX = Math.min(minDirtyX, x);
						minDirtyY = Math.min(minDirtyY, y);
						
						maxDirtyX = Math.max(maxDirtyX, x);
						maxDirtyY = Math.max(maxDirtyY, y);
					}
				}
			}
		}
		
		fireLayerChanged(new Rectangle(minDirtyX, minDirtyY, maxDirtyX - minDirtyX + 1, maxDirtyY - minDirtyY + 1));
	}
	
	/**
	 * Supprime de la couche tous les points non vide de la couche donnée.
	 * 
	 * @param layer Couche à exclure.
	 */
	public void clear(TileLayer layer) {
		
		final int minHeight = Math.min(this.height, layer.height);
		final int minWidth = Math.min(this.width, layer.width);
		
		int minDirtyX = Integer.MAX_VALUE;
		int minDirtyY = Integer.MAX_VALUE;
		int maxDirtyX = Integer.MIN_VALUE;
		int maxDirtyY = Integer.MIN_VALUE;
		
		for(int y = 0; y < minHeight; y++) {
			for(int x = 0; x < minWidth; x++) {
				final int tile = layer.tiles[y * layer.width + x];
				
				if(tile != -1) {
					final int index = y * this.width + x;
					
					if(this.tiles[index] != -1) {
						this.tiles[index] = -1;
						
						// Calcul du rectangle modifié
						minDirtyX = Math.min(minDirtyX, x);
						minDirtyY = Math.min(minDirtyY, y);
						
						maxDirtyX = Math.max(maxDirtyX, x);
						maxDirtyY = Math.max(maxDirtyY, y);
					}
				}
			}
		}
		
		fireLayerChanged(new Rectangle(minDirtyX, minDirtyY, maxDirtyX - minDirtyX + 1, maxDirtyY - minDirtyY + 1));
	}
	
	/**
	 * Redimensionne les couches données en fonction de la couche ayant la plus
	 * grande taille sans prendre en compte les parallaxes.
	 * 
	 * @param layers Tableau de couches à redimensionner.
	 * @return Les couches redimensionnées sous forme de liste. 
	 */
	public static List<TileLayer> normalize(TileLayer... layers) {
		
		int width = 0;
		int height = 0;
		
		for(final TileLayer layer : layers) {
			if(layer.getWidth() > width)
				width = layer.getWidth();
			
			if(layer.getHeight() > height)
				height = layer.getHeight();
		}
		
		for(final TileLayer layer : layers) {
			final int normalizedWidth = (int) (width * layer.getScrollRate());
			final int normalizedHeight = (int) (height * layer.getScrollRate());
			layer.resize(normalizedWidth, normalizedHeight);
		}
		
		return Arrays.asList(layers);
	}
	
	/**
	 * Ajoute le listener à la liste des listeners.
	 * 
	 * @param listener Nouveau listener.
	 */
	public void addLayerChangeListener(LayerChangeListener listener) {
		
		listeners.add(listener);
	}
	
	/**
	 * Supprime le listener de la liste des listeners.
	 * 
	 * @param listener Le listener à supprimer.
	 */
	public void removeLayerChangeListener(LayerChangeListener listener) {
		
		listeners.remove(listener);
	}
	
	/**
	 * Déclenche l'événement <code>layerChanged</code>.
	 * 
	 * @param dirtyRectangle Rectangle modifié.
	 */
	protected void fireLayerChanged(Rectangle dirtyRectangle) {
		
		for(int index = listeners.size() - 1; index >= 0; index--)
			listeners.get(index).layerChanged(this, dirtyRectangle);
	}
	
	@Override
	public void addSizeChangeListener(SizeChangeListener listener) {
		sizeChangeListeners.add(listener);
	}
	
	@Override
	public void removeSizeChangeListener(SizeChangeListener listener) {
		sizeChangeListeners.remove(listener);
	}
	
	protected void fireSizeChanged(Dimension oldSize, Dimension newSize) {
		
		for(final SizeChangeListener listener : sizeChangeListeners)
			listener.sizeChanged(this, oldSize, newSize);
	}
	
	/**
	 * Renvoie une copie des données de cette couche.
	 * 
	 * @return Une copie des données.
	 */
	public int[] copyData() {
		
		return this.tiles.clone();
	}
	
	/**
	 * Restaure les données à partir du tableau donné en argument. 
	 * 
	 * @param tiles Données à restaurer.
	 */
	public void restoreData(int[] tiles, Rectangle source) {
		
		this.tiles = tiles.clone();
		
		if(source == null)
			source = new Rectangle(0, 0, width, height);
			
		fireLayerChanged(source);
	}
	
	/**
	 * Inverse la couche horizontalement.
	 */
	public void flipHorizontally() {
		int[] mirror = new int[this.tiles.length];
		
		int index = 0;
		for(int y = 0; y < height; y++) {
			for(int x = 1; x <= width; x++) {
				mirror[index] = getTile(width - x, y);
				index++;
			}
		}
		
		this.tiles = mirror;
		fireLayerChanged(new Rectangle(width, height));
	}
	
	/**
	 * Inverse la couche verticalement.
	 */
	public void flipVertically() {
		int[] mirror = new int[this.tiles.length];
		
		int index = 0;
		for(int y = 1; y <= height; y++) {
			for(int x = 0; x < width; x++) {
				mirror[index] = getTile(x, height - y);
				index++;
			}
		}
		
		this.tiles = mirror;
		fireLayerChanged(new Rectangle(width, height));
	}

	@Override
	public String toString() {
		
		if(name == null)
			return super.toString();
		else
			return name;
	}
}
