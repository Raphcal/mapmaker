package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.LayerChangeListener;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class GridList extends JComponent {
	
	private GridListOrientation orientation = GridListOrientation.VERTICAL;
	private final int tileSize = 6;
	private int thumbnailSize = 72;
	private int padding = 4;
	private List<TileMap> maps;
	
	private LayerChangeListener listener;
	private final HashMap<TileLayer, Integer> indexes = new HashMap<TileLayer, Integer>();
	
	private BufferedImage addImage;
	
	private boolean editable = true;
	
	private Integer selection;

	public GridList() {
		this(null);
	}

	public GridList(List<TileMap> maps) {
		setOpaque(true);
		
		try {
			addImage = ImageIO.read(GridList.class.getResourceAsStream("/resources/add.png"));
		} catch (IOException ex) {
			Exceptions.showStackTrace(ex, null);
		}
		
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
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				switch(e.getClickCount()) {
					case 1:
						int element = orientation.indexOfElementAtPoint(GridList.this, e.getPoint());
						if(element >= 0 && element <= GridList.this.maps.size()) {
							selection = element;
						} else {
							selection = null;
						}
						repaint();
						break;
						
					case 2:
						
						break;
				}
			}
		});
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

	public int getPadding() {
		return padding;
	}

	public int getThumbnailSize() {
		return thumbnailSize;
	}

	public void setThumbnailSize(int thumbnailSize) {
		this.thumbnailSize = thumbnailSize;
		updateSize();
	}

	private void updateSize() {
		final Dimension size = orientation.getDimension(this);
		setPreferredSize(size);
		setSize(size);
		
		invalidate();
	}
	
	int getNumberOfElements() {
		int count = maps.size();
		if(editable) {
			count++;
		}
		return count;
	}

	public List<TileMap> getMaps() {
		return maps;
	}
	
	public void setOrientation(GridListOrientation orientation) {
		this.orientation = orientation != null ? orientation : GridListOrientation.VERTICAL;
		updateSize();
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle clipBounds = g.getClipBounds();
		
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		}
		
		final int firstMap = (orientation.getStart(clipBounds) - padding) / (thumbnailSize + padding);
		final int lastMap = Math.min(
			firstMap + orientation.getLength(clipBounds) / (thumbnailSize + padding),
			maps.size() - 1);
		
		for(int index = firstMap; index <= lastMap; index++) {
			paintMap(index, g);
		}
		
		if(selection != null && selection == maps.size()) {
			g.setColor(Color.BLUE);
			g.fillRect(orientation.getX(this, maps.size()) - padding, orientation.getY(this, maps.size()) - padding, thumbnailSize + padding + padding, thumbnailSize + padding + padding);
		}
		
		g.drawRect(orientation.getX(this, maps.size()), orientation.getY(this, maps.size()), thumbnailSize, thumbnailSize);
		g.drawImage(addImage, 
			orientation.getX(this, maps.size()) + thumbnailSize / 2 - addImage.getWidth() / 2, 
			orientation.getY(this, maps.size()) + thumbnailSize / 2 - addImage.getHeight()/ 2, null);
		
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
		
		if(selection != null && selection == index) {
			g.setColor(Color.BLUE);
			g.fillRect(originX - padding, originY - padding, thumbnailSize + padding + padding, thumbnailSize + padding + padding);
		}
		
		if(map.getBackgroundColor() != null) {
			g.setColor(map.getBackgroundColor());
			g.fillRect(originX, originY, thumbnailSize, thumbnailSize);
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
