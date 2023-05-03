package fr.rca.mapmaker.operation;

import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Direction;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
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
		final Map<String, Double> map = new HashMap<>();
		map.put("leftdirection", (double) Direction.LEFT.ordinal());
		map.put("rightdirection", (double) Direction.RIGHT.ordinal());
		map.put("updirection", (double) Direction.UP.ordinal());
		map.put("downdirection", (double) Direction.DOWN.ordinal());

		DIRECTIONS = map;
	}

	/**
	 * Cet objet n'est pas instantiable, utilisez directement ses méthodes.
	 */
	protected VariableDeclarationParser() {
	}

	/**
	 *
	 * @param script
	 * @return
	 */
	public static Operation parse(String script, Project project) {
		if (script == null || script.trim().isEmpty()) {
			return new Operation();
		}

		final OperationParser parser = new OperationParser();
		final List<TileMap> maps = project.getMaps();
		for (int index = 0; index < maps.size(); index++) {
			// Création d'une constante contenant l'indice de la carte pour chaque carte.
			final TileMap tileMap = maps.get(index);
			final String name = Optional.ofNullable(tileMap.getName()).map(String::toLowerCase).orElse("map" + index);
			parser.putInstruction("maps." + name, new Constant(index));
			parser.putInstruction("maps." + name + ".width", new Constant(tileMap.getWidth() * tileMap.getPalette().getTileSize()));
			parser.putInstruction("maps." + name + ".height", new Constant(tileMap.getHeight()* tileMap.getPalette().getTileSize()));
		}

		final List<Instruction> instructions = new ArrayList<Instruction>();

		for (final String line : script.split("\n")) {
			final int affectationIndex = line.indexOf('=');
			if (affectationIndex > -1) {
				final String left = line.substring(0, affectationIndex).trim().toLowerCase();
				final String right = line.substring(affectationIndex + 1).trim().toLowerCase();

				if ("sprite.direction".equals(left)) {
					final Double direction = DIRECTIONS.get(right);

					if (direction != null) {
						instructions.add(new Constant(direction));
						instructions.add(new SpriteDirection());

					} else {
						LOGGER.warn("Direction incorrecte : '" + right + "'.");
					}

				} else if (left.startsWith("sprite.variables")) {
					final int leftQuoteIndex = left.indexOf('"');
					final int rightQuoteIndex = left.lastIndexOf('"');

					final String variableName = left.substring(leftQuoteIndex + 1, rightQuoteIndex);
					Operation rightOperation = parser.parseOperation(right.toLowerCase());
					instructions.addAll(rightOperation.getInstructions());
					instructions.add(new SpriteVariable(variableName));

				} else if ("sprite.hitbox.top".equals(left)) {
					Operation rightOperation = parser.parseOperation(right.toLowerCase());
					instructions.addAll(rightOperation.getInstructions());
					instructions.add(new SpriteHitboxTop());

				} else {
					// Ligne ignorée.
				}
			}
		}
		return new Operation(instructions);
	}

}
