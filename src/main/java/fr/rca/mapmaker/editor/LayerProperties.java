package fr.rca.mapmaker.editor;

import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LayerProperties {
	
	private PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	private String name;
	private float scrollRate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		final String oldName = this.name;
		this.name = name;
		
		propertyChangeSupport.firePropertyChange("name", oldName, name);
	}

	public float getScrollRate() {
		return scrollRate;
	}

	public void setScrollRate(float scrollRate) {
		final float oldScrollRate = this.scrollRate;
		this.scrollRate = scrollRate;
		
		propertyChangeSupport.firePropertyChange("scrollRate", oldScrollRate, scrollRate);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
