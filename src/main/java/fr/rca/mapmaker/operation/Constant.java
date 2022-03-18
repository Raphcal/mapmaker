package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 * Ajoute une constante à la pile.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 *
 */
public class Constant implements Instruction {

	private final double value;

	public Constant() {
		this.value = 0;
	}

	/**
	 * Créé une nouvelle constante à partir de la valeur donnée en argument.
	 * @param value Valeur de la constante.
	 */
	public Constant(double value) {
		this.value = value;
	}
	
	@Override
	public void execute(double x, Deque<Double> stack, fr.rca.mapmaker.model.sprite.Instance instance) {
		stack.push(value);
	}

	public double getValue() {
		return value;
	}

	@Override
	public String toString() {
		if (value == Math.PI) {
			return "pi";
		} else if (value == Math.E) {
			return "e";
		}
		return new Long((long)value).doubleValue() == value
				? Long.toString((long)value)
				: Double.toString(value);
	}

	@Override
	public ByteCode toByteCode() {
		if (value == Math.PI) {
			return ByteCode.PI;
		} else if (value == Math.E) {
			return ByteCode.E;
		}
		return ByteCode.CONSTANT;
	}

}
