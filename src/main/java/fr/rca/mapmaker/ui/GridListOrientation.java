package fr.rca.mapmaker.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum GridListOrientation {
	VERTICAL {
		@Override
		public Dimension getDimension(GridList list) {
			final int padding = list.getPadding();
			final int thumbnailSize = list.getThumbnailSize();
			return new Dimension(padding + thumbnailSize + padding, padding + list.getNumberOfElements() * (thumbnailSize + padding));
		}

		@Override
		public int getX(GridList list, int index) {
			final int padding = list.getPadding();
			return padding;
		}

		@Override
		public int getY(GridList list, int index) {
			final int padding = list.getPadding();
			final int thumbnailSize = list.getThumbnailSize();
			return padding + (thumbnailSize + padding) * index;
		}

		@Override
		public int getStart(Rectangle rectangle) {
			return rectangle.y;
		}

		@Override
		public int getLength(Rectangle rectangle) {
			return rectangle.height;
		}

		@Override
		public int indexOfElementAtPoint(GridList list, Point point) {
			final int padding = list.getPadding();
			final int thumbnailSize = list.getThumbnailSize();
			return point.y / (thumbnailSize + padding);
		}
		
	}, HORIZONTAL {
		@Override
		public Dimension getDimension(GridList list) {
			final int padding = list.getPadding();
			final int thumbnailSize = list.getThumbnailSize();
			return new Dimension(padding + list.getNumberOfElements() * (thumbnailSize + padding), padding + thumbnailSize + padding);
		}

		@Override
		public int getX(GridList list, int index) {
			final int padding = list.getPadding();
			final int thumbnailSize = list.getThumbnailSize();
			return padding + (thumbnailSize + padding) * index;
		}

		@Override
		public int getY(GridList list, int index) {
			final int padding = list.getPadding();
			return padding;
		}

		@Override
		public int getStart(Rectangle rectangle) {
			return rectangle.x;
		}

		@Override
		public int getLength(Rectangle rectangle) {
			return rectangle.width;
		}

		@Override
		public int indexOfElementAtPoint(GridList list, Point point) {
			final int padding = list.getPadding();
			final int thumbnailSize = list.getThumbnailSize();
			return point.x / (thumbnailSize + padding);
		}
		
	};

	public abstract Dimension getDimension(GridList list);

	public abstract int getX(GridList list, int index);

	public abstract int getY(GridList list, int index);

	public abstract int getStart(Rectangle rectangle);

	public abstract int getLength(Rectangle rectangle);
	
	public abstract int indexOfElementAtPoint(GridList list, Point point);
}
