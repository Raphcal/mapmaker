package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.HasSelectionListeners;
import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.LayerChangeListener;
import fr.rca.mapmaker.model.SelectionListener;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.selection.DefaultSelectionStyle;
import fr.rca.mapmaker.model.selection.SelectionStyle;
import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Composite;
import java.awt.Container;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.util.List;
import javax.swing.JViewport;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Grid extends AbstractLayerPainter {

	/**
	 * Mode de composition pour les couches supérieures au focus.
	 */
	private static final AlphaComposite ALPHA_COMPOSITE = AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 0.33f);
	/**
	 * Couleur du voile recouvrant les couches inférieures au focus.
	 */
	private static final Color DARK_LAYER_COLOR = new Color(0.0f, 0.0f, 0.0f, 0.66f);

	/**
	 * Données à afficher
	 */
	private TileMap tileMap;
	private final LayerChangeListener layerChangeListener;
	private final SelectionListener selectionListener;
	private final SizeChangeListener sizeChangeListener;

	private SelectionStyle selectionStyle;

	private final TileLayer overlay;

	/**
	 * Gestion du scrolling.
	 */
	private JViewport viewport;

	/**
	 * Taille des carreaux (non obligatoire).
	 */
	private Integer customTileSize;

	/**
	 * Indice de la couche mise en focus.
	 */
	private boolean focus;
	private int activeLayer;

	/**
	 * Niveau de zoom des carreaux.
	 */
	private double zoom = 1.0;

	/**
	 * Contrainte de taille maximum.
	 */
	private Dimension constraint;

	public Grid() {
		setOpaque(true);

		selectionStyle = new DefaultSelectionStyle();

		layerChangeListener = new LayerChangeListener() {
			@Override
			public void layerChanged(TileLayer layer, Rectangle dirtyRectangle) {
				final int tileSize = getTileSize();
				final Point origin = getLayerOrigin(layer);

				// Rafraîchissement de la vue
				repaintFromParent(origin.x + dirtyRectangle.x * tileSize, origin.y + dirtyRectangle.y * tileSize,
						dirtyRectangle.width * tileSize, dirtyRectangle.height * tileSize);
			}
		};

		selectionListener = new SelectionListener() {
			@Override
			public void selectionChanged(Point oldSelection, Point newSelection) {
				final int tileSize = getTileSize();

				if (oldSelection != null) {
					repaintFromParent(oldSelection.x * tileSize, oldSelection.y * tileSize, tileSize, tileSize);
				}

				if (newSelection != null) {
					repaintFromParent(newSelection.x * tileSize, newSelection.y * tileSize, tileSize, tileSize);
				}
			}
		};

		sizeChangeListener = new SizeChangeListener() {

			@Override
			public void sizeChanged(Object source, Dimension oldSize, Dimension newSize) {
				updateSize();
			}
		};

		overlay = new TileLayer(0, 0);
		overlay.addLayerChangeListener(layerChangeListener);

		setTileMap(new TileMap(new TileLayer(32, 32), AlphaColorPalette.getDefaultColorPalette()));
	}

	public TileMap getTileMap() {
		return tileMap;
	}

	public int getTileMapWidth() {
		return tileMap != null ? tileMap.getWidth() : 0;
	}

	public int getTileMapHeight() {
		return tileMap != null ? tileMap.getHeight() : 0;
	}

	public void setTileMap(TileMap tileMap) {
		if (this.tileMap != null) {
			// Suppression des listeners
			this.tileMap.removeLayerChangeListener(layerChangeListener);

			if (this.tileMap instanceof HasSelectionListeners) {
				((HasSelectionListeners) this.tileMap).removeSelectionListener(selectionListener);
			}

			if (this.tileMap instanceof HasSizeChangeListeners) {
				((HasSizeChangeListeners) this.tileMap).removeSizeChangeListener(sizeChangeListener);
			}
		}

		this.tileMap = tileMap;

		// Ajout des listeners
		if (tileMap != null) {
			tileMap.addLayerChangeListener(layerChangeListener);
		}

		if (tileMap instanceof HasSelectionListeners) {
			((HasSelectionListeners) tileMap).addSelectionListener(selectionListener);
		}

		if (tileMap instanceof HasSizeChangeListeners) {
			((HasSizeChangeListeners) tileMap).addSizeChangeListener(sizeChangeListener);
		}

		updateSize();
		repaint();
	}

	public void setViewport(JViewport viewport) {
		this.viewport = viewport;
	}

	public void setSelectionStyle(SelectionStyle selectionStyle) {
		this.selectionStyle = selectionStyle;
	}

	public TileLayer getOverlay() {
		return overlay;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
		updateSize();
		repaint();
	}

	public void setZoomAsInteger(int zoom) {
		setZoom((double) zoom / 100.0);
	}

	public int getZoomAsInteger() {
		return (int) (zoom * 100.0);
	}

	public double getActualZoom() {
		return (double) getTileSize() / (double) tileMap.getPalette().getTileSize();
	}

	private void drawBackground(Graphics g, Rectangle clipBounds, Dimension size) {
		// Choix de la couleur de fond
		if (tileMap != null && tileMap.getBackgroundColor() != null) {
			g.setColor(tileMap.getBackgroundColor());
		} else {
			((Graphics2D) g).setPaint(Paints.TRANSPARENT_PAINT);
		}

		// Affichage
		g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);

		final Graphics2D graphics2d = (Graphics2D) g;
		final Rectangle clipBounds = g.getClipBounds();
		final Composite orignalComposite = graphics2d.getComposite();

		final Dimension size = getPreferredSize();

		// Affichage du fond
		if (isOpaque()) {
			drawBackground(g, clipBounds, size);
		}

		if (tileMap == null) {
			return;
		}

		// Liste des calques
		final List<Layer> layers = tileMap.getLayers();

		// Préparation du focus
		final int darkLayerIndex;
		final int transparentIndex;

		if (focus) {
			darkLayerIndex = activeLayer;
			transparentIndex = activeLayer + 1;

		} else {
			darkLayerIndex = layers.size() + 1;
			transparentIndex = layers.size() + 1;
		}

		// Récupération de la taille des tuiles.
		final int tileSize = getTileSize();

		final Point viewPoint;

		if (viewport != null) {
			viewPoint = viewport.getViewPosition();
		} else {
			viewPoint = new Point(0, 0);
		}

		final Palette palette = tileMap.getPalette();

		// Affichage des couches
		for (int index = 0; index <= layers.size(); index++) {
			// Si une couche est mise en avant (focus) affichage grisé
			// des couches inférieures.
			if (index == darkLayerIndex) {
				g.setColor(DARK_LAYER_COLOR);
				g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
			}

			final Layer layer;

			if (index == layers.size()) {
				layer = overlay;
			} else {
				layer = layers.get(index);
			}

			// Si une couche est mise en avant (focus) affichage par
			// transparence des couches supérieures.
			if (index == transparentIndex || index == layers.size()) {
				graphics2d.setComposite(ALPHA_COMPOSITE);
			}

			if (layer.isVisible()) {
				if (tileSize > 0) {
					paintLayer(layer, palette, clipBounds, tileSize, viewPoint, g);
				} else {
					paintLayer(layer, palette, clipBounds, getZoomedTileSize(), viewPoint, g);
				}
			}
		}

		// Restauration du mode d'affichage
		graphics2d.setComposite(orignalComposite);

		// Contours
//		g.setColor(Color.BLACK);
//		g.drawRect(-1, -1, size.width, size.height);
		// Sélection
		paintSelection(tileSize, g);

		g.dispose();
	}

	public void repaint(Point p) {
		// TODO: prendre en compte le scrolling
		final int tileSize = getTileSize();
		repaintFromParent(p.x * tileSize, p.y * tileSize, tileSize, tileSize);
	}

	protected void repaintFromParent(int x, int y, int width, int height) {
		final Container parent = getParent();
		if (parent != null && parent.getLayout() instanceof LayerLayout && ((LayerLayout) parent.getLayout()).getDisposition() == LayerLayout.Disposition.TOP_LEFT) {
			// TODO: Gérer la disposition CENTER
			parent.repaint(x, y, width, height);
		} else {
			repaint(x, y, width, height);
		}
	}

	protected void paintSelection(int tileSize, Graphics g) {
		if (tileMap instanceof HasSelectionListeners) {
			final Point selectedPoint = ((HasSelectionListeners) tileMap).getSelection();

			if (selectedPoint != null) {
				final int x = selectedPoint.x * tileSize;
				final int y = selectedPoint.y * tileSize;
				final Point origin = getLayerOrigin(tileMap.getLayers().get(activeLayer));

				selectionStyle.paintCursor(g, tileMap.getPalette(), origin.x + x, origin.y + y, tileSize, tileSize);
			}
		}
	}

	private Point getLayerOrigin(final Layer layer) {
		final Point origin = new Point();

		if (viewport != null) {
			final Point viewPoint = viewport.getViewPosition();

			// Emplacement du point supérieur gauche de la couche.
			origin.x = (int) (viewPoint.x * (1 - layer.getScrollRate().getX()));
			origin.y = (int) (viewPoint.y * (1 - layer.getScrollRate().getY()));

		} else {
			origin.x = 0;
			origin.y = 0;
		}

		return origin;
	}

	protected void updateSize() {
		final int width;
		final int height;

		if (tileMap != null) {
			width = tileMap.getWidth();
			height = tileMap.getHeight();

		} else {
			width = 5;
			height = 5;
		}

		firePropertyChange("tileMapWidth", width, overlay.getWidth());
		firePropertyChange("tileMapHeight", height, overlay.getHeight());
		overlay.resize(width, height);

		final double tileSize = getZoomedTileSize();
		final Dimension dimension = new Dimension((int) (width * tileSize), (int) (height * tileSize));
		if (constraint != null) {
			dimension.width = Math.min(dimension.width, constraint.width);
			dimension.height = Math.min(dimension.height, constraint.height);
		}

		setPreferredSize(dimension);

		revalidate();
	}

	public void refresh() {
		if (tileMap != null) {
			tileMap.refresh();
		}
		updateSize();
		repaint();
	}

	public void setCustomTileSize(Integer tileSize) {
		this.customTileSize = tileSize;

		updateSize();
	}

	public Integer getCustomTileSize() {
		return customTileSize;
	}

	public double getZoomedTileSize() {
		final int baseSize;

		if (this.customTileSize != null) {
			baseSize = customTileSize;

		} else if (tileMap != null && tileMap.getPalette() != null) {
			baseSize = tileMap.getPalette().getTileSize();

		} else {
			baseSize = 1;
		}

		return zoom * baseSize;
	}

	public int getTileSize() {
		return (int) getZoomedTileSize();
	}

	/**
	 * Récupère les coordonnées du point affiché à l'emplacement donné.
	 *
	 * @param point Point dans la grille.
	 * @return Le point correspondant sur la couche en focus.
	 */
	public Point getLayerLocation(Point point) {
		return getLayerLocation(point.x, point.y);
	}

	/**
	 * Récupère les coordonnées du point affiché à l'emplacement donné.
	 *
	 * @param x Abscisse relatif à la grille.
	 * @param y Ordonnée relatif à la grille.
	 * @return Le point correspondant sur la couche en focus.
	 */
	public Point getLayerLocation(int x, int y) {
		final int tileSize = getTileSize();

		if (tileMap != null && viewport != null
				&& activeLayer >= 0 && activeLayer < tileMap.getSize()) {

			final Layer layer = tileMap.getLayers().get(activeLayer);
			final Point viewPoint = viewport.getViewPosition();

			// Décalage du point pour prendre en compte le parallaxe.
			x -= (int) (viewPoint.x * (1 - layer.getScrollRate().getX()));
			y -= (int) (viewPoint.y * (1 - layer.getScrollRate().getY()));
		}

		return new Point(x / tileSize, y / tileSize);
	}

	public boolean isFocusVisible() {
		return focus;
	}

	public void setFocusVisible(boolean visible) {
		focus = visible;
	}

	public void setActiveLayer(TileLayer layer) {
		setActiveLayer(tileMap.getLayers().indexOf(layer));
	}

	public void setActiveLayer(int index) {
		if (index < 0) {
			index = 0;

		} else if (index >= tileMap.getLayers().size()) {
			index = tileMap.getLayers().size() - 1;
		}

		this.activeLayer = index;
	}

	public int getActiveLayerIndex() {
		return activeLayer;
	}

	public Layer getActiveLayer() {
		if (activeLayer >= 0 && activeLayer < tileMap.getSize()) {
			return tileMap.getLayers().get(activeLayer);
		} else {
			return null;
		}
	}

	public Dimension getConstraint() {
		return constraint;
	}

	public void setConstraint(Dimension constraint) {
		this.constraint = constraint;
	}

}
