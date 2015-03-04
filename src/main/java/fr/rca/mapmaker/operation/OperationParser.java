package fr.rca.mapmaker.operation;

import java.util.ArrayList;

/**
 * Instancie des objets {@link Operation} à partir de leur représentation
 * sous forme de texte.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public final class OperationParser {
	
	/**
	 * Préfixe des variables.
	 */
	public static final char VARIABLE_PREFIX = '$';
	/**
	 * Préfixe des fonctions.
	 */
	public static final char FUNCTION_PREFIX = '@';
	/**
	 * Début d'un bloc d'opération.
	 */
	public static final char BLOCK_START = '(';
	/**
	 * Fin d'un bloc d'opération.
	 */
	public static final char BLOCK_END = ')';
	/**
	 * Fin d'un bloc d'opération.
	 */
	public static final char MINUS = '-';
	/**
	 * Opérateur de multiplication.
	 */
	public static final char MULTIPLY_INSTRUCTION = '*';
	
	
	/**
	 * Etats possibles lors du traitement d'une opération.
	 * 
	 * @author Raphaël Calabro (rcalabro@ideia.fr)
	 */
	private enum State {
		/**
		 * Etat initial.
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
		 * Lecture du nom d'une référence vers une opération.
		 */
		OPERATION_REFERENCE,
		/**
		 * Lecture d'un tableau.
		 */
		FUNCTION,
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
	
	/**
	 * Cet objet n'est pas instantiable, utilisez directement ses méthodes.
	 */
	private OperationParser() {}
	
	/**
	 * Créé une opération à partir de sa représentation sous forme de texte.
	 * @param operation Opération sous forme de texte.
	 * @return Un objet {@link Operation}.
	 */
	public static Operation parse(String operation) {
		
		final ArrayList<Instruction> instructions = new ArrayList<Instruction>();
		
		parse(operation, 0, null, instructions);
		
		return new Operation(instructions);
	}
	
	/**
	 * Analyse un morceau d'une opération.
	 * @param operation Texte complet de l'opération.
	 * @param index Indice de début à traiter.
	 * @param parent L'opérateur qui a conduit à exécuter cette méthode.
	 * @param instructions Liste d'instructions de l'opération à retourner.
	 * @return Dernier indice traité par cette méthode.
	 */
	private static int parse(String operation, int startIndex, Operator parent, ArrayList<Instruction> instructions) {
		int index = startIndex;
		
		// Etat du parser
		State state = State.INITIAL;
		
		// Constructeur de mots
		final StringBuilder itemBuilder = new StringBuilder();
		
		// Traitement de l'opération
		for(; state != State.RETURN && index <= operation.length(); index++) {
			
			// Lecture de la lettre courante 
			char c;
			if(index == operation.length()) {
				c = '\n';
			} else {
				c = operation.charAt(index);
			}
			
			switch(state) {
			
			// Début de la lecture
			case INITIAL:
				if(c == BLOCK_START) {
					index = parse(operation, index+1, null, instructions);
					state = State.WAITING_FOR_OPERATOR;
					
				} else if(c == BLOCK_END) {
					index--;
					state = State.RETURN;
					// Erreur ?
					throw new IllegalArgumentException("Parenthèse fermante seule.");
					
				} else if(!isWhitespace(c)) {
					itemBuilder.append(c);
					
					// Début d'une variable
					if(c == VARIABLE_PREFIX) {
						state = State.VARIABLE;
					
					// Début d'une fonction
					} else if(c == FUNCTION_PREFIX) {
						state = State.FUNCTION;
					
					// Opérateurs unaires
					} else if(c == MINUS) {
						state = State.OPERATOR;
					
					// Début d'une constante
					} else {
						state = State.CONSTANT;
					}
				}
				break;
				
			// Lecture d'une constante
			case CONSTANT:
				if(c == VARIABLE_PREFIX) {
					instructions.add(new Constant(Double.valueOf(itemBuilder.toString())));
					itemBuilder.setLength(0);
					itemBuilder.append(MULTIPLY_INSTRUCTION);
					index--;
					state = State.OPERATOR;
				}
					
				else if(c != BLOCK_END && !isWhitespace(c)) {
					itemBuilder.append(c);
				
				} else {
					instructions.add(new Constant(Double.valueOf(itemBuilder.toString())));
					itemBuilder.setLength(0);
					
					if(c == BLOCK_END) {
						index--;
						state = State.RETURN;
					} else {
						state = State.WAITING_FOR_OPERATOR;
					}
				}
				break;
				
			// Lecture d'une variable
			case VARIABLE:
				if(c == VARIABLE_PREFIX) {
					instructions.add(new Variable(itemBuilder.toString()));
					itemBuilder.setLength(0);
					itemBuilder.append(MULTIPLY_INSTRUCTION);
					index--;
					state = State.OPERATOR;
				}
				
				else if(c != BLOCK_END && !isWhitespace(c)) {
					itemBuilder.append(c);
					
				} else {
					instructions.add(new Variable(itemBuilder.toString()));
					itemBuilder.setLength(0);
					
					if(c == BLOCK_END) {
						index--;
						state = State.RETURN;
					} else {
						state = State.WAITING_FOR_OPERATOR;
					}
				}
				break;
				
			// Lecture d'une fonction
			case FUNCTION:
				if(c == BLOCK_START) {
					state = State.FUNCTION_BLOCK;
				
				} else if(c == BLOCK_END) {
					throw new IllegalArgumentException("Parenthèse invalide à l'emplacement " + index + " : '" + c + "'. Début de bloc attendu.");
				
				} else if(!isWhitespace(c)) {
					itemBuilder.append(c);
				
				} else {
					state = State.WAITING_FOR_BLOCK;
				}
				break;
				
			// Lecture d'un opérateur
			case OPERATOR:
				final Operator operator = (Operator) Instructions.getInstruction(itemBuilder.toString());

				if(operator == null) {
					throw new IllegalArgumentException("À l'index " + index + ", opérateur inconnu (" + itemBuilder + ").");
				}

				final int parentPriority = parent == null ? -1 : parent.getPriority().ordinal();
				final int thisPriority = operator == null ? -1 : operator.getPriority().ordinal() - 1;

				if(parentPriority > thisPriority) {
					instructions.add(parent);
					parent = null;
				}

				index = parse(operation, index, operator, instructions);

				if(parent != null) {
					instructions.add(parent);
					parent = null;
				}

				itemBuilder.setLength(0);

				index--;
				state = State.RETURN;
				break;
			
			// Attente d'un opérateur
			case WAITING_FOR_OPERATOR:
				if(c == BLOCK_END) {
					index--;
					state = State.RETURN;
				}
				
				else if(!isWhitespace(c)) {
					itemBuilder.append(c);
					state = State.OPERATOR;
				}
				break;
				
			// Attente d'un bloc
			case WAITING_FOR_BLOCK:
				if(c == BLOCK_START) {
					state = State.FUNCTION_BLOCK;
				
				} else if(!isWhitespace(c)) {
					throw new IllegalArgumentException("Lettre invalide à l'emplacement " + index + " : '" + c + "'. Début de bloc attendu.");
				}
				break;
				
			// Traitement d'une fonction
			case FUNCTION_BLOCK:
				index = parse(operation, index, null, instructions);
				
				final Instruction function = Instructions.getInstruction(itemBuilder.toString());
				
				if(function == null) {
					throw new IllegalArgumentException("À l'index " + index + ", fonction inconnue (" + itemBuilder + ").");
				}
					
				instructions.add(function);
				itemBuilder.setLength(0);
				
				state = State.WAITING_FOR_OPERATOR;
				break;
				
			default:
				break;
			}
		}
		
		if(parent != null) {
			instructions.add(parent);
		}
		
		return index;
	}
	
	private static boolean isWhitespace(char c) {
		return c == ' ' ||
				c == '\r' ||
				c == '\n' ||
				c == '\t';
	}
}
