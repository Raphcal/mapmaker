package fr.rca.mapmaker.model.map;

import java.util.ArrayList;
import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import org.slf4j.LoggerFactory;

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
    
    public static Collection<LayerPlugin> copyOf(@NotNull Collection<LayerPlugin> plugins) {
        final ArrayList<LayerPlugin> copy = new ArrayList<LayerPlugin>();
        for (final LayerPlugin plugin : plugins) {
            copy.add(plugin.copy());
        }
        return copy;
    }
    
    public static @Nullable <L extends LayerPlugin> String nameOf(@NotNull Class<L> clazz) {
        try {
            final L layerPlugin = clazz.newInstance();
            return layerPlugin.name();
        } catch (InstantiationException | IllegalAccessException ex) {
            LoggerFactory.getLogger(LayerPlugins.class).error("Impossible d'instancier le plugin de type '" + clazz + "'.", ex);
        }
        return null;
    }
    
	private LayerPlugins() {
		// Pas de construction.
	} 
	
}
