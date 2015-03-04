package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 * Ajoute la valeur d'une variable à la pile.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 *
 */
public class Variable implements Instruction {
	
	private final String name;
	
	/**
	 * Créé une référence vers la variable dont le nom est donné en argument.
	 * @param name Nom de la variable.
	 */
	public Variable(String name) {
		this.name = name.substring(1); // Suppression du '$'
	}

	@Override
	public void execute(double x, Deque<Double> stack) {
		stack.push(x);
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
