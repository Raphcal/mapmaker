package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.HasPropertyChangeListeners;
import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.LayerChangeListener;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.palette.ColorPalette;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import lombok.Data;

@Data
public class TileLayer implements DataLayer, HasSizeChangeListeners, HasPropertyChangeListeners, HasLayerPlugin {
	public static final int EMPTY_TILE = -1;

	/**
	 * Carte parente.
	 * NOTE: Pas sûr de l'intérêt.
	 */
	private TileMap parent;

	/**
	 * Nom de la couche.
	 */
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
	private ScrollRate scrollRate = new ScrollRate();

	/**
	 * Indique si le contenu de cette couche est solide.
	 */
	private boolean solid;

	/**
	 * Visibilité de la couche.
	 */
	private boolean visible = true;

	/**
	 * Décorateur de TileLayer.
	 */
	private final Map<String, LayerPlugin> plugins = new HashMap<>();

	/**
	 * Liste de listeners.
	 */
	private final List<LayerChangeListener> listeners = new ArrayList<>();
	private final List<SizeChangeListener> sizeChangeListeners = new ArrayList<>();

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);

	public TileLayer() {
		this(0, 0);
	}

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
		Arrays.fill(this.tiles, EMPTY_TILE);
	}

	public TileLayer(int width, int height, LayerPlugin plugin) {
		this(width, height, Collections.singleton(plugin));
	}

	public TileLayer(int width, int height, Collection<LayerPlugin> plugins) {
		this(width, height);
		if (plugins != null) {
			for (final LayerPlugin plugin : plugins) {
				this.plugins.put(plugin.name(), plugin);
			}
		}
	}

	/**
	 * Créé une copie de la couche donnée en argument.
	 *
	 * @param other Couche à copier.
	 */
	public TileLayer(TileLayer other) {
		this(other.tiles, new Dimension(other.width, other.height), null);
		copyTileLayerFields(other);
	}

	/**
	 * Créé une copie de la couche donnée en argument.
	 *
	 * @param other Couche à copier.
	 */
	public TileLayer(DataLayer other) {
		this.width = other.getWidth();
		this.height = other.getHeight();
		this.tiles = other.copyData();
		this.name = other.getName();
		if (other instanceof TileLayer) {
			copyTileLayerFields((TileLayer) other);
		}
	}

	public TileLayer(int[] data, Dimension dimension, Rectangle copySurface) {
		if (copySurface == null) {
			copySurface = new Rectangle(0, 0, dimension.width, dimension.height);
		}

		this.width = copySurface.width;
		this.height = copySurface.height;

		if (copySurface.width == dimension.width && copySurface.height == dimension.height
				&& copySurface.x == 0 && copySurface.y == 0) {
			this.tiles = data.clone();
		} else {
			this.tiles = new int[width * height];

			final int lastY = Math.min(height, dimension.height - copySurface.y);
			for (int y = 0; y < lastY; y++) {
				System.arraycopy(data, (y + copySurface.y) * dimension.width + copySurface.x, tiles, y * width, Math.min(width, dimension.width - copySurface.x));
			}
		}
	}

	/**
	 * Créé une nouvelle couche à partir des informations données.
	 * <p>
	 * Attention, le tableau de tuiles donné est utilisé tel quel et n'est pas
	 * copié.
	 *
	 * @param width Largeur de la couche.
	 * @param height Hauteur de la couche.
	 * @param data Données de la couche.
	 */
	public TileLayer(int width, int height, int[] data) {
		this.width = width;
		this.height = height;
		this.tiles = data;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 59 * hash + this.width;
		hash = 59 * hash + this.height;
		hash = 59 * hash + Arrays.hashCode(this.tiles);
		hash = 59 * hash + Objects.hashCode(this.scrollRate);
		hash = 59 * hash + (this.solid ? 1 : 0);
		hash = 59 * hash + Objects.hashCode(this.plugins);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final TileLayer other = (TileLayer) obj;
		if (this.width != other.width) {
			return false;
		}
		if (this.height != other.height) {
			return false;
		}
		if (this.solid != other.solid) {
			return false;
		}
		if (!Arrays.equals(this.tiles, other.tiles)) {
			return false;
		}
		if (!Objects.equals(this.scrollRate, other.scrollRate)) {
			return false;
		}
		return Objects.equals(this.plugins, other.plugins);
	}

	

	private void copyTileLayerFields(TileLayer other) {
		this.name = other.name;
		for (final LayerPlugin plugin : other.plugins.values()) {
			this.plugins.put(plugin.name(), plugin.copy());
		}

		if (other.scrollRate != null) {
			this.scrollRate = new ScrollRate(other.scrollRate);
		}
	}

	/**
	 * Modifie le nom de ce calque.
	 *
	 * @param name Nom du calque.
	 */
	public void setName(String name) {
		final String oldName = this.name;
		this.name = name;
		propertyChangeSupport.firePropertyChange("name", oldName, this.name);
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
		final int index = y * width + x;
		return (x >= 0 && x < width) && (y >= 0 && y < height) && index < tiles.length
				? tiles[index]
				: -1;
	}

	/**
	 * Récupère la tuile à l'emplacement donné.
	 *
	 * @param p Coordonnées de la tuile.
	 * @return Le numéro de la tuile à l'emplacement donné.
	 */
	@Override
	public int getTile(Point p) {
		return getTile(p.x, p.y);
	}

	/**
	 * Défini la tuile à l'emplacement donné.
	 *
	 * @param x Abscisse de la tuile.
	 * @param y Ordonnée de la tuile.
	 * @param tile Numéro de la tuile.
	 */
	public void setTile(int x, int y, int tile) {
		if (y >= 0 && y < height && x >= 0 && x < width) {
			setRawTile(x, y, tile);
			fireLayerChanged(new Rectangle(x, y, 1, 1));
		}
	}

	/**
	 * Défini directement la tuile à l'emplacement donné sans faire aucun
	 * contrôle ni activer d'événement.
	 *
	 * @param x Abscisse de la tuile.
	 * @param y Ordonnée de la tuile.
	 * @param tile Numéro de la tuile.
	 */
	public void setRawTile(int x, int y, int tile) {
		tiles[y * width + x] = tile;
	}

	/**
	 * Défini la tuile à l'emplacement donné.
	 *
	 * @param p Position de la tuile.
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

		for (int y = minY; y < maxY; y++) {
			for (int x = minX; x < maxX; x++) {
				if (shape.contains(x, y)) {
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
		for (int i = 0; i < max; i++) {
			final int index = (int) (Math.round(y) * this.width + Math.round(x));

			if (y >= 0 && y < height && x >= 0 && x < width && index < tiles.length) {
				tiles[index] = tile;
			}

			x += stepX;
			y += stepY;
		}

		fireLayerChanged(new Rectangle(
				Math.min(p1.x, p2.x), Math.min(p1.y, p2.y),
				Math.abs(p1.x - p2.x) + 1, Math.abs(p1.y - p2.y) + 1));
	}

	public Color getRGB(int x, int y, ColorPalette colorPalette) {
		int tile = getTile(x, y);
		return colorPalette.getColor(tile);
	}

	@Override
	public <L extends LayerPlugin> L getPlugin(Class<L> clazz) {
		return getPlugin(clazz, LayerPlugins.nameOf(clazz));
	}

	@Override
	public <L extends LayerPlugin> L getPlugin(Class<L> clazz, String name) {
		return (L) plugins.get(name);
	}

	@Override
	public void setPlugin(LayerPlugin plugin) {
		final String pluginName = plugin.name();
		final LayerPlugin oldPlugin = plugins.get(pluginName);
		plugins.put(pluginName, plugin);
		propertyChangeSupport.firePropertyChange("plugin-" + pluginName, oldPlugin, plugin);
	}

	@Override
	public <L extends LayerPlugin> void removePlugin(Class<L> clazz) {
		removePlugin(LayerPlugins.nameOf(clazz));
	}

	public void removePlugin(String name) {
		plugins.remove(name);
	}

	@Override
	public Collection<LayerPlugin> getPlugins() {
		return plugins.values();
	}

	/**
	 * Détermine si au moins une tuile a été positionnée sur cette couche.
	 *
	 * @return <code>true</code> si la couche est vide, <code>false</code>
	 * sinon.
	 */
	public boolean isEmpty() {
		for (final int tile : tiles) {
			if (tile != EMPTY_TILE) {
				return false;
			}
		}
		return true;
	}

	/**
	 * Redimensionne la couche à la taille donnée.
	 *
	 * @param width Nouvelle largeur.
	 * @param height Nouvelle hauteur.
	 */
	public void resize(int width, int height) {
		final int[] resizedTiles = new int[width * height];
		Arrays.fill(resizedTiles, EMPTY_TILE);

		final int minHeight = Math.min(height, this.height);
		final int minWidth = Math.min(width, this.width);

		final int max = minHeight * minWidth;
		for (int index = 0; index < max; index++) {
			final int x = index % minWidth;
			final int y = index / minWidth;
			final int indexInOldArray = y * this.width + x;
			if (indexInOldArray < tiles.length) {
				resizedTiles[y * width + x] = tiles[indexInOldArray];
			}
		}

		final Dimension oldDimension = new Dimension(this.width, this.height);

		this.tiles = resizedTiles;
		this.width = width;
		this.height = height;

		fireSizeChanged(oldDimension, new Dimension(width, height));
	}

	public void scale(double rate) {
		scale((int) (width * rate), (int) (height * rate));
	}

	/**
	 * Etire l'image à la taille donnée.
	 *
	 * @param width Nouvelle largeur.
	 * @param height Nouvelle hauteur.
	 */
	public void scale(int width, int height) {
		final int[] scaledTiles = new int[width * height];
		Arrays.fill(scaledTiles, EMPTY_TILE);

		final double ratioX = (double)this.width / width;
		final double ratioY = (double)this.height / height;

		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final int xInOld = (int) Math.min(Math.round(x * ratioX), this.width - 1);
				final int yInOld = (int) Math.min(Math.round(y * ratioY), this.height - 1);
				scaledTiles[y * width + x] = tiles[yInOld * this.width + xInOld];
			}
		}

		final Dimension oldDimension = new Dimension(this.width, this.height);

		this.tiles = scaledTiles;
		this.width = width;
		this.height = height;

		fireSizeChanged(oldDimension, new Dimension(width, height));
	}

	private int ditherColor(double x, double y) {
		if (Math.floor(x) == x && Math.floor(y) == y) {
			return tiles[(int)y * this.width + (int)x];
		} else {
			final int top = (int)y;
			final int bottom = top < this.height - 1 ? top + 1 : this.height - 1;
			final int left = (int)x;
			final int right = left < this.width - 1 ? left + 1 : this.width - 1;
			// TODO: Faire varier la taille de la palette en fonction de la taille de destination.
			int[] tileSquare = new int[] {
				tiles[top * this.width + left], tiles[top * this.width + right],
				tiles[bottom * this.width + left], tiles[bottom * this.width + right]
			};
			// TODO: Mélanger les couleurs en fonction de la partie décimale de x et y.
		}
		return EMPTY_TILE;
	}

	/**
	 * Déplace le contenu de la couche.
	 *
	 * @param offsetX Décalage horizontal.
	 * @param offsetY Décalage vertical.
	 */
	public void translate(int offsetX, int offsetY) {
		translate(this.tiles, this.width, this.height, offsetX, offsetY);
	}

	public void copyAndTranslate(TileLayer layer, int offsetX, int offsetY) {
		translate(layer.tiles, layer.width, layer.height, offsetX, offsetY);
	}

	private void translate(int[] source, int width, int height, int offsetX, int offsetY) {
		final int[] translatedTiles = new int[this.width * this.height];
		Arrays.fill(translatedTiles, EMPTY_TILE);

		final int sourceX = Math.max(0, -offsetX);
		final int sourceY = Math.max(0, -offsetY);
		final int destinationX = Math.max(0, offsetX);
		final int destinationY = Math.max(0, offsetY);
		final int copyWidth = Math.min(this.width - Math.abs(offsetX), width);
		final int copyHeight = Math.min(this.height - Math.abs(offsetY), height);

		for (int j = 0; j < copyHeight; j++) {
			System.arraycopy(
					source, (j + sourceY) * width + sourceX,
					translatedTiles, (j + destinationY) * this.width + destinationX, copyWidth);
		}

		this.tiles = translatedTiles;
		fireLayerChanged(new Rectangle(0, 0, width, height));
	}

	/**
	 * Vide le contenu de la couche.
	 */
	public void clear() {
		Arrays.fill(this.tiles, EMPTY_TILE);
		fireLayerChanged(new Rectangle(0, 0, width, height));
	}

	/**
	 * Vide le contenu de la forme donnée.
	 *
	 * @param shape Forme à vider.
	 */
	public void clear(Shape shape) {
		setTiles(shape, EMPTY_TILE);
	}

	/**
	 * Vide le contenu à l'emplacement donné.
	 *
	 * @param x Abscisse de la tuile.
	 * @param y Ordonnée de la tuile.
	 */
	public void clear(int x, int y) {
		setTile(x, y, EMPTY_TILE);
	}

	/**
	 * Copie le contenu de la couche donnée sur la couche actuelle. Les points
	 * transparents (-1) ne seront pas copiés.
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

		for (int y = 0; y < minHeight; y++) {
			for (int x = 0; x < minWidth; x++) {
				int tile = layer.tiles[y * layer.width + x];

				if (tile != EMPTY_TILE) {
					if (tile == -2) {
						tile = EMPTY_TILE;
					}

					final int index = y * this.width + x;

					if (this.tiles[index] != tile) {
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
	 * Dessine le layer donné à l'emplacement donné. Contrairement à {@link #merge(fr.rca.mapmaker.model.map.TileLayer)) Les
	 * points transparents sont copiés.
	 *
	 * @param layer Couche à recopier.
	 * @param destination Emplacement où dessiner
	 */
	public void mergeAtPoint(DataLayer layer, Point destination) {
		final int[] copiedTiles = layer.copyData();
		final int copiedWidth = Math.min(layer.getWidth(), this.width - destination.x);
		final int copiedHeight = Math.min(layer.getHeight(), this.height - destination.y);

		for (int y = 0; y < copiedHeight; y++) {
			System.arraycopy(copiedTiles, y * layer.getWidth(), tiles, destination.x + (y + destination.y) * width, copiedWidth);
		}

		fireLayerChanged(new Rectangle(destination.x, destination.y, copiedWidth, copiedHeight));
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

		for (int y = 0; y < minHeight; y++) {
			for (int x = 0; x < minWidth; x++) {
				final int tile = layer.tiles[y * layer.width + x];

				if (tile != EMPTY_TILE) {
					final int index = y * this.width + x;

					if (this.tiles[index] != EMPTY_TILE) {
						this.tiles[index] = EMPTY_TILE;

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

		for (final TileLayer layer : layers) {
			if (layer.getWidth() > width) {
				width = layer.getWidth();
			}

			if (layer.getHeight() > height) {
				height = layer.getHeight();
			}
		}

		for (final TileLayer layer : layers) {
			final int normalizedWidth = (int) (width * layer.getScrollRate().getX());
			final int normalizedHeight = (int) (height * layer.getScrollRate().getY());
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
		for (int index = listeners.size() - 1; index >= 0; index--) {
			listeners.get(index).layerChanged(this, dirtyRectangle);
		}
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
		for (final SizeChangeListener listener : sizeChangeListeners) {
			listener.sizeChanged(this, oldSize, newSize);
		}
	}

	@Override
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	@Override
	public void addPropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(propertyName, listener);
	}

	@Override
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

	@Override
	public void removePropertyChangeListener(String propertyName, PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(propertyName, listener);
	}

	/**
	 * Renvoie une copie des données de cette couche.
	 *
	 * @return Une copie des données.
	 */
	@Override
	public int[] copyData() {
		return this.tiles.clone();
	}

	/**
	 * Restaure les données à partir du tableau donné en argument.
	 *
	 * @param tiles Données à restaurer.
	 * @param source Surface à copier.
	 */
	@Override
	public void restoreData(int[] tiles, Rectangle source) {
		// TODO: Restaurer correctement les changements en cas de redimensionnement. Sauvegarder les changements de taille ?
		this.tiles = tiles.clone();

		if (source == null) {
			source = new Rectangle(0, 0, width, height);
		}

		fireLayerChanged(source);
	}

	/**
	 * Restaure les données à partir du tableau donné en argument et
	 * redimensionne la couche à partir des tailles données.
	 *
	 * @param tiles Données à restaurer.
	 * @param width Nouvelle largeur.
	 * @param height Nouvelle hauteur.
	 */
	@Override
	public void restoreData(int[] tiles, int width, int height) {
		final Dimension oldDimension = new Dimension(this.width, this.height);

		this.tiles = tiles.clone();
		this.width = width;
		this.height = height;

		fireSizeChanged(oldDimension, new Dimension(width, height));
		fireLayerChanged(new Rectangle(0, 0, width, height));
	}

	@Override
	public void restoreData(DataLayer source) {
		final Dimension oldDimension = new Dimension(this.width, this.height);

		this.name = source.getName();
		this.tiles = source.copyData();
		this.width = source.getWidth();
		this.height = source.getHeight();

		if (source instanceof HasLayerPlugin) {
			plugins.clear();
			for (final LayerPlugin plugin : ((HasLayerPlugin) source).getPlugins()) {
				setPlugin(plugin);
			}
		}
		fireSizeChanged(oldDimension, new Dimension(width, height));
		fireLayerChanged(new Rectangle(0, 0, width, height));
	}

	/**
	 * Vérifie si cette couche est visuellement identique à celle donnée en
	 * argument.
	 *
	 * @param other Couche à vérifier
	 * @return <code>true</code> si les deux couches sont identiques,
	 * <code>false</code> sinon.
	 */
	public boolean hasSameData(TileLayer other) {
		return Arrays.equals(tiles, other.tiles);
	}

	/**
	 * Inverse la couche horizontalement.
	 */
	public void flipHorizontally() {
		final int[] mirror = new int[this.tiles.length];

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 1; x <= width; x++) {
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
		final int[] mirror = new int[this.tiles.length];

		int index = 0;
		for (int y = 1; y <= height; y++) {
			for (int x = 0; x < width; x++) {
				mirror[index] = getTile(x, height - y);
				index++;
			}
		}

		this.tiles = mirror;
		fireLayerChanged(new Rectangle(width, height));
	}

	/**
	 * Applique une rotation sur la couche.
	 *
	 * @param angle Angle à appliquer.
	 */
	public void rotate(double angle) {
		rotate(angle, width / 2.0, height / 2.0);
	}

	/**
	 * Applique une rotation sur la couche.
	 *
	 * @param angle Angle à appliquer.
	 * @param pivotX Abscisse du centre de la rotation.
	 * @param pivotY Ordonnée du centre de la rotation.
	 */
	public void rotate(double angle, double pivotX, double pivotY) {
		final int[] rotated = new int[this.tiles.length];

		int index = 0;
		for (int y = 0; y < height; y++) {
			for (int x = 0; x < width; x++) {
				final double originalAngle = Math.atan2(y - pivotY, x - pivotX);
				final double length = Point.distance(x, y, pivotX, pivotY);

				final int refX = (int) (Math.cos(originalAngle - angle) * length + pivotX);
				final int refY = (int) (Math.sin(originalAngle - angle) * length + pivotY);

				rotated[index] = getTile(refX, refY);

				index++;
			}
		}

		this.tiles = rotated;
		fireLayerChanged(new Rectangle(width, height));
	}

	public void rotate90(int times) {
		final int[] rotated = new int[this.tiles.length];
		final int[] source = new int[this.tiles.length];

		System.arraycopy(this.tiles, 0, rotated, 0, this.tiles.length);

		for (int iteration = 0; iteration < times; iteration++) {
			System.arraycopy(rotated, 0, source, 0, this.tiles.length);

			int index = 0;
			for (int y = 0; y < height; y++) {
				for (int x = 0; x < width; x++) {
					rotated[index] = source[y + (width - 1 - x) * width];
					index++;
				}
			}
		}

		this.tiles = rotated;
		fireLayerChanged(new Rectangle(width, height));
	}

	@Override
	public String toString() {
		if (name == null) {
			return super.toString();
		} else {
			return name;
		}
	}
}
