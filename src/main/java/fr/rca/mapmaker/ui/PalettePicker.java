package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.selection.AutoSelectionStyle;
import fr.rca.mapmaker.model.selection.DefaultSelectionStyle;
import fr.rca.mapmaker.model.selection.SelectionStyle;
import java.awt.BorderLayout;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ComponentAdapter;
import java.awt.event.ComponentEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PalettePicker extends JComponent {
	private Palette palette;
	private int columns;
	private double tileSize;
	private Point dragOrigin;
	private final Rectangle selection = new Rectangle(0, 0, 1, 1);
	private final SelectionStyle selectionStyle = new AutoSelectionStyle();

	public PalettePicker() {
		wireEvents();
	}
	
	public Rectangle getSelection() {
		return selection;
	}
	
	public DataLayer getSelectionLayer() {
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

	public Palette getPalette() {
		return palette;
	}

	public void setPalette(Palette palette) {
		this.palette = palette;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Rectangle clipBounds = g.getClipBounds();
		
		// Coordonnées du premier point à afficher.
		final int startX = (int) (clipBounds.x / tileSize);
		final int startY = (int) (clipBounds.y / tileSize);
		
		// Coordonnées du dernier point à afficher.
		final int maxX = Math.min((int) Math.ceil((double) (clipBounds.x + clipBounds.width) / tileSize), columns);
		final int maxY = Math.min((int) Math.ceil((double) (clipBounds.y + clipBounds.height) / tileSize), palette.size() / columns);
		
		// Affichage de la couche
		for(int y = startY; y < maxY; y++) {
			for(int x = startX; x < maxX; x++) {
				palette.paintTile(g, tileAtPoint(new Point(x, y)), (int) (x * tileSize), (int) (y * tileSize), (int) tileSize);
			}
		}
		
		// Affichage de la sélection
		selectionStyle.paintCursor(g, palette, tileSize, selection.x, selection.y, selection.width, selection.height);
	}
	
	private void wireEvents() {
		addComponentListener(new ComponentAdapter() {

			@Override
			public void componentResized(ComponentEvent e) {
				final Dimension size = getSize();
				final double ratio = size.getWidth() / palette.getTileSize();
				
				columns = (int) Math.ceil(ratio);
				tileSize = size.getWidth() / columns;
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
	
	public static void main(String[] args) {
		final AlphaColorPalette palette = new AlphaColorPalette(ColorPalette.getDefaultColorPalette().getColors()) {

			@Override
			public int getTileSize() {
				return 32;
			}
			
		};
		
		final PalettePicker tilePicker = new PalettePicker();
		tilePicker.palette = palette;
		
		final JFrame frame = new JFrame();
		frame.setSize(320, 240);
		frame.getContentPane().setLayout(new BorderLayout());
		frame.getContentPane().add(tilePicker);
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
