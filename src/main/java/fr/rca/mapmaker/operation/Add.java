package fr.rca.mapmaker.operation;

import java.util.Deque;
import java.util.Map;

/**
 * Représente l'opérateur plus ('+').
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * 
 */
public class Add implements Operator {

	@Override
	public void execute(Map<String, String> clientData, Deque<Double> stack) {
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
}
