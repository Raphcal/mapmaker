package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileMapListRenderer extends JComponent implements ListCellRenderer {
	
	private static final int TILE_SIZE = 6;
	private static final int THUMBNAIL_SIZE = 72;
	private static final int TILE_COUNT = THUMBNAIL_SIZE / TILE_SIZE;
	
	private static final int HORIZONTAL_PADDING = 16;
	private static final int VERTICAL_PADDING = 10;
	
	private TileMap map;
	private boolean selected;
	
	private final Color selectionColor = new Color(180, 198, 221);

	public TileMapListRenderer() {
		final Dimension size = new Dimension(HORIZONTAL_PADDING + THUMBNAIL_SIZE + HORIZONTAL_PADDING,
				VERTICAL_PADDING + THUMBNAIL_SIZE + VERTICAL_PADDING);
		
		setSize(size);
		setPreferredSize(size);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle clipBounds = g.getClipBounds();
		
		if(selected) {
			g.setColor(selectionColor);
			g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		}
		
		final Palette palette = map.getPalette();

		final int tilesX = Math.min(map.getWidth(), TILE_COUNT);
		final int tilesY = Math.min(map.getHeight(), TILE_COUNT);
		
		final int width = tilesX * TILE_SIZE;
		final int height = tilesY * TILE_SIZE;
		
		final Point origin = new Point((clipBounds.width - width) / 2, (clipBounds.height - height) / 2);
		
		float ratio = (float)tilesX / TILE_COUNT;
		
		DropShadows.drawShadow(new Rectangle(
				origin.x - (int) (7.0f * ratio), origin.y - (int) (4.0f * ratio),
				width + (int) (14.0f * ratio), height + (int) (14.0f * ratio)), (Graphics2D) g);
		
		if(map.getBackgroundColor() != null) {
			g.setColor(map.getBackgroundColor());
		
		} else if(selected) {
			g.setColor(selectionColor);
		
		} else {
			g.setColor(getBackground());
		}
			
		g.fillRect(origin.x, origin.y, width, height);
		
		for(final Layer layer : map.getLayers()) {
			if(layer.isVisible()) {
				final int maxX = Math.min(tilesX, layer.getWidth());
				final int maxY = Math.min(tilesY, layer.getHeight());
				
				for(int y = 0; y < maxY; y++) {
					for (int x = 0; x < maxX; x++) {
						palette.paintTile(g, layer.getTile(x, y),
							origin.x + x * TILE_SIZE,
							origin.y + y * TILE_SIZE, TILE_SIZE);
					}
				}
			}
		}
		
		g.dispose();
	}
	
	@Override
	public Component getListCellRendererComponent(JList list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
		this.selected = isSelected;
		this.map = (TileMap) value;
		
		return this;
	}
	
	
}
