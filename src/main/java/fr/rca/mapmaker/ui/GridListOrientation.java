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
			final int width = list.getGridSize();
			final int height = width;
			return new Dimension(padding + width + padding, padding + list.getNumberOfElements() * (height + padding));
		}

		@Override
		public int getX(GridList list, int index) {
			final int padding = list.getPadding();
			return padding;
		}

		@Override
		public int getY(GridList list, int index) {
			final int padding = list.getPadding();
			final int height = list.getGridSize();
			return padding + (height + padding) * index;
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
			final int height = list.getGridSize();
			return point.y / (height + padding);
		}

		@Override
		public int getSize(GridList list) {
			final int height = list.getGridSize();
			return height;
		}
		
	}, HORIZONTAL {
		@Override
		public Dimension getDimension(GridList list) {
			final int padding = list.getPadding();
			final int width = list.getGridSize();
			final int height = width;
			return new Dimension(padding + list.getNumberOfElements() * (width + padding), padding + height + padding);
		}

		@Override
		public int getX(GridList list, int index) {
			final int padding = list.getPadding();
			final int width = list.getGridSize();
			return padding + (width + padding) * index;
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
			final int width = list.getGridSize();
			return point.x / (width + padding);
		}

		@Override
		public int getSize(GridList list) {
			final int width = list.getGridSize();
			return width;
		}
		
	};

	public abstract Dimension getDimension(GridList list);

	public abstract int getX(GridList list, int index);

	public abstract int getY(GridList list, int index);

	public abstract int getStart(Rectangle rectangle);

	public abstract int getLength(Rectangle rectangle);
	
	public abstract int indexOfElementAtPoint(GridList list, Point point);
	
	public abstract int getSize(GridList list);
}
