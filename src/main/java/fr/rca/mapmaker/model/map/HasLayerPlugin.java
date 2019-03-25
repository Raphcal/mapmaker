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
    
    /**
     * Récupère l'instance du plugin donné.
     *
     * @param <L> Type du plugin.
     * @param clazz Classe du plugin.
     * @return L'instance si la couche possède ce plugin ou <code>null</code>.
     */
	@Nullable <L extends LayerPlugin> L getPlugin(@NotNull Class<L> clazz);
    
    /**
     * Récupère tous les plugins présents sur la couche.
     *
     * @return Tous les plugins de la couche.
     */
	@NotNull Collection<LayerPlugin> getPlugins();
    
    /**
     * Ajoute ou remplace le plugin donné.
     *
     * @param plugin Plugin à ajouter.
     */
	void setPlugin(@NotNull LayerPlugin plugin);

    /**
     * Supprime le plugin correspondant.
     *
     * @param <L> Type du plugin.
     * @param clazz Classe du plugin.
     */
    <L extends LayerPlugin> void removePlugin(@NotNull Class<L> clazz);

}
