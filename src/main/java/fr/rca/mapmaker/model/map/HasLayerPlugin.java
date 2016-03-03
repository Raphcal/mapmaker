package fr.rca.mapmaker.model.map;

import org.jetbrains.annotations.Nullable;

/**
 * Indique qu'une couche supporte les plugins.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface HasLayerPlugin {
	
	@Nullable LayerPlugin getPlugin();
	void setPlugin(@Nullable LayerPlugin plugin);
	
}
