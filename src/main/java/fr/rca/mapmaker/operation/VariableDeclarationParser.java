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
		map.put("RightDirection", 0.0);
		
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
				final String left = line.substring(0, affectationIndex).trim();
				final String right = line.substring(affectationIndex + 1).trim();
				
				if("sprite.Direction".equals(left)) {
					final Double direction = DIRECTIONS.get(right);
					
					if(direction != null) {
						instructions.add(new Constant(direction));
						instructions.add(new SpriteDirection());
						
					} else {
						LOGGER.warn("Direction incorrecte : '" + right + "'.");
					}
					
				} else if(left.startsWith("sprite.Variables")) {
					final int leftQuoteIndex = left.indexOf('"');
					final int rightQuoteIndex = left.lastIndexOf('"');
					
					final String variableName = left.substring(leftQuoteIndex + 1, rightQuoteIndex);
					OperationParser.parse(right.toLowerCase(), 0, null, instructions);
					instructions.add(new SpriteVariable(variableName));
					
				} else {
					// Ligne ignorée.
				}
			}
		}
		return new Operation(instructions);
	}
	
	public static void main(String[] args) {
		final Operation operation = parse("sprite.Direction = LeftDirection\n"
			+ "sprite.Variables[\"Angle\"] = -3.0 * PI / 4.0\n"
			+ "test=meuh");
		
		System.out.println(operation);
	}
}
