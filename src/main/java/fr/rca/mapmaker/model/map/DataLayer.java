package fr.rca.mapmaker.model.map;

import java.awt.Rectangle;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface DataLayer extends Layer {
	int[] copyData();
	void restoreData(int[] tiles, Rectangle source);
}
