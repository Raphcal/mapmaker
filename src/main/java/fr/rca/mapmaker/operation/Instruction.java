package fr.rca.mapmaker.operation;

import java.io.Serializable;
import java.util.Deque;

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
	 * @param x Valeur de x.
	 * @param stack Pile d'exécution.
	 */
	void execute(double x, Deque<Double> stack);
}