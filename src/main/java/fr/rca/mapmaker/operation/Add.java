package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 * Représente l'opérateur plus ('+').
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * 
 */
public class Add implements Operator {

	@Override
	public void execute(double x, Deque<Double> stack) {
		final Double o2 = stack.pop();
		final Double o1 = stack.pop();
		
		stack.push(o1 + o2);
	}

	@Override
	public Priority getPriority() {
		return Priority.ADD_SUBSTRACT;
	}
	
	@Override
	public String toString() {
		return "+";
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.ADD;
	}
	
}
