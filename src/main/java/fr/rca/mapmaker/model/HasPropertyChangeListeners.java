package fr.rca.mapmaker.model;

import java.beans.PropertyChangeListener;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface HasPropertyChangeListeners {
	void addPropertyChangeListener(PropertyChangeListener listener);
	void addPropertyChangeListener(String propertyName, PropertyChangeListener listener);
	void removePropertyChangeListener(PropertyChangeListener listener);
	void removePropertyChangeListener(String propertyName, PropertyChangeListener listener);
}
