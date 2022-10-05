package fr.rca.mapmaker.operation;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.sprite.Instance;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.Collections;
import java.util.Deque;
import java.util.List;
import java.util.Map;


/**
 * Représente une opération.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * 
 */
public class Operation {
	
	/**
	 * La liste d'instructions à exécuter.
	 */
	private final List<Instruction> instructions;

	public Operation() {
		this.instructions = Collections.emptyList();
	}

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
	public double execute(double x) {
		final Deque<Double> stack = new ArrayDeque<Double>();

		for(final Instruction instruction : instructions) {
			instruction.execute(x, stack, null);
		}

		// Renvoi du résultat
		if(stack.isEmpty()) {
			return 0.0;
		} else {
			return stack.peek();
		}
	}
	
	/**
	 * Exécute cette opération pour l'instance donnée.
	 * @param instance Instance à modifier.
	 */
	public void execute(final Instance instance) {
		final Deque<Double> stack = new ArrayDeque<Double>() {

			@Override
			public Double pop() {
				if (!isEmpty()) {
					return super.pop();
				} else {
					return 1.0;
				}
			}
			
		};
		
		for(final Instruction instruction : instructions) {
			instruction.execute(0, stack, instance);
		}
	}

	@Deprecated
	public void setZoom(double zoom) {
		// Vide
	}

	public List<Instruction> getInstructions() {
		return instructions;
	}
	
	public byte[] toByteArray() {
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		for(final Instruction instruction : instructions) {
			final ByteCode byteCode = instruction.toByteCode();
			outputStream.write(byteCode.getByte());
			
			if(byteCode == ByteCode.CONSTANT) {
				try {
					Streams.write((float)((Constant)instruction).getValue(), outputStream);
					
				} catch (IOException ex) {
					// Ne peut pas arriver avec ByteArrayOutputStream.
					Exceptions.showStackTrace(ex, null);
				}
			} else if(byteCode == ByteCode.SPRITE_VARIABLE) {
				try {
					Streams.write(((SpriteVariable)instruction).getName(), outputStream);
					
				} catch (IOException ex) {
					// Ne peut pas arriver avec ByteArrayOutputStream.
					Exceptions.showStackTrace(ex, null);
				}
			}
		}
		
		return outputStream.toByteArray();
	}

	@Override
	public String toString() {
		return toString(Language.GENERIC);
	}

	public String toString(Language language) {
		final ArrayDeque<String> stack = new ArrayDeque<>();
		for(final Instruction instruction : instructions) {
			instruction.pushString(stack, language);
		}
		return stack.pop();
	}

	public String toString(Language language, Map<ByteCode, String> replacements) {
		final ArrayDeque<String> stack = new ArrayDeque<>();
		for(final Instruction instruction : instructions) {
			String replacement = replacements.get(instruction.toByteCode());
			if (replacement == null) {
				instruction.pushString(stack, language);
			} else {
				stack.push(replacement);
			}
		}
		return stack.pop();
	} 
}
