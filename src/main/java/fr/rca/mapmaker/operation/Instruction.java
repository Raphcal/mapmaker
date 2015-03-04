package fr.rca.mapmaker.operation;

import java.io.Serializable;
import java.util.Deque;
import java.util.Map;

/**
 * Classe parente de toutes les instructions.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public interface Instruction extends Serializable {

	/**
	 * Exécute l'instruction à partir des valeurs du client et éléments déjà
	 * contenus dans la pile d'exécution.
	 * 
	 * @param data Liste de variables.
	 * @param stack Pile d'exécution.
	 */
	void execute(Map<String, String> data, Deque<Double> stack);
}
