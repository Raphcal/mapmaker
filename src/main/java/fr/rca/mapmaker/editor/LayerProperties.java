package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import lombok.Getter;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Getter
public class LayerProperties {

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private String name;

	private ScrollRate scrollRate = new ScrollRate();
	private int width;
	private int height;
	private boolean solid;

	public TileLayer newTileLayer() {
		final TileLayer tileLayer = new TileLayer(width, height);
		tileLayer.setScrollRate(scrollRate);
		tileLayer.setName(name);
		tileLayer.setSolid(solid);
		return tileLayer;
	}

	public void affect(TileLayer tileLayer) {
		tileLayer.resize(width, height);
		tileLayer.setScrollRate(scrollRate);
		tileLayer.setName(name);
		tileLayer.setSolid(solid);
	}

	public void setName(String name) {
		final String oldName = this.name;
		this.name = name;

		propertyChangeSupport.firePropertyChange("name", oldName, name);
	}

	public void setWidth(int width) {
		final String oldSize = getSize();
		this.width = width;
		propertyChangeSupport.firePropertyChange("size", oldSize, getSize());
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

	public void setSolid(boolean solid) {
		final boolean oldSolid = this.solid;
		this.solid = solid;

		propertyChangeSupport.firePropertyChange("solid", oldSolid, solid);
	}

	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}

	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}

}
