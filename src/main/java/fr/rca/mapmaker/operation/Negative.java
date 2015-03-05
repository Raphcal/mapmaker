package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Negative implements Instruction {

	@Override
	public void execute(double x, Deque<Double> stack) {
		stack.push(-stack.pop());
	}

	@Override
	public String toString() {
		return "-";
	}
	
}
