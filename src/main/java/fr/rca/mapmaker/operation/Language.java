package fr.rca.mapmaker.operation;

import fr.rca.mapmaker.util.MapBuilder;
import java.util.Collections;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public enum Language {
	GENERIC,

	C (MapBuilder.createMap(translations -> translations
			.put(ByteCode.COSINUS, "cosf")
			.put(ByteCode.SINUS, "sinf")
			.put(ByteCode.SQUARE_ROOT, "sqrtf")
			.put(ByteCode.POW, "powf")
			.put(ByteCode.PI, "M_PI")
			.put(ByteCode.E, "M_E")
	), MapBuilder.createMap(priorities -> priorities
			.put(ByteCode.POW, Operator.Priority.FUNCTION)
	)) {
		@Override
		public String translate(Instruction instruction) {
			final ByteCode byteCode = instruction.toByteCode();
			if (byteCode == ByteCode.CONSTANT) {
				final double value = ((Constant)instruction).getValue();
				if (value != Math.floor(value)) {
					return Double.toString(value) + 'f';
				}
			}
			return super.translate(instruction);
		}
	},

	SWIFT (MapBuilder.createMap(translations -> translations
			.put(ByteCode.PI, ".pi")
			.put(ByteCode.E, ".e")
	), MapBuilder.createMap(priorities -> priorities
			.put(ByteCode.POW, Operator.Priority.FUNCTION)
	));

	private final Map<ByteCode, String> translations;
	private final Map<ByteCode, Operator.Priority> priorities;

	private Language() {
		this.translations = Collections.emptyMap();
		this.priorities = Collections.emptyMap();
	}

	private Language(Map<ByteCode, String> translations, Map<ByteCode, Operator.Priority> priorities) {
		this.translations = translations;
		this.priorities = priorities;
	}

	public String translate(@NotNull Instruction instruction) {
		final String translation = translations.get(instruction.toByteCode());
		return translation != null ? translation : instruction.toString();
	}

	public Operator.Priority priority(@NotNull Operator operator) {
		final Operator.Priority priority = priorities.get(operator.toByteCode());
		return priority != null ? priority : operator.getPriority();
	}
}
