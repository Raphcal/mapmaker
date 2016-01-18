package fr.rca.mapmaker.operation;

import fr.rca.mapmaker.model.sprite.Direction;
import java.util.Deque;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class SpriteDirection implements Instruction {

	@Override
	public void execute(double x, Deque<Double> stack, fr.rca.mapmaker.model.sprite.Instance instance) {
		instance.setDirection(Direction.from(stack.pop()));
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
