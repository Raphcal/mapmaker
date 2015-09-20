package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class SquareRoot implements Function {

	@Override
	public int getNumberOfArguments() {
		return 1;
	}
	
	@Override
	public Priority getPriority() {
		return Priority.FUNCTION;
	}
	
	@Override
	public void execute(double x, Deque<Double> stack) {
		stack.push(Math.sqrt(stack.pop()));
	}

	@Override
	public String toString() {
		return "sqrt";
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.SQUARE_ROOT;
	}
	
}
