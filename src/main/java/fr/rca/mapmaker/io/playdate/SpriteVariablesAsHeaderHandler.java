package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.sprite.Sprite;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class SpriteVariablesAsHeaderHandler extends CodeDataHandler<Map<Sprite, Set<String>>> {

	public static int NO_VALUE = -1;

	@Override
	public void write(Map<Sprite, Set<String>> t, OutputStream outputStream) throws IOException {
		outputStream.write((generateHeader(t)
				+ "#ifndef spritevariables_h\n"
				+ "#define spritevariables_h\n"
				+ "\n"
				+ "#define kSpriteVariableNoValue " + NO_VALUE + "\n"
				+ "\n"
				+ t.entrySet().stream()
						.sorted((lhs, rhs) -> String.valueOf(lhs.getKey().getName()).compareToIgnoreCase(rhs.getKey().getName()))
						.map(spriteAndVariables -> "typedef enum {\n"
								+ spriteAndVariables.getValue().stream()
										.map(variable -> "    Sprite"
												+ Names.normalizeName(spriteAndVariables.getKey(), Names::toPascalCase)
												+ "Variable"
												+ Names.toPascalCase(variable)
												+ ",\n"
										)
										.collect(Collectors.joining())
								+ "} Sprite"
								+ Names.normalizeName(spriteAndVariables.getKey(), Names::toPascalCase)
								+ "Variable;\n\n")
						.collect(Collectors.joining())
				+ "#endif /* spritenames_h */\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(Map<Sprite, Set<String>> t) {
		return "spritevariables.h";
	}

}
