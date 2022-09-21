package fr.rca.mapmaker.model.map;

import java.beans.PropertyChangeSupport;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class ScrollRate {
	public static final ScrollRate IDENTITY = new ScrollRate() {

		@Override
		public void setX(double x) {
			throw new UnsupportedOperationException();
		}

		@Override
		public void setY(double y) {
			throw new UnsupportedOperationException();
		}
		
	};
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private double x;
	private double y;

	public ScrollRate() {
		this(1.0f, 1.0f);
	}

	public ScrollRate(ScrollRate other) {
		this(other.x, other.y);
	}

	public ScrollRate(double x, double y) {
		this.x = x;
		this.y = y;
	}

	@Override
	public int hashCode() {
		int hash = 3;
		hash = 97 * hash + (int) (Double.doubleToLongBits(this.x) ^ (Double.doubleToLongBits(this.x) >>> 32));
		hash = 97 * hash + (int) (Double.doubleToLongBits(this.y) ^ (Double.doubleToLongBits(this.y) >>> 32));
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final ScrollRate other = (ScrollRate) obj;
		if (Double.doubleToLongBits(this.x) != Double.doubleToLongBits(other.x)) {
			return false;
		}
		return Double.doubleToLongBits(this.y) == Double.doubleToLongBits(other.y);
	}

	public double getX() {
		return x;
	}

	public void setX(double x) {
		final double oldX = this.x;
		this.x = x;
		propertyChangeSupport.firePropertyChange("x", oldX, x);
	}
	
	public double getY() {
		return y;
	}

	public void setY(double y) {
		final double oldY = this.y;
		this.y = y;
		propertyChangeSupport.firePropertyChange("y", oldY, y);
	}
	
}
