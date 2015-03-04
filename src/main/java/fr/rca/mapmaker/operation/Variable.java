package fr.rca.mapmaker.operation;

import java.util.Deque;
import java.util.Map;

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
	public void execute(Map<String, String> clientData, Deque<Double> stack) {
		if(clientData != null) {
			stack.push(Double.valueOf(clientData.get(name)));
		} else {
			stack.push(null);
		}
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return name;
	}
}
