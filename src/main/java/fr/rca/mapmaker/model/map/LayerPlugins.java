package fr.rca.mapmaker.model.map;

/**
 * Ensemble de méthodes utilitaires pour gérer les plugins de layers.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class LayerPlugins {
	
	/**
	 * Copie le plugin donné si différent de <code>null</code>.
	 * 
	 * @param plugin Plugin à copier.
	 * @return Une copie du plugin ou <code>null</code> si le plugin donné
	 * est <code>null</code>.
	 */
	public static LayerPlugin copyOf(LayerPlugin plugin) {
		if (plugin != null) {
			return plugin.copy();
		} else {
			return null;
		}
	}
	
	private LayerPlugins() {
		// Pas de construction.
	} 
	
}
