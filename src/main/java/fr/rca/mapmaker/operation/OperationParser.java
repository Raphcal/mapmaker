package fr.rca.mapmaker.operation;

import static fr.rca.mapmaker.operation.Instructions.INSTRUCTIONS;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Instancie des objets {@link Operation} à partir de leur représentation sous
 * forme de texte.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class OperationParser {

	/**
	 * Début d'un bloc d'opération.
	 */
	public static final char BLOCK_START = '(';
	/**
	 * Fin d'un bloc d'opération.
	 */
	public static final char BLOCK_END = ')';
	/**
	 * Séparateur d'arguments.
	 */
	public static final char ARGUMENT_SEPARATOR = ',';
	/**
	 * Fin d'un bloc d'opération.
	 */
	public static final char MINUS = '-';
	/**
	 * Opérateur de multiplication.
	 */
	public static final char MULTIPLY_INSTRUCTION = '*';

	/**
	 * États possibles lors du traitement d'une opération.
	 *
	 * @author Raphaël Calabro (rcalabro@ideia.fr)
	 */
	private enum State {
		/**
		 * État initial.
		 */
		INITIAL,
		/**
		 * Lecture d'une constante.
		 */
		CONSTANT,
		/**
		 * Lecture du nom d'une variable.
		 */
		VARIABLE,
		/**
		 * Lecture d'un opérateur.
		 */
		OPERATOR,
		/**
		 * Attente d'un opérateur.
		 */
		WAITING_FOR_OPERATOR,
		/**
		 * Attente d'un début de bloc.
		 */
		WAITING_FOR_BLOCK,
		/**
		 * Traitement du bloc d'une fonction.
		 */
		FUNCTION_BLOCK,
		/**
		 * Fin.
		 */
		RETURN
	}

	private HashMap<String, Instruction> instructions;

	/**
	 * Cet objet n'est pas instantiable, utilisez directement ses méthodes.
	 */
	public OperationParser() {
		this.instructions = new HashMap<>(Instructions.INSTRUCTIONS);
	}

	/**
	 * Créé une opération à partir de sa représentation sous forme de texte.
	 *
	 * @param operation Opération sous forme de texte.
	 * @return Un objet {@link Operation}.
	 */
	public static @NotNull Operation parse(@Nullable String operation) {
		if (operation == null || operation.trim().isEmpty()) {
			return new Operation();
		}

		final List<Instruction> instructions = new ArrayList<>();
		parse(operation, 0, null, instructions);
		return new Operation(instructions);
	}

	/**
	 * Créé une opération à partir de sa représentation sous forme de texte.
	 *
	 * @param operation Opération sous forme de texte.
	 * @return Un objet {@link Operation}.
	 */
	public @NotNull Operation parseOperation(@Nullable String operation) {
		if (operation == null || operation.trim().isEmpty()) {
			return new Operation();
		}

		final List<Instruction> result = new ArrayList<>();
		parseOperation(operation, 0, null, result);
		return new Operation(result);
	}

	public void putInstruction(String name, Instruction instruction) {
		instructions.put(name, instruction);
	}

	/**
	 * Fait un décalage de l'opération donnée.
	 *
	 * @param operation Opération à décaler.
	 * @param x Décalage horizontal.
	 * @param y Décalage vertical.
	 * @return L'opération décalée.
	 */
	public static String shift(@Nullable String operation, int x, int y) {
		if (operation == null || operation.trim().isEmpty()) {
			return null;
		}

		String result = operation;

		if (x > 0) {
			result = result.replace("x", "(x + " + x + ")")
					.replace("y", "(y + " + x + ")");
		} else if (x < 0) {
			result = result.replace("x", "(x - " + Math.abs(x) + ")")
					.replace("y", "(y - " + Math.abs(x) + ")");
		}

		if (y > 0) {
			result += " - " + y;
		} else if (y < 0) {
			result += " + " + Math.abs(y);
		}

		return result;
	}

	/**
	 * Analyse un morceau d'une opération.
	 *
	 * @param operation Texte complet de l'opération.
	 * @param startIndex Indice de début à traiter.
	 * @param parent L'opérateur qui a conduit à exécuter cette méthode.
	 * @param instructions Liste d'instructions de l'opération à retourner.
	 * @return Dernier indice traité par cette méthode.
	 */
	public static int parse(String operation, int startIndex, Operator parent, List<Instruction> instructions) {
		OperationParser operationParser = new OperationParser();
		return operationParser.parseOperation(operation, startIndex, parent, instructions);
	}

	/**
	 * Analyse un morceau d'une opération.
	 *
	 * @param operation Texte complet de l'opération.
	 * @param startIndex Indice de début à traiter.
	 * @param parent L'opérateur qui a conduit à exécuter cette méthode.
	 * @param instructions Liste d'instructions de l'opération à retourner.
	 * @return Dernier indice traité par cette méthode.
	 */
	private int parseOperation(String operation, int startIndex, Operator parent, List<Instruction> instructions) {
		int index = startIndex;

		// Etat du parser
		State state = State.INITIAL;

		// Constructeur de mots
		final StringBuilder itemBuilder = new StringBuilder();

		boolean waitingForNegative = false;

		// Traitement de l'opération
		for (; state != State.RETURN && index <= operation.length(); index++) {

			// Lecture de la lettre courante 
			char c;
			if (index == operation.length()) {
				c = '\n';
			} else {
				c = operation.charAt(index);
			}

			switch (state) {

				// Début de la lecture
				case INITIAL:
					if (c == BLOCK_START) {
						index = parse(operation, index + 1, null, instructions);
						state = State.WAITING_FOR_OPERATOR;

					} else if (c == BLOCK_END || c == ARGUMENT_SEPARATOR) {
						index--;
						state = State.RETURN;
						// Erreur ?
						throw new IllegalArgumentException("Parenthèse fermante seule.");

					} else if (c == MINUS) {
						// Opérateur unaire
						waitingForNegative = true;

					} else if (!isWhitespace(c)) {
						itemBuilder.append(c);

						// Début d'une variable
						if (c >= 'a' && c <= 'z') {
							state = State.VARIABLE;

							// Début d'une constante
						} else {
							state = State.CONSTANT;
						}
					}
					break;

				// Lecture d'une constante
				case CONSTANT:
					if (c >= 'a' && c <= 'z') {
						instructions.add(new Constant(Double.valueOf(itemBuilder.toString())));
						itemBuilder.setLength(0);
						itemBuilder.append(MULTIPLY_INSTRUCTION);
						index--;
						state = State.OPERATOR;

					} else if ((c >= '0' && c <= '9') || c == '.') {
						itemBuilder.append(c);

					} else {
						instructions.add(new Constant(Double.valueOf(itemBuilder.toString())));
						itemBuilder.setLength(0);

						index--;

						if (c == BLOCK_END || c == ARGUMENT_SEPARATOR) {
							state = State.RETURN;

						} else {
							state = State.WAITING_FOR_OPERATOR;
						}
					}
					break;

				// Lecture d'une variable
				case VARIABLE:
					if (Character.isAlphabetic(c) || Character.isDigit(c) || c == '.' || c == '_') {
						itemBuilder.append(c);

					} else {
						final Instruction instruction = getInstruction(itemBuilder.toString());

						if (instruction instanceof Function) {
							if (c == BLOCK_START) {
								state = State.FUNCTION_BLOCK;

							} else if (c == BLOCK_END || c == ARGUMENT_SEPARATOR) {
								throw new IllegalArgumentException("Parenthèse invalide à l'emplacement " + index + " : '" + c + "'. Début de bloc attendu.");

							} else {
								state = State.WAITING_FOR_BLOCK;
							}

						} else if (instruction instanceof Variable || instruction instanceof Constant) {
							instructions.add(instruction);
							itemBuilder.setLength(0);

							index--;

							if (c == BLOCK_END || c == ARGUMENT_SEPARATOR) {
								state = State.RETURN;
							} else {
								state = State.WAITING_FOR_OPERATOR;
							}
						}
					}
					break;

				// Lecture d'un opérateur
				case OPERATOR:
					final Operator operator = (Operator) Instructions.getInstruction(itemBuilder.toString());

					if (operator == null) {
						throw new IllegalArgumentException("À l'index " + index + ", opérateur inconnu (" + itemBuilder + ").");
					}

					final int parentPriority = parent == null ? -1 : parent.getPriority().ordinal();
					final int thisPriority = operator.getPriority().ordinal() - 1;

					if (parentPriority > thisPriority) {
						instructions.add(parent);
						parent = null;
					}

					index = parse(operation, index, operator, instructions);

					if (parent != null) {
						instructions.add(parent);
						parent = null;
					}

					itemBuilder.setLength(0);

					index--;
					state = State.RETURN;
					break;

				// Attente d'un opérateur
				case WAITING_FOR_OPERATOR:
					if (waitingForNegative) {
						instructions.add(new Negative());
						waitingForNegative = false;
					}

					if (c == BLOCK_END || c == ARGUMENT_SEPARATOR) {
						index--;
						state = State.RETURN;

					} else if (!isWhitespace(c)) {
						itemBuilder.append(c);
						state = State.OPERATOR;
					}
					break;

				// Attente d'un bloc
				case WAITING_FOR_BLOCK:
					if (c == BLOCK_START) {
						state = State.FUNCTION_BLOCK;

					} else if (!isWhitespace(c)) {
						throw new IllegalArgumentException("Lettre invalide à l'emplacement " + index + " : '" + c + "'. Début de bloc attendu.");
					}
					break;

				// Traitement d'une fonction
				case FUNCTION_BLOCK:
					final Function function = (Function) Instructions.getInstruction(itemBuilder.toString());

					if (function == null) {
						throw new IllegalArgumentException("À l'index " + index + ", fonction inconnue (" + itemBuilder + ").");
					}

					for (int argument = 0; argument < function.getNumberOfArguments(); argument++) {
						index = parse(operation, index, null, instructions);

						if (argument + 1 < function.getNumberOfArguments()) {
							index++;
						}
					}

					instructions.add(function);
					itemBuilder.setLength(0);

					state = State.WAITING_FOR_OPERATOR;
					break;

				default:
					break;
			}
		}

		if (parent != null) {
			instructions.add(parent);
		}

		if (waitingForNegative) {
			instructions.add(new Negative());
		}

		return index;
	}

	private static boolean isWhitespace(char c) {
		return c == ' '
				|| c == '\r'
				|| c == '\n'
				|| c == '\t';
	}

	private Instruction getInstruction(String name) {
		return instructions.get(name.toLowerCase());
	}
}
