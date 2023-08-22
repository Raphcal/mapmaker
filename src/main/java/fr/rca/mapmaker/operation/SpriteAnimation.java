package fr.rca.mapmaker.operation;

import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import java.util.Deque;

/**
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class SpriteAnimation implements Instruction {

	@Override
	public void execute(double x, Deque<Double> stack, Instance instance) {
		Project project = instance.getProject();
		int index = stack.pop().intValue();
		String animationName = project.getAnimationNames().get(index);
		instance.setAnimationName(animationName);
	}

	@Override
	public ByteCode toByteCode() {
		return ByteCode.SPRITE_ANIMATION;
	}

}
