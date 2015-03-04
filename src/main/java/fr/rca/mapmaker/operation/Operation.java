package fr.rca.mapmaker.operation;

import java.util.ArrayDeque;
import java.util.List;


/**
 * Représente une opération.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @author Kristela Macaj (kmacaj@ideia.fr)
 * 
 */
public class Operation {
	
	/**
	 * La liste d'instructions à exécuter.
	 */
	private final List<Instruction> instructions;
	
	/**
	 * Créé une nouvelle opération à partir d'une liste d'instructions.
	 * @param instructions Liste d'instructions à exécuter.
	 */
	public Operation(List<Instruction> instructions) {
		this.instructions = instructions;
	}
	
	/**
	 * Exécute cette opération à partir des données du client.
	 * @param x Valeur de x.
	 * @return Le résultat de cette opération.
	 */
	public Object execute(double x) {
		final ArrayDeque<Double> stack = new ArrayDeque<Double>();
		
		for(final Instruction instruction : instructions) {
			instruction.execute(x, stack);
		}
		
		// Renvoi du résultat
		return stack.peek();
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		
		for(final Instruction instruction : instructions) {
			stringBuilder.append(instruction).append(' ');
		}
		
		if(stringBuilder.length() > 0) {
			stringBuilder.setLength(stringBuilder.length() - 1);
		}
		
		return stringBuilder.toString();
	}
}
