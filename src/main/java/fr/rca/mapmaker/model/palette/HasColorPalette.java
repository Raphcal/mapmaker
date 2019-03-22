package fr.rca.mapmaker.model.palette;

/**
 * Indique qu'une classe possède une palette de couleur.
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public interface HasColorPalette {
    
    /**
     * Récupère la palette de couleur.
     *
     * @return La palette de couleur.
     */
    ColorPalette getColorPalette();

}
