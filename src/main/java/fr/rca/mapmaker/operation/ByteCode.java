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
	CONSTANT('C'),
	X('x'),
	PI('p'),
	E('e'),
	MINIMUM('m'),
	MAXIMUM('M'),
	COSINUS('c'),
	SINUS('s'),
	ZOOM('z'),
	SPRITE_VARIABLE('v'),
	SPRITE_DIRECTION('d');
	
	private final byte b;

	private ByteCode(char b) {
		this.b = (byte) b;
	}

	public byte getByte() {
		return b;
	}
	
	public String nameCapitalized() {
		return name().substring(0, 1) + name().substring(1).toLowerCase();
	}
	
	public static void main(String[] args) {
		System.out.println("// Swift");
		for(final ByteCode byteCode : values()) {
			System.out.println(String.format("case %s = 0x%x", byteCode.nameCapitalized(), byteCode.getByte()));
		}
		
		System.out.print("let operation : [UInt8] = [");
		for(final byte b : OperationParser.parse("(x * 45678) / 2").toByteArray()) {
			System.out.print(String.format("0x%x, ", b));
		}
		System.out.println("]");
	}
}
