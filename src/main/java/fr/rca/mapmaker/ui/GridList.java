package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.LayerChangeListener;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GridList extends JComponent {
	
	public enum Orientation {
		VERTICAL {
			@Override
			public Dimension getDimension(GridList list) {
				return new Dimension(
					list.padding + list.thumbnailSize + list.padding, 
					list.padding + list.maps.size() * (list.thumbnailSize + list.padding));
			}

			@Override
			public int getX(GridList list, int index) {
				return list.padding;
			}

			@Override
			public int getY(GridList list, int index) {
				return list.padding + (list.thumbnailSize + list.padding) * index;
			}

			@Override
			public int getStart(Rectangle rectangle) {
				return rectangle.y;
			}

			@Override
			public int getLength(Rectangle rectangle) {
				return rectangle.height;
			}
		}, 
		HORIZONTAL {
			@Override
			public Dimension getDimension(GridList list) {
				return new Dimension(
					list.padding + list.maps.size() * (list.thumbnailSize + list.padding),
					list.padding + list.thumbnailSize + list.padding);
			}
			
			@Override
			public int getX(GridList list, int index) {
				return list.padding + (list.thumbnailSize + list.padding) * index;
			}

			@Override
			public int getY(GridList list, int index) {
				return list.padding;
			}
			
			@Override
			public int getStart(Rectangle rectangle) {
				return rectangle.x;
			}

			@Override
			public int getLength(Rectangle rectangle) {
				return rectangle.width;
			}
		};
		
		public abstract Dimension getDimension(GridList list);
		public abstract int getX(GridList list, int index);
		public abstract int getY(GridList list, int index);
		public abstract int getStart(Rectangle rectangle);
		public abstract int getLength(Rectangle rectangle);
	}
	
	private Orientation orientation = Orientation.VERTICAL;
	private final int tileSize = 6;
	private final int thumbnailSize = 72;
	private final int padding = 4;
	private List<TileMap> maps;
	
	private LayerChangeListener listener;
	private HashMap<TileLayer, Integer> indexes = new HashMap<TileLayer, Integer>();

	public GridList() {
		this(null);
	}

	public GridList(List<TileMap> maps) {
		setOpaque(true);
		
		listener = new LayerChangeListener() {
			@Override
			public void layerChanged(TileLayer layer, Rectangle dirtyRectangle) {
				final Integer index = indexes.get(layer);
				
				if(index != null) {
					repaintMap(index);
				}
			}
		};
		
		setMaps(maps);
	}

	public final void setMaps(List<TileMap> maps) {
		if(maps == null) {
			maps = Collections.emptyList();
		}
		
		if(this.maps != null) {
			for(final TileLayer layer : indexes.keySet()) {
				layer.removeLayerChangeListener(listener);
			}
			indexes.clear();
		}
		
		this.maps = maps;
		
		for(int index = 0; index < maps.size(); index++) {
			final TileMap map = maps.get(index);
			
			if(map != null) {
				for(final Layer layer : map.getLayers()) {
					if(layer instanceof TileLayer) {
						final TileLayer tileLayer = (TileLayer) layer;
						tileLayer.addLayerChangeListener(listener);
						indexes.put(tileLayer, index);
					}
				}
			}
		}

		updateSize();
		
		if(getParent() != null) {
			getParent().validate();
		}
		
		repaint();
	}

	private void updateSize() {
		final Dimension size = orientation.getDimension(this);
		setPreferredSize(size);
		setSize(size);
		
		invalidate();
	}

	public List<TileMap> getMaps() {
		return maps;
	}

	public void setOrientation(Orientation orientation) {
		this.orientation = orientation;
		updateSize();
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle clipBounds = g.getClipBounds();
		
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		}
		
		final int firstMap = (orientation.getStart(clipBounds) - padding) / (thumbnailSize + padding);
		final int lastMap = firstMap + orientation.getLength(clipBounds) / (thumbnailSize + padding);
		
		for(int index = firstMap; index <= lastMap; index++) {
			paintMap(index, g);
		}
		
		g.dispose();
	}
	
	private void paintMap(int index, Graphics g) {
		if(index < 0 || index >= maps.size()) {
			return;
		}
		
		final TileMap map = maps.get(index);
		final Palette palette = map.getPalette();
		
		final int size = thumbnailSize / tileSize;
		final int originX = orientation.getX(this, index);
		final int originY = orientation.getY(this, index);
		
		if(map.getBackgroundColor() != null) {
			g.setColor(map.getBackgroundColor());
			g.fillRect(padding, originY, thumbnailSize, thumbnailSize);
		}
		
		for(final Layer layer : map.getLayers()) {
			if(layer.isVisible()) {
				final int maxX = Math.min(size, layer.getWidth());
				final int maxY = Math.min(size, layer.getHeight());
				
				for(int y = 0; y < maxY; y++) {
					for(int x = 0; x < maxX; x++) {
						palette.paintTile(g, layer.getTile(x, y), originX + x * tileSize, originY + y * tileSize, tileSize);
					}
				}
			}
		}
	}
	
	private void repaintMap(int index) {
		repaint(new Rectangle(0, padding + index * (thumbnailSize + padding), thumbnailSize + padding, thumbnailSize + padding));
	}
}
