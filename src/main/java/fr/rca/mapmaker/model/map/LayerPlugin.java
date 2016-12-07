package fr.rca.mapmaker.model.map;

/**
 * Permet d'ajouter des propriétés à une couche.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface LayerPlugin {
	
    /**
     * Nom du plugin.
     * 
     * @return Le nom du plugin.
     */
    String name();
    
	/**
	 * Copie les données du plugin.
	 * 
	 * @return Une nouvelle instance contenant les même informations.
	 */
	LayerPlugin copy();
	
}
