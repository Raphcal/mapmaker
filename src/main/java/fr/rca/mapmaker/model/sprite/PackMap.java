package fr.rca.mapmaker.model.sprite;

import java.awt.Point;

/**
 * Table qui agence ses objets pour tenir dans la surface donnée.
 * <p/>
 * Les objets doivent-être triés dans l'ordre du plus haut au moins haut pour
 * que les résultats soient meilleurs.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PackMap {
	private static final int HORIZONTAL = 0;
	private static final int VERTICAL = 1;

	public static final int SIDE_LEFT = 0;
	public static final int SIDE_RIGHT = 1;
	public static final int SIDE_TOP = 2;
	public static final int SIDE_BOTTOM = 3;
	
	private static final int WIDTH = 0;
	private static final int HEIGHT = 1;
	
	private Entry topLeft;
	
	private int width;
	private int height;

	public PackMap(int width, int height) {
		this.width = width;
		this.height = height;
		this.topLeft = new Entry(width, height);
	}
	
	public Entry get(int column, int row) {
		Entry cell = topLeft;
		for(int i = 0; i < column; i++) {
			cell = cell.sides[SIDE_RIGHT];
		}
		for(int i = 0; i < row; i++) {
			cell = cell.sides[SIDE_BOTTOM];
		}
		return cell;
	}
	
	public Point getPoint(int column, int row) {
		final Point point = new Point();
		Entry cell = topLeft;
		
		for(int i = 0; i < row; i++) {
			point.y += cell.getHeight();
			cell = cell.sides[SIDE_BOTTOM];
		}
		for(int i = 0; i < column; i++) {
			point.x += cell.getWidth();
			cell = cell.sides[SIDE_RIGHT];
		}
		
		return point;
	}
	
	/**
	 * Tente d'insérer un objet de taille donnée dans la cellule donnée.
	 * S'il reste de la place dans la ligne et/ou dans la colonne, une nouvelle
	 * ligne/colonne sera créée.
	 * 
	 * @param column
	 * @param row
	 * @param width
	 * @param height
	 * @return 
	 */
	public boolean insertIfPossible(int column, int row, int width, int height) {
		final Entry cell = get(column, row);
		
		int rowspan = cell.getSpan(height, VERTICAL);
		if(rowspan > 0) {
			int colspan = cell.getSpan(width, HORIZONTAL);
			if(colspan > 0) {
				Entry e = topLeft;
				int remainingWidth = width;
				for(int i = 0; i < column; i++) {
					e = e.sides[SIDE_RIGHT];
				}
				for(int i = 1; i < colspan; i++) {
					remainingWidth -= e.getWidth();
					e = e.sides[SIDE_RIGHT];
				}
				e.divide(remainingWidth, HORIZONTAL);

				e = topLeft;
				int remainingHeight = height;
				for(int i = 0; i < row; i++) {
					e = e.sides[SIDE_BOTTOM];
				}
				for(int i = 1; i < rowspan; i++) {
					remainingHeight -= e.getHeight();
					e = e.sides[SIDE_BOTTOM];
				}
				e.divide(remainingHeight, VERTICAL);

				Entry r = cell;
				for(int j = 0; j < rowspan; j++) {
					Entry c = r;
					for(int i = 0; i < colspan; i++) {
						c.empty = false;
						c = c.sides[SIDE_RIGHT];
					}
					r = r.sides[SIDE_BOTTOM];
				}
				return true;
			}
		}
		return false;
	}
	
	public int getColumns() {
		int columns = 0;
		for(Entry e = topLeft; e != null; e = e.sides[SIDE_RIGHT]) {
			columns++;
		}
		return columns;
	}

	public int getRows() {
		int rows = 0;
		for(Entry e = topLeft; e != null; e = e.sides[SIDE_BOTTOM]) {
			rows++;
		}
		return rows;
	}

	public int getWidth() {
		return width;
	}
	
	public int getEnclosingWidth() {
		int realWidth;
		
		if(width == Integer.MAX_VALUE) {
			realWidth = 0;
			for(Entry e = topLeft; e.sides[SIDE_RIGHT] != null; e = e.sides[SIDE_RIGHT]) {
				realWidth += e.getWidth();
			}
		} else {
			realWidth = width;
		}
		
		return realWidth;
	}

	public int getHeight() {
		return height;
	}

	public int getSurface() {
		return getEnclosingWidth() * height;
	}
	
	public static class Entry {
		private boolean empty;
		private int[] size;
		private Entry[] sides;

		public Entry() {
			this(0, 0, true);
		}
		
		public Entry(int width, int height) {
			this(width, height, true);
		}
		
		public Entry(int width, int height, boolean empty) {
			this.empty = empty;
			this.size = new int[] {width, height};
			this.sides = new Entry[4];
		}
		
		public void divide(int location, int direction) {
			divide(location, direction, -1);
		}
		
		public Entry divide(int location, int direction, int from) {
			if(location >= this.size[direction]) {
				return null;
			}
			// FIXME: Dans certains cas (à identifier) la division ne se fait pas
			
			final int otherDirection = 1 - direction;
			
			final int counterpartSize = this.size[direction] == Integer.MAX_VALUE ? Integer.MAX_VALUE : this.size[direction] - location;
			this.size[direction] = location;
			
			final Entry entry = new Entry();
			entry.empty = this.empty;
			entry.size[direction] = counterpartSize;
			entry.size[otherDirection] = this.size[1 - direction];
			
			entry.sides[direction * 2] = this; // = LEFT si HORIZONTAL, TOP si VERTICAL
			entry.sides[direction * 2 + 1] = sides[direction * 2 + 1];
			this.sides[direction * 2 + 1] = entry;
			
			propagate(entry, otherDirection * 2, otherDirection * 2 + 1, location, direction, from);
			propagate(entry, otherDirection * 2 + 1, otherDirection * 2, location, direction, from);
			
			return entry;
		}
		
		private void propagate(Entry entry, int side, int reverse, int location, int direction, int from) {
			if((from == -1 || from == side) && this.sides[side] != null) {
//				final int odd = side % 2;
//				final int reverse = (1 - ((side - odd) / 2)) * 2 + odd;
				
				final Entry newSide = this.sides[side].divide(location, direction, side);
				entry.sides[side] = newSide;
				newSide.sides[reverse] = entry;
			}
		}

		private int getSpan(int length, int orientation) {
			int span = 0;
			long remaining = length;
			for(Entry e = this; remaining > 0 && e != null && e.isEmpty(); e = e.sides[orientation * 2 + 1]) {
				remaining -= e.size[orientation];
				span++;
			}
			if(remaining > 0) {
				return 0;
			}
			return span;
		}
		
		public boolean isEmpty() {
			return empty;
		}

		public void setEmpty(boolean empty) {
			this.empty = empty;
		}
		
		public int getWidth() {
			return this.size[WIDTH];
		}
		
		public int getHeight() {
			return this.size[HEIGHT];
		}
	}
	
	public boolean checkIntegrity() {
		boolean correct = true;
		
		final int columns = getColumns();
		final int rows = getRows();
		
		Entry e = topLeft;
		for(int row = 0; row < rows; row++) {
			Entry cell = e;
			for(int column = 0; column < columns; column++) {
				if(cell != null) {
					if((row == 0) != (cell.sides[SIDE_TOP] == null)) {
						System.err.println(column + "," + row + " : top incorrecte : " + cell);
						correct = false;
					}
					if((row == rows - 1) != (cell.sides[SIDE_BOTTOM] == null)) {
						System.err.println(column + "," + row + " : bottom incorrecte : " + cell);
						correct = false;
					}
					if((column == 0) != (cell.sides[SIDE_LEFT] == null)) {
						System.err.println(column + "," + row + " : left incorrecte : " + cell);
						correct = false;
					}
					if((column == columns - 1) != (cell.sides[SIDE_RIGHT] == null)) {
						System.err.println(column + "," + row + " : right incorrecte : " + cell);
						correct = false;
					}
					cell = cell.sides[SIDE_RIGHT];
					
				} else {
					System.err.println(column + "," + row + " : " + cell);
					correct = false;
				}
			}
			e = e.sides[SIDE_BOTTOM];
		}
		
		return correct;
	}
	
}
