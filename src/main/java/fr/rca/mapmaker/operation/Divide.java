package fr.rca.mapmaker.operation;

import java.util.Deque;
import java.util.logging.Logger;

/**
 * Représente l'opérateur diviser ('/').
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 *
 */
public class Divide implements Operator {
	
	private static final Logger LOGGER = Logger.getLogger(Divide.class.getName());

	@Override
	public void execute(double x, Deque<Double> stack) {
		final Double o2 = stack.pop();
		final Double o1 = stack.pop();
		
		if(o2 == 0.0) {
			stack.push(0.0);
			LOGGER.warning("[OPERATION] Tentative de division par zéro.");

		} else {
			stack.push(o1 / o2);
		}
	}

	@Override
	public Priority getPriority() {
		return Priority.MULTIPLY_DIVIDE;
	}

	@Override
	public String toString() {
		return "/";
	}
}
