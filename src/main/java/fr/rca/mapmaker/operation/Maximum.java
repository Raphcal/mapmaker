package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 * Compare 2 valeurs et renvoie la plus élevée.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
class Maximum implements Operator {

	@Override
	public Priority getPriority() {
		return Priority.COMPARE;
	}

	@Override
	public void execute(double x, Deque<Double> stack) {
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
		
		if(o1 > o2) {
			stack.push(o1);
		} else {
			stack.push(o2);
		}
	}
	
	@Override
	public String toString() {
		return "max";
	}
}
