package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.Layer;
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
	private ScrollRate scrollRate;

	public String getName() {
		return name;
	}

	public void setName(String name) {
		final String oldName = this.name;
		this.name = name;
		
		propertyChangeSupport.firePropertyChange("name", oldName, name);
	}

	public ScrollRate getScrollRate() {
		return scrollRate;
	}

	public void setScrollRate(ScrollRate scrollRate) {
		final ScrollRate oldScrollRate = this.scrollRate;
		this.scrollRate = new ScrollRate(scrollRate);
		
		propertyChangeSupport.firePropertyChange("scrollRate", oldScrollRate, scrollRate);
	}
	
	public void addPropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.addPropertyChangeListener(listener);
	}
	
	public void removePropertyChangeListener(PropertyChangeListener listener) {
		propertyChangeSupport.removePropertyChangeListener(listener);
	}
}
