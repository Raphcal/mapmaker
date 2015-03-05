package fr.rca.mapmaker.operation;

import java.util.HashMap;

/**
 * Liste de toutes les instructions utilisables dans les opérations.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class Instructions {

	/**
	 * Table de correspondance entre le nom d'une instruction et son
	 * implémentation.
	 */
	private static final HashMap<String, Instruction> INSTRUCTIONS;
	
	/**
	 * Classe utilitaire, toutes les méthodes sont statiques.
	 */
	private Instructions() {}
	
	static {
		final HashMap<String, Instruction> map = new HashMap<String, Instruction>();
		map.put("x", new Variable());
		map.put("pi", new Constant(Math.PI));
		map.put("e", new Constant(Math.E));
		
		map.put("+", new Add());
		map.put("-", new Substract());
		map.put("*", new Multiply());
		map.put("/", new Divide());
		
		map.put("min", new Minimum());
		map.put("max", new Maximum());
		
		map.put("cos", new Cosinus());
		map.put("sin", new Sinus());
				
		INSTRUCTIONS = map;
	}
	
	/**
	 * Récupère l'instruction nommée <code>name</code>.
	 * @param name Nom de l'instruction à récupérer.
	 * @return L'instruction ou <code>null</code> si <code>name</code> ne
	 * correspond à aucune instruction.
	 */
	public static Instruction getInstruction(String name) {
		return INSTRUCTIONS.get(name.toLowerCase());
	}
	
}
