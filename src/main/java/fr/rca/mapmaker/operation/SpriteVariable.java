package fr.rca.mapmaker.operation;

import fr.rca.mapmaker.model.sprite.Instance;
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
	public void execute(double x, Deque<Double> stack, Instance instance) {
		instance.getVariables().put(name, stack.pop());
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
