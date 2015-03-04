package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 * Ajoute une constante à la pile.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 *
 */
public class Constant implements Instruction {

	private Double value;

	public Constant() {
	}

	/**
	 * Créé une nouvelle constante à partir de la valeur donnée en argument.
	 * @param value Valeur de la constante.
	 */
	public Constant(Double value) {
		this.value = value;
	}
	
	@Override
	public void execute(double x, Deque<Double> stack) {
		stack.push(value);
	}
	
	@Override
	public String toString() {
		return '"' + String.valueOf(value) + '"';
	}

	public Double getValue() {
		return value;
	}
}
