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
			return new Dimension(padding + list.getGridWidth() + padding, padding + list.getNumberOfElements() * (list.getGridHeight() + padding));
		}

		@Override
		public int getX(GridList list, int index) {
			final int padding = list.getPadding();
			return padding;
		}

		@Override
		public int getY(GridList list, int index) {
			final int padding = list.getPadding();
			return padding + (list.getGridHeight() + padding) * index;
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
			return point.y / (list.getGridHeight() + padding);
		}

		@Override
		public int getSize(GridList list) {
			return list.getGridHeight();
		}
		
	}, HORIZONTAL {
		@Override
		public Dimension getDimension(GridList list) {
			final int padding = list.getPadding();
			return new Dimension(padding + list.getNumberOfElements() * (list.getGridWidth() + padding), padding + list.getGridHeight() + padding);
		}

		@Override
		public int getX(GridList list, int index) {
			final int padding = list.getPadding();
			return padding + (list.getGridWidth() + padding) * index;
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
			return point.x / (list.getGridWidth() + padding);
		}

		@Override
		public int getSize(GridList list) {
			return list.getWidth();
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
