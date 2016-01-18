package fr.rca.mapmaker.operation;

import java.util.Deque;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Représente l'opérateur diviser ('/').
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 *
 */
public class Divide implements Operator {
	
	private static final Logger LOGGER = LoggerFactory.getLogger(Divide.class);

	@Override
	public void execute(double x, Deque<Double> stack, fr.rca.mapmaker.model.sprite.Instance instance) {
		final Double o2 = stack.pop();
		final Double o1 = stack.pop();
		
		if(o2 == 0.0) {
			stack.push(0.0);
			LOGGER.warn("[OPERATION] Tentative de division par zéro.");

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

	@Override
	public ByteCode toByteCode() {
		return ByteCode.DIVIDE;
	}
	
}
