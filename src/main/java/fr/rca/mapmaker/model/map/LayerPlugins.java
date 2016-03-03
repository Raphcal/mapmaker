package fr.rca.mapmaker.model.map;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class LayerPlugins {
	
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
