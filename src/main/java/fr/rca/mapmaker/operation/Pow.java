package fr.rca.mapmaker.operation;

import fr.rca.mapmaker.model.sprite.Instance;
import java.util.Deque;

/**
 *
 * @author rca
 */
public class Pow implements Function {

    @Override
    public Priority getPriority() {
        return Priority.MULTIPLY;
    }

	@Override
	public int getNumberOfArguments() {
		return 2;
	}

    @Override
    public void execute(double x, Deque<Double> stack, Instance instance) {
        final Double o2 = stack.pop();
		final Double o1 = stack.pop();
		
		stack.push(Math.pow(o1, o2));
    }

    @Override
    public String toString() {
        return "^";
    }

    @Override
    public ByteCode toByteCode() {
        return ByteCode.POW;
    }
}
