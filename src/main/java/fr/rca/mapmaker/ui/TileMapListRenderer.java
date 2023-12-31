package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.sprite.Direction;
import fr.rca.mapmaker.model.sprite.Instance;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;
import javax.swing.JList;
import javax.swing.ListCellRenderer;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileMapListRenderer extends JComponent implements ListCellRenderer {

	private static final int THUMBNAIL_SIZE = 72;

	private static final int HORIZONTAL_PADDING = 16;
	private static final int VERTICAL_PADDING = 10;

	private static final Dimension size = new Dimension(
			HORIZONTAL_PADDING + THUMBNAIL_SIZE + HORIZONTAL_PADDING,
			VERTICAL_PADDING + THUMBNAIL_SIZE + VERTICAL_PADDING);

	private TileMap map;
	private boolean selected;

	private final Color selectionColor = new Color(180, 198, 221);

	public TileMapListRenderer() {
		setSize(size);
		setPreferredSize(size);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle clipBounds = g.getClipBounds();

		if (selected) {
			g.setColor(selectionColor);
			g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		}

		final Palette palette = map.getPalette();

		final int availableWidth = clipBounds.width - HORIZONTAL_PADDING - HORIZONTAL_PADDING;
		final int availableHeight = clipBounds.height - VERTICAL_PADDING - VERTICAL_PADDING;

		int tileSize;
		if (map.getWidth() < map.getHeight()) {
			tileSize = (int) Math.ceil(availableWidth / (double) map.getWidth());
		} else {
			tileSize = (int) Math.ceil(availableHeight / (double) map.getHeight());
		}

		if (tileSize <= 0) {
			tileSize = 1;
		}
		final int tileCount = availableWidth / tileSize;

		final int tilesX = Math.min(map.getWidth(), tileCount);
		final int tilesY = Math.min(map.getHeight(), availableHeight / tileSize);

		final int width = tilesX * tileSize;
		final int height = tilesY * tileSize;

		final Point origin = new Point((clipBounds.width - width) / 2, (clipBounds.height - height) / 2);

		// Ratio de l'image (1:1 si carré, 2:1 si rectangulaire, etc).
		float ratio = (float) tilesX / tileCount;

		DropShadows.drawShadow(new Rectangle(
				origin.x - (int) (7.0f * ratio), origin.y - (int) (4.0f * ratio),
				width + (int) (14.0f * ratio), height + (int) (14.0f * ratio)), (Graphics2D) g);

		if (map.getBackgroundColor() != null) {
			g.setColor(map.getBackgroundColor());

		} else if (selected) {
			g.setColor(selectionColor);

		} else {
			g.setColor(getBackground());
		}

		g.fillRect(origin.x, origin.y, width, height);

		for (final Layer layer : map.getLayers()) {
			if (layer.isVisible()) {
				final int maxX = Math.min(tilesX, layer.getWidth());
				final int maxY = Math.min(tilesY, layer.getHeight());

				for (int y = 0; y < maxY; y++) {
					for (int x = 0; x < maxX; x++) {
						palette.paintTile(g, layer.getTile(x, y),
								origin.x + x * tileSize,
								origin.y + y * tileSize, tileSize);
					}
				}
			}
		}
		final int paletteTileSize = palette.getTileSize();
		final double zoom = (double)tileSize / paletteTileSize;
		for (Instance instance : map.getSpriteInstances()) {
			final int x = instance.getX();
			final int y = instance.getY();
			if (x <= tilesX * paletteTileSize && y <= tilesY * paletteTileSize) {
				final BufferedImage image = instance.getImage();
				final Point topLeft = new Point(origin.x + (int) (x * zoom), origin.y + (int) (y * zoom));
				final Dimension targetSize = new Dimension(
						Math.min((int) (image.getWidth() * zoom), tilesX * tileSize - (int) (x * zoom)),
						Math.min((int) (image.getHeight() * zoom), tilesY * tileSize - (int) (y * zoom))
				);

				final Point sourceTopLeft = new Point(0, 0);
				final Point sourceBottomRight = new Point((int) (targetSize.width / zoom), (int) (targetSize.height / zoom));

				if (instance.getDirection() == Direction.LEFT) {
					sourceTopLeft.x = image.getWidth();
					sourceBottomRight.x = image.getWidth() - sourceBottomRight.x;
				}
				else if (instance.getDirection() == Direction.UP) {
					sourceTopLeft.y = image.getHeight();
					sourceBottomRight.y = image.getHeight() - sourceBottomRight.y;
				}

				g.drawImage(image, topLeft.x, topLeft.y, topLeft.x + targetSize.width, topLeft.y + targetSize.height, sourceTopLeft.x, sourceTopLeft.y, sourceBottomRight.x, sourceBottomRight.y, null);
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
