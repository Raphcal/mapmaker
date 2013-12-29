package fr.rca.mapmaker.model;

import java.awt.Dimension;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface SizeChangeListener {
	
	void sizeChanged(Object source, Dimension oldSize, Dimension newSize);
}
