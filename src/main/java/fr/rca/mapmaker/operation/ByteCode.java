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
	POW('^'),
	NEGATIVE('n'),
	CONSTANT('C'),
	X('x'),
	PI('p'),
	E('e'),
	MINIMUM('m'),
	MAXIMUM('M'),
	COSINUS('c'),
	SINUS('s'),
	SQUARE_ROOT('S'),
	ZOOM('z'),
	SPRITE_VARIABLE('v'),
	SPRITE_DIRECTION('d'),
	SPRITE_ANIMATION('a'),
	SPRITE_HITBOX_TOP('h'),;
	
	private final byte b;

	private ByteCode(char b) {
		this.b = (byte) b;
	}

	public byte getByte() {
		return b;
	}
	
	public String nameCapitalized() {
        final String[] parts = name().toLowerCase().split("_");
        final StringBuilder stringBuilder = new StringBuilder(parts[0]);
        for (int index = 1; index < parts.length; index++) {
            final String part = parts[index];
            stringBuilder.append(Character.toUpperCase(part.charAt(0)))
                    .append(part.substring(1));
        }
		return stringBuilder.toString();
	}
	
	public static void main(String[] args) {
		System.out.println("// Swift");
		for(final ByteCode byteCode : values()) {
			System.out.println(String.format("case %s = 0x%x", byteCode.nameCapitalized(), byteCode.getByte()));
		}
		
		System.out.print("let operation : [UInt8] = [");
		for(final byte b : OperationParser.parse("sin((pi / 2) * min(max(x, 0), 1)) ^ 2").toByteArray()) {
			System.out.print(String.format("0x%x, ", b));
		}
		System.out.println("]");
	}
}
