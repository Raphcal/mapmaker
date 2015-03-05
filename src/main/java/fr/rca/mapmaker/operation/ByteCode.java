package fr.rca.mapmaker.operation;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public enum ByteCode {
	ADD('+'),
	SUBSTRACT('-'),
	MULTIPLY('*'),
	DIVIDE('/'),
	NEGATIVE('n'),
	CONSTANT('"'),
	X('x'),
	PI('p'),
	E('e'),
	MINIMUM('m'),
	MAXIMUM('M'),
	COSINUS('c'),
	SINUS('s');
	
	private final byte b;

	private ByteCode(char b) {
		this.b = (byte) b;
	}

	public byte getByte() {
		return b;
	}
	
}
