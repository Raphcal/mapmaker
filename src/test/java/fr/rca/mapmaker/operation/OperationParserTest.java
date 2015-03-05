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
		final Operation operation = OperationParser.parse("2 * (x + 4) + 7");
		Assert.assertEquals(2.0 * (5.0 + 4.0) + 7.0, operation.execute(5));
	}
	
	@Test
	public void testFunctions() {
		final Operation operation = OperationParser.parse("2 * cos(x) + 7");
		Assert.assertEquals(2.0 * Math.cos(5.0) + 7.0, operation.execute(5));
	}
	
	@Test
	public void testPi() {
		final Operation operation = OperationParser.parse("2 * pi + 7");
		Assert.assertEquals(2.0 * Math.PI + 7.0, operation.execute(5));
	}
	
	@Test
	public void test2ArgsFunction() {
		final Operation operation = OperationParser.parse("2 * min(7, 6) + 7");
		Assert.assertEquals(2.0 * 6.0 + 7.0, operation.execute(5));
	}
}
