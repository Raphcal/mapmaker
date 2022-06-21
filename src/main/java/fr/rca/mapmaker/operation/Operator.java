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
		 * Opérateur de multiplication/division.
		 */
		MULTIPLY_DIVIDE,
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
		final Operator.Priority priority = language.priority(this);
		switch (priority) {
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
				stack.push(self + Operator.addBraces(stack.pop()));
				break;
			default:
				String rhs = stack.pop();
				String lhs = stack.pop();
				if (priority == Operator.Priority.MULTIPLY_DIVIDE) {
					rhs = Operator.addBraces(rhs);
					lhs = Operator.addBraces(lhs);
				}
				stack.push(lhs + ' ' + self + ' ' + rhs);
				break;
		}
	}

	static String addBraces(String value) {
		int braceCount = 0;
		boolean mayBeUnary = false;
		for (char c : value.toCharArray()) {
			if (c == '(') {
				braceCount++;
			} else if (c == ')') {
				braceCount--;
			} else if (braceCount == 0 && c == '+') {
				return '(' + value + ')';
			} else if (braceCount == 0 && c == '-') {
				mayBeUnary = true;
				continue;
			} else if (mayBeUnary && c == ' ') {
				return '(' + value + ')';
			}
			mayBeUnary = false;
		}
		return value;
	}

	
}
