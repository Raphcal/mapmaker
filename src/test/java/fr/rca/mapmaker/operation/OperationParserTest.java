package fr.rca.mapmaker.operation;

import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class OperationParserTest {
	
	@Test
	public void testBasicOperations() {
		final Operation operation = OperationParser.parse("2 * ($x + 4) + 7");
		Assert.assertEquals(25.0, operation.execute(5));
	}
}
