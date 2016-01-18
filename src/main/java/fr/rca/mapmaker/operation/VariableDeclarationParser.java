package fr.rca.mapmaker.operation;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class VariableDeclarationParser {
	private static final Logger LOGGER = LoggerFactory.getLogger(VariableDeclarationParser.class);
	private static final Map<String, Double> DIRECTIONS;
	
	static {
		final Map<String, Double> map = new HashMap<String, Double>();
		map.put("leftdirection", 0.0);
		map.put("rightdirection", 1.0);
		
		DIRECTIONS = map;
	}
	
	/**
	 * Cet objet n'est pas instantiable, utilisez directement ses méthodes.
	 */
	protected VariableDeclarationParser() {}
	
	/**
	 * 
	 * @param script 
	 * @return  
	 */
	public static Operation parse(String script) {
		if (script == null || script.trim().isEmpty()) {
			return new Operation();
		}
		
		final List<Instruction> instructions = new ArrayList<Instruction>();
		
		for(final String line : script.split("\n")) {
			final int affectationIndex = line.indexOf('=');
			if(affectationIndex > -1) {
				final String left = line.substring(0, affectationIndex).trim().toLowerCase();
				final String right = line.substring(affectationIndex + 1).trim().toLowerCase();
				
				if("sprite.direction".equals(left)) {
					final Double direction = DIRECTIONS.get(right);
					
					if(direction != null) {
						instructions.add(new Constant(direction));
						instructions.add(new SpriteDirection());
						
					} else {
						LOGGER.warn("Direction incorrecte : '" + right + "'.");
					}
					
				} else if(left.startsWith("sprite.variables")) {
					final int leftQuoteIndex = left.indexOf('"');
					final int rightQuoteIndex = left.lastIndexOf('"');
					
					final String variableName = left.substring(leftQuoteIndex + 1, rightQuoteIndex);
					OperationParser.parse(right.toLowerCase(), 0, null, instructions);
					instructions.add(new SpriteVariable(variableName));
					
				} else if("sprite.hitbox.top".equals(left)) {
					OperationParser.parse(right.toLowerCase(), 0, null, instructions);
					instructions.add(new SpriteHitboxTop());
					
				} else {
					// Ligne ignorée.
				}
			}
		}
		return new Operation(instructions);
	}
	
}
