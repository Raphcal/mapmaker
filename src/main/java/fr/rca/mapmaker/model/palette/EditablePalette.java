package fr.rca.mapmaker.model.palette;

import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface EditablePalette extends Palette {

	void setName(String name);
	
	void editTile(int index, JFrame parent);
	
	void removeTile(int index);
	
	void removeRow();
	void insertRowBefore();
	void insertRowAfter();
}
