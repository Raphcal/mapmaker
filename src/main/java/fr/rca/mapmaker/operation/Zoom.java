package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class Zoom implements Function {
	
	private double zoom;

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

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
		stack.push(stack.pop() * zoom);
	}

	@Override
	public String toString() {
		return "zoom";
	}
	
	@Override
	public ByteCode toByteCode() {
		return ByteCode.ZOOM;
	}
	
}
