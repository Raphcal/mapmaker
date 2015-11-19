package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.HasSelectionListeners;
import fr.rca.mapmaker.model.Optional;
import fr.rca.mapmaker.model.SelectionListener;
import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.selection.AutoSelectionStyle;
import fr.rca.mapmaker.model.selection.SelectionStyle;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PalettePicker extends JComponent implements HasSelectionListeners {
	
	private final Palette palette = Optional.newInstance(Palette.class);
	private final Rectangle selection = new Rectangle(0, 0, 1, 1);
	private final SelectionStyle selectionStyle = new AutoSelectionStyle();
	private final List<SelectionListener> selectionListeners = new ArrayList<SelectionListener>();
	
	private Point dragOrigin;
	private int columns = 1;
	private int tileSize = 1;
	
	public PalettePicker() {
		setOpaque(true);
		wireEvents();
	}
	
	public int getSelectedTile() {
		return selection.x + selection.y * columns;
	}
	
	public DataLayer getSelectionAsLayer() {
		final int[] tiles = new int[selection.width * selection.height];
		
		int index = 0;
		for(int y = 0; y < selection.height; y++) {
			for(int x = 0; x < selection.width; x++) {
				tiles[index++] = x + selection.x + (y + selection.y) * columns;
			}
		}
		
		final TileLayer layer = new TileLayer();
		layer.restoreData(tiles, selection.width, selection.height);
		return layer;
	}

	public void setPalette(Palette palette) {
		Optional.set(this.palette, palette);
		updateSize();
		repaint();
	}

	public Palette getPalette() {
		return Optional.get(this.palette);
	}

	@Override
	public void addSelectionListener(SelectionListener listener) {
		selectionListeners.add(listener);
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		selectionListeners.remove(listener);
	}

	@Override
	public Point getSelection() {
		return new Point(selection.x, selection.y);
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle clipBounds = g.getClipBounds();
		
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		}
		
		if (tileSize == 0) {
			return;
		}
		
		// Coordonnées du premier point à afficher.
		final int startX = clipBounds.x / tileSize;
		final int startY = clipBounds.y / tileSize;
		
		// Coordonnées du dernier point à afficher.
		final int maxX = Math.min((int) Math.ceil((double) (clipBounds.x + clipBounds.width) / tileSize), columns);
		final int maxY = Math.min((int) Math.ceil((double) (clipBounds.y + clipBounds.height) / tileSize), palette.size() / columns);
		
		// Affichage de la couche
		for(int y = startY; y < maxY; y++) {
			for(int x = startX; x < maxX; x++) {
				((Graphics2D)g).setPaint(Paints.TRANSPARENT_PAINT);
				g.fillRect(x * tileSize, y * tileSize, tileSize, tileSize);
				palette.paintTile(g, tileAtPoint(new Point(x, y)), x * tileSize, y * tileSize, tileSize);
			}
		}
		
		// Affichage de la sélection
		selectionStyle.paintCursor(g, palette, tileSize, selection.x, selection.y, selection.width, selection.height);
	}
	
	protected void fireSelectionChanged() {
		for(final SelectionListener listener : selectionListeners) {
			listener.selectionChanged(null, getSelection());
		}
	}
	
	public void updateSize() {
		final int paletteTileSize = Math.max(palette.getTileSize(), 1);
		final Dimension size = getSize();
		final double ratio = size.getWidth() / paletteTileSize;

		columns = Math.max((int) Math.ceil(ratio), 1);
		tileSize = (int) (size.getWidth() / columns);
		final int rows = rowCount();

		if(selection.x + selection.width > columns) {
			selection.width = Math.max(columns - selection.x, 1);
			selection.x = Math.min(selection.x, columns - selection.width);
		}

		if(selection.y + selection.height > rows) {
			selection.height = Math.max(rows - selection.y, 1);
			selection.y = Math.min(selection.y, rows - selection.height);
		}

		palette.setSelectedTile(selection.x + selection.y * columns);

		setPreferredSize(new Dimension(tileSize * columns, tileSize * rows));
		invalidate();
	}
	
	private void wireEvents() {
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				updateSize();
			}

		});
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				final Point tileLocation = tileLocationAtPoint(e.getPoint());
				selection.x = tileLocation.x;
				selection.y = tileLocation.y;
				selection.width = 1;
				selection.height = 1;
				palette.setSelectedTile(selection.x + selection.y * columns);
				
				repaint();
				fireSelectionChanged();
			}
			
			@Override
			public void mouseReleased(MouseEvent e) {
				dragOrigin = null;
			}
			
		});
		
		addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mousePressed(MouseEvent e) {
				if(dragOrigin == null) {
					dragOrigin = tileLocationAtPoint(e.getPoint());
				}
			}
			
			@Override
			public void mouseDragged(MouseEvent e) {
				final Point tileLocation = tileLocationAtPoint(e.getPoint());
				if(dragOrigin == null) {
					dragOrigin = tileLocation;
				}
				
				if(e.getButton() == MouseEvent.BUTTON1) {
					selection.x = Math.min(dragOrigin.x, tileLocation.x);
					selection.y = Math.min(dragOrigin.y, tileLocation.y);
					selection.width = Math.abs(tileLocation.x - dragOrigin.x) + 1;
					selection.height = Math.abs(tileLocation.y - dragOrigin.y) + 1;
				} else {
					selection.x = tileLocation.x;
					selection.y = tileLocation.y;
					selection.width = 1;
					selection.height = 1;
				}
				
				palette.setSelectedTile(selection.x + selection.y * columns);
				
				repaint();
				fireSelectionChanged();
			}

		});
	}
	
	private int tileAtPoint(Point point) {
		return point.x + point.y * columns;
	}
	
	private Point tileLocationAtPoint(Point point) {
		return new Point(Math.min((int) (point.getX() / tileSize), columns - 1), Math.min((int) (point.getY() / tileSize), rowCount() - 1));
	}
	
	private int rowCount() {
		return palette.size() / columns;
	}
	
}
