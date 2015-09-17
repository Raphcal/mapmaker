package fr.rca.mapmaker.model;

import java.awt.Point;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface SelectionListener {
	
	void selectionChanged(Point oldSelection, Point newSelection);
	
}
