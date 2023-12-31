package fr.rca.mapmaker.operation;

import java.util.Deque;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class SpriteHitboxTop implements Instruction {
	
	@Override
	public void execute(double x, Deque<Double> stack, fr.rca.mapmaker.model.sprite.Instance instance) {
		// Sans effet.
	}

	@Override
	public String toString() {
		return "sprite.Hitbox.Top";
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.SPRITE_HITBOX_TOP;
	}
	
}
