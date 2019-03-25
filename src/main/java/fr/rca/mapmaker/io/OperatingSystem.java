package fr.rca.mapmaker.io;

/**
 * Classe utilitaire pour avoir des informations sur le système d'exploitation
 * de l'utilisateur.
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public final class OperatingSystem {
    
    public static final boolean IS_MAC_OS;
    
    static {
        final String osName = System.getProperty("os.name");
        // TODO: Vérifier ce que renvoi Mojave.
        IS_MAC_OS = osName != null && (osName.startsWith("Mac OS X") || osName.startsWith("macOS"));
    }
    
}
