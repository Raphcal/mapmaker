package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Negative implements Instruction {

	@Override
	public void execute(double x, Deque<Double> stack, fr.rca.mapmaker.model.sprite.Instance instance) {
		stack.push(-stack.pop());
	}

	@Override
	public String toString() {
		return "-";
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.NEGATIVE;
	}
	
}
