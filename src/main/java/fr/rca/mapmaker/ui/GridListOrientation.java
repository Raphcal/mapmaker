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
			return new Dimension(list.padding + list.thumbnailSize + list.padding, list.padding + list.getNumberOfElements() * (list.thumbnailSize + list.padding));
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

		@Override
		public int indexOfElementAtPoint(GridList list, Point point) {
			return point.y / (list.thumbnailSize + list.padding);
		}
		
	}, HORIZONTAL {
		@Override
		public Dimension getDimension(GridList list) {
			return new Dimension(list.padding + list.getNumberOfElements() * (list.thumbnailSize + list.padding), list.padding + list.thumbnailSize + list.padding);
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

		@Override
		public int indexOfElementAtPoint(GridList list, Point point) {
			return point.x / (list.thumbnailSize + list.padding);
		}
		
	};

	public abstract Dimension getDimension(GridList list);

	public abstract int getX(GridList list, int index);

	public abstract int getY(GridList list, int index);

	public abstract int getStart(Rectangle rectangle);

	public abstract int getLength(Rectangle rectangle);
	
	public abstract int indexOfElementAtPoint(GridList list, Point point);
}
