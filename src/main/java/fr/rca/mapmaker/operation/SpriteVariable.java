package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class SpriteVariable implements Instruction {
	
	private String name;

	public SpriteVariable() {
	}

	public SpriteVariable(String name) {
		this.name = name;
	}
	
	@Override
	public void execute(double x, Deque<Double> stack) {
		throw new UnsupportedOperationException("Not supported.");
	}

	public String getName() {
		return name;
	}
	
	@Override
	public String toString() {
		return "sprite.Variables[\"" + name + "\"]";
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.SPRITE_VARIABLE;
	}
	
}
