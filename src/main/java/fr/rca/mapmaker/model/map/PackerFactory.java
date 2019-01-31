package fr.rca.mapmaker.model.map;

/**
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public final class PackerFactory {
    
    public static Packer createPacker() {
        return new PackMap(0, 0, 1);
    } 
    
    private PackerFactory() {
        // Vide.
    }
    
}
