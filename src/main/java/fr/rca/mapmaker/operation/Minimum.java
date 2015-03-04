package fr.rca.mapmaker.operation;

import java.util.Deque;
import java.util.Map;

/**
 * Compare 2 valeurs et renvoie la plus petite.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Minimum implements Operator {

	@Override
	public Priority getPriority() {
		return Priority.COMPARE;
	}

	@Override
	public void execute(Map<String, String> clientData, Deque<Double> stack) {
		final Double o2 = stack.pop();
		final Double o1 = stack.pop();
		
		if(o1 == null) {
			stack.push(o2);
			return;
		}
		
		if(o2 == null) {
			stack.push(o1);
			return;
		}
		
		if(o1 < o2) {
			stack.push(o1);
		} else {
			stack.push(o2);
		}
	}
	
	@Override
	public String toString() {
		return "min";
	}
}
