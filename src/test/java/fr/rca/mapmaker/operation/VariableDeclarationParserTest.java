package fr.rca.mapmaker.operation;

import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class VariableDeclarationParserTest {
	
	/**
	 * Test of parse method, of class VariableDeclarationParser.
	 */
	@Test
	public void testParse() {
		final Operation operation = VariableDeclarationParser.parse(
			  "function Load(sprite)\n"
			+ "  sprite.Direction = LeftDirection\n"
			+ "  sprite.Variables[\"Angle\"] = -3.0 * PI / 4.0\n"
			+ "  test=meuh\n"
			+ "  sprite.Hitbox.Top = Zoom(3.0)\n"
			+ "end");
		Assert.assertNotNull(operation);
		
		System.out.println(operation);
		
		final Class[] classes = new Class[] {
			Constant.class, SpriteDirection.class, 
			Constant.class, Negative.class, Constant.class, Multiply.class, Constant.class, Divide.class, SpriteVariable.class,
			Constant.class, Zoom.class, SpriteHitboxTop.class
		};
		
		for(int index = 0; index < operation.getInstructions().size(); index++) {
			Assert.assertEquals(classes[index], operation.getInstructions().get(index).getClass());
		}
	}
	
}
