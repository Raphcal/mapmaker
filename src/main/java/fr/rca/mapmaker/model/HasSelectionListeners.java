package fr.rca.mapmaker.model;

import java.awt.Point;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface HasSelectionListeners {
	
	void addSelectionListener(SelectionListener listener);
	void removeSelectionListener(SelectionListener listener);
	Point getSelection();
}
