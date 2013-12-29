package fr.rca.mapmaker.io;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum SupportedOperation {
	/**
	 * Le format supporte l'enregistrement de projets.
	 */
	SAVE,
	/**
	 * Le format supporte le chargement de projets.
	 */
	LOAD,
	/**
	 * Le format peut importer des ressources provenant d'un autre projet.
	 */
	IMPORT
}
