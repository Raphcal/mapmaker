package fr.rca.mapmaker.operation;

import java.util.Deque;

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
		 * Opérateur de division.
		 */
		DIVIDE,
		/**
		 * Opérateur de multiplication.
		 */
		MULTIPLY,
		/**
		 * Fonction (cosinus, sinus, etc.).
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

	@Override
	default void pushString(Deque<String> stack, Language language) {
		final String self = language.translate(this);
		switch (language.priority(this)) {
			case FUNCTION:
				StringBuilder stringBuilder = new StringBuilder();
				int argCount = this instanceof Function ? ((Function)this).getNumberOfArguments() : 1;
				for (int arg = 0; arg < argCount; arg++) {
					if (arg > 0) {
						stringBuilder.insert(0, ", ");
					}
					stringBuilder.insert(0, stack.pop());
				}
				stringBuilder.insert(0, '(')
						.insert(0, self)
						.append(')');

				stack.push(stringBuilder.toString());
				break;
			case UNARY:
				stack.push(self + stack.pop());
				break;
			default:
				final String rhs = stack.pop();
				final String lhs = stack.pop();
				stack.push(lhs + ' ' + self + ' ' + rhs);
				break;
		}
	}

	
}
