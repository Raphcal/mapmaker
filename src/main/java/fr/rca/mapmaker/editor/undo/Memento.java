package fr.rca.mapmaker.editor.undo;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface Memento {

	void begin();

	void clear();

	void end();

	boolean isRedoable();

	boolean isUndoable();

	void redo();

	void undo();
	
}
