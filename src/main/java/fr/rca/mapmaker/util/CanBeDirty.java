package fr.rca.mapmaker.util;

/**
 *
 * @author Raphaël CALABRO (raphael.calabro.external2@banque-france.fr)
 */
public interface CanBeDirty {
	boolean isDirty();
	void setDirty(boolean dirty);

	static CanBeDirty wrap(Object object) {
		if (object instanceof CanBeDirty) {
			return (CanBeDirty) object;
		} else {
			return AlwaysDirty.INSTANCE;
		}
	}
}

class AlwaysDirty implements CanBeDirty {
	static final AlwaysDirty INSTANCE = new AlwaysDirty();

	@Override
	public boolean isDirty() {
		return true;
	}

	@Override
	public void setDirty(boolean dirty) {
		// Aucun effet.
	}
}
