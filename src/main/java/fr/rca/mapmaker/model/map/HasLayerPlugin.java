package fr.rca.mapmaker.model.map;

import java.util.Collection;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Indique qu'une couche supporte les plugins.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface HasLayerPlugin {
	@Nullable <L extends LayerPlugin> L getPlugin(@NotNull Class<L> clazz);
	@NotNull Collection<LayerPlugin> getPlugins();
	void setPlugin(@NotNull LayerPlugin plugin);
    <L extends LayerPlugin> void removePlugin(@NotNull Class<L> clazz);
}
