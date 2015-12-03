package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.ScrollRate;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LayerProperties {
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private String name;
	
	private ScrollRate scrollRate = new ScrollRate();
	private int width;
	private int height;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		final String oldName = this.name;
		this.name = name;
		
		propertyChangeSupport.firePropertyChange("name", oldName, name);
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		final String oldSize = getSize();
		this.width = width;
		propertyChangeSupport.firePropertyChange("size", oldSize, getSize());
	}
	
	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		final String oldSize = getSize();
		this.height = height;
		propertyChangeSupport.firePropertyChange("size", oldSize, getSize());
	}
	
	public String getSize() {
		return Integer.toString(width) + 'x' + height;
	}
	
	public void setSize(String size) {
		if (size == null) {
			return;
		}
		
		final int separation = size.indexOf('x');
		if (separation < 1) {
			return;
		}
		
		final String oldSize = getSize();
		
		try {
			width = Integer.parseInt(size.substring(0, separation));
		} catch (NumberFormatException e) {
			// Exception ignorée.
		}
		try {
			height = Integer.parseInt(size.substring(separation + 1, size.length()));
		} catch (NumberFormatException e) {
			// Exception ignorée.
		}
		
		propertyChangeSupport.firePropertyChange("size", oldSize, getSize());
	}
	
	public ScrollRate getScrollRate() {
		return scrollRate;
	}

	public void setScrollRate(ScrollRate scrollRate) {
		final String oldScrollRates = getScrollRates();
		this.scrollRate = new ScrollRate(scrollRate);
		
		propertyChangeSupport.firePropertyChange("scrollRates", oldScrollRates, getScrollRates());
	}
	
	public String getScrollRates() {
		return Double.toString(scrollRate.getX()) + 'x' + scrollRate.getY();
	}
	
	public void setScrollRates(final String value) {
		if (value == null) {
			return;
		}
		
		final int separation = value.indexOf('x');
		if (separation < 1) {
			return;
		}
		
		final String oldScrollRates = getScrollRates();
		final String rates = value.replace(',', '.');
		
		try {
			scrollRate.setX(Double.parseDouble(rates.substring(0, separation)));
		} catch (NumberFormatException e) {
			// Exception ignorée.
		}
		try {
			scrollRate.setY(Double.parseDouble(rates.substring(separation + 1, value.length())));
		} catch (NumberFormatException e) {
			// Exception ignorée.
		}
		
		propertyChangeSupport.firePropertyChange("scrollRates", oldScrollRates, getScrollRates());
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
	
}
