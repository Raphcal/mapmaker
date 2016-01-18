package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Sinus implements Function {

	@Override
	public int getNumberOfArguments() {
		return 1;
	}

	@Override
	public Priority getPriority() {
		return Priority.FUNCTION;
	}

	@Override
	public void execute(double x, Deque<Double> stack, fr.rca.mapmaker.model.sprite.Instance instance) {
		stack.push(Math.sin(stack.pop()));
	}

	@Override
	public String toString() {
		return "sin";
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.SINUS;
	}
	
}
