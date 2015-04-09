package fr.rca.mapmaker.operation;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.common.Streams;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.util.ArrayDeque;
import java.util.List;


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
		final ArrayDeque<Double> stack = new ArrayDeque<Double>();
		
		for(final Instruction instruction : instructions) {
			instruction.execute(x, stack);
		}
		
		// Renvoi du résultat
		if(stack.isEmpty()) {
			return 0.0;
		} else {
			return stack.peek();
		}
	}
	
	public void setZoom(double zoom) {
		for(final Instruction instruction : instructions) {
			if(instruction instanceof Zoom) {
				((Zoom) instruction).setZoom(zoom);
			}
		}
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
			}
		}
		
		return outputStream.toByteArray();
	}
	
	@Override
	public String toString() {
		final StringBuilder stringBuilder = new StringBuilder();
		
		for(final Instruction instruction : instructions) {
			stringBuilder.append(instruction).append(' ');
		}
		
		if(stringBuilder.length() > 0) {
			stringBuilder.setLength(stringBuilder.length() - 1);
		}
		
		return stringBuilder.toString();
	}
}
