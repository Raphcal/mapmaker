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
			"sprite.Direction = LeftDirection\n"
			+ "sprite.Variables[\"Angle\"] = -3.0 * PI / 4.0\n"
			+ "sprite.Hitbox.Top = Zoom(3)\n"
			+ "test=meuh");
		
		System.out.println(operation);
		
		Assert.assertNotNull(operation);
	}
	
}
