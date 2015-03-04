package fr.rca.mapmaker.operation;

/**
 * Représente une instruction de type "opérateur".<br>
 * <br>
 * Un opérateur utilise 2 valeurs pour en générer une autre.
 * Par exemple : l'opérateur {@link Add} donne le résultat de l'addition de 2
 * valeurs.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 *
 */
public interface Operator extends Instruction {
	
	/**
	 * Priorité d'un opérateur.
	 * 
	 * @author Raphaël Calabro (rcalabro@ideia.fr)
	 *
	 */
	public enum Priority {
		/**
		 * Opérateur logique (et, ou).
		 */
		LOGICAL,
		/**
		 * Opérateur de comparaison (égalité, supérieur, etc).
		 */
		COMPARE,
		/**
		 * Opérateur d'addition/soustraction.
		 */
		ADD_SUBSTRACT,
		/**
		 * Opérateur de multiplication/division.
		 */
		MULTIPLY_DIVIDE,
		/**
		 * Opérateur avancé (matches, in, etc).
		 */
		FUNCTION,
		/**
		 * Opérateur unaire (négation).
		 */
		UNARY
	}
	
	/**
	 * Récupère la priorité de l'opérateur.
	 * @return La priorité de cet opérateur.
	 */
	Priority getPriority();
}
