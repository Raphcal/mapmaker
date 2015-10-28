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
		map.put("LeftDirection", 0.0);
		map.put("RightDirection", 1.0);
		
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
		final List<Instruction> instructions = new ArrayList<Instruction>();
		
		for(final String line : script.split("\n")) {
			final int affectationIndex = line.indexOf('=');
			if(affectationIndex > -1) {
				final String left = line.substring(0, affectationIndex).trim().toLowerCase();
				final String right = line.substring(affectationIndex + 1).trim();
				
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
	
	public static boolean isFacingRight(String script) {
		if(script == null) {
			return true;
		}
		
		final Operation operation = parse(script);
		
		Instruction previousInstruction = null;
		for(final Instruction instruction : operation.getInstructions()) {
			if(instruction instanceof SpriteDirection && previousInstruction instanceof Constant) {
				return ((Constant) previousInstruction).getValue() == 1;
			}
			previousInstruction = instruction;
		}
		return true;
	}
}
