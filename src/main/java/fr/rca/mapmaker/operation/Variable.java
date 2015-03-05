package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 * Ajoute la valeur d'une variable à la pile.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 *
 */
public class Variable implements Instruction {
	
	@Override
	public void execute(double x, Deque<Double> stack) {
		stack.push(x);
	}

	public String getName() {
		return "x";
	}
	
	@Override
	public String toString() {
		return "x";
	}
}
