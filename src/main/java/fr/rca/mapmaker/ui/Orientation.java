package fr.rca.mapmaker.ui;

import java.awt.Dimension;
import java.awt.Point;
import java.awt.Rectangle;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum Orientation {
	VERTICAL {
		@Override
		public Dimension getDimension(Orientable orientable) {
			final int padding = orientable.getPadding();
			final int width = orientable.getGridSize();
			final int height = width;
			return new Dimension(padding + width + padding, padding + orientable.getNumberOfElements() * (height + padding));
		}

		@Override
		public int getX(Orientable orientable, int index) {
			final int padding = orientable.getPadding();
			return padding;
		}

		@Override
		public int getY(Orientable orientable, int index) {
			final int padding = orientable.getPadding();
			final int height = orientable.getGridSize();
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
		public int indexOfElementAtPoint(Orientable orientable, Point point) {
			final int padding = orientable.getPadding();
			final int width = orientable.getGridSize();
			final int height = orientable.getGridSize();
			
			if(point.getX() <= padding || point.getX() >= padding + width) {
				return -1;
			}
			
			return point.y / (height + padding);
		}

		@Override
		public int getSize(Orientable orientable) {
			final int height = orientable.getGridSize();
			return height;
		}
		
	}, HORIZONTAL {
		@Override
		public Dimension getDimension(Orientable orientable) {
			final int padding = orientable.getPadding();
			final int width = orientable.getGridSize();
			final int height = width;
			return new Dimension(padding + orientable.getNumberOfElements() * (width + padding), padding + height + padding);
		}

		@Override
		public int getX(Orientable orientable, int index) {
			final int padding = orientable.getPadding();
			final int width = orientable.getGridSize();
			return padding + (width + padding) * index;
		}

		@Override
		public int getY(Orientable orientable, int index) {
			final int padding = orientable.getPadding();
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
		public int indexOfElementAtPoint(Orientable orientable, Point point) {
			final int padding = orientable.getPadding();
			final int width = orientable.getGridSize();
			final int height = orientable.getGridSize();
			
			if(point.getY() <= padding || point.getY() >= padding + height) {
				return -1;
			}
			
			return point.x / (width + padding);
		}

		@Override
		public int getSize(Orientable orientable) {
			final int width = orientable.getGridSize();
			return width;
		}
		
	};

	public abstract Dimension getDimension(Orientable orientable);

	public abstract int getX(Orientable orientable, int index);

	public abstract int getY(Orientable orientable, int index);

	public abstract int getStart(Rectangle rectangle);

	public abstract int getLength(Rectangle rectangle);
	
	public abstract int indexOfElementAtPoint(Orientable orientable, Point point);
	
	public abstract int getSize(Orientable orientable);
}
