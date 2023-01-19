package fr.rca.mapmaker.model.palette;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface EditablePalette extends Palette {

	void setName(String name);
	
	void editTile(int index, java.awt.Frame parent);
	
	void removeTile(int index);
	
	void removeRow();
	void insertRowBefore();
	void insertRowAfter();
}
