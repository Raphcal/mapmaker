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
