package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class SpriteDirection implements Instruction {

	@Override
	public void execute(double x, Deque<Double> stack) {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String toString() {
		return "sprite.Direction";
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.SPRITE_DIRECTION;
	}
	
}
