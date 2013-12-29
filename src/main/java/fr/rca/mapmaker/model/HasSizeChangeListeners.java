package fr.rca.mapmaker.model;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface HasSizeChangeListeners {
	void addSizeChangeListener(SizeChangeListener listener);
	void removeSizeChangeListener(SizeChangeListener listener);
}
