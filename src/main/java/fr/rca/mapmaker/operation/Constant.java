package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 * Ajoute une constante à la pile.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 *
 */
public class Constant implements Instruction {

	private double value;

	public Constant() {
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
		return '"' + String.valueOf(value) + '"';
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.CONSTANT;
	}
	
}
