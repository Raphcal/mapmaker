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
	
	private int tileSize = 6;
	private int thumbnailSize = 72;
	private int padding = 4;
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
				
				if(index != null)
					repaintMap(index);
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

		final Dimension size = new Dimension(padding + thumbnailSize + padding, padding + maps.size() * (thumbnailSize + padding));
		setPreferredSize(size);
		setSize(size);
		
		invalidate();
		
		if(getParent() != null) {
			getParent().validate();
		}
		
		repaint();
	}

	public List<TileMap> getMaps() {
		return maps;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		
		final Rectangle clipBounds = g.getClipBounds();
		
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		}
		
		final int topMap = (clipBounds.y - padding) / (thumbnailSize + padding);
		final int bottomMap = topMap + clipBounds.height / (thumbnailSize + padding);
		
		for(int index = topMap; index <= bottomMap; index++)
			paintMap(index, g);
			
		
		g.dispose();
	}
	
	private void paintMap(int index, Graphics g) {
		
		if(index < 0 || index >= maps.size())
			return;
		
		final TileMap map = maps.get(index);
		final Palette palette = map.getPalette();
		
		final int size = thumbnailSize / tileSize;
		final int originY = padding + index * (thumbnailSize + padding);
		
		if(map.getBackgroundColor() != null) {
			g.setColor(map.getBackgroundColor());
			g.fillRect(padding, originY, thumbnailSize, thumbnailSize);
		}
		
		for(final Layer layer : map.getLayers()) {
			if(layer.isVisible()) {
				final int maxX = Math.min(size, layer.getWidth());
				final int maxY = Math.min(size, layer.getHeight());
				
				for(int y = 0; y < maxY; y++)
					for(int x = 0; x < maxX; x++)
						palette.paintTile(g, layer.getTile(x, y), padding + x * tileSize, originY + y * tileSize, tileSize);
			}
		}
	}
	
	private void repaintMap(int index) {
		
		repaint(new Rectangle(0, padding + index * (thumbnailSize + padding), thumbnailSize + padding, thumbnailSize + padding));
	}
}
