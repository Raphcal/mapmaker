package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 * Représente l'opérateur multiplier ('*').
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * 
 */
public class Multiply implements Operator {

	@Override
	public void execute(double x, Deque<Double> stack, fr.rca.mapmaker.model.sprite.Instance instance) {
		final Double o2 = stack.pop();
		final Double o1 = stack.pop();
		
		stack.push(o1 * o2);
	}

	@Override
	public Priority getPriority() {
		return Priority.MULTIPLY_DIVIDE;
	}

	@Override
	public String toString() {
		return "*";
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.MULTIPLY;
	}
	
}
