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
	
	@Test
	public void testMultipleFunctions() {
		final Operation operation = OperationParser.parse("min(x * 2, 6) * (cos(max(x, pi)) + 7)");
		Assert.assertEquals(Math.min(5.0 * 2.0, 6.0) * (Math.cos(Math.max(5.0, Math.PI)) + 7.0), operation.execute(5));
		Assert.assertEquals(Math.min(2.0 * 2.0, 6.0) * (Math.cos(Math.max(2.0, Math.PI)) + 7.0), operation.execute(2));
	}
	
	@Test
	public void testJustVariable() {
		final Operation operation = OperationParser.parse("x");
		Assert.assertEquals(5.0, operation.execute(5));
	}
	
	@Test
	public void testJustConstant() {
		final Operation operation = OperationParser.parse("12");
		Assert.assertEquals(12.0, operation.execute(5));
	}
	
	@Test
	public void testJustPi() {
		final Operation operation = OperationParser.parse("pi");
		Assert.assertEquals(Math.PI, operation.execute(5));
	}
	
	@Test
	public void testNegativeSimple() {
		final Operation operation = OperationParser.parse("-pi / 4");
		Assert.assertEquals(-Math.PI / 4.0, operation.execute(5));
	}
	
	@Test
	public void testNegativeSimple2() {
		final Operation operation = OperationParser.parse("-1 / 4");
		Assert.assertEquals(-1.0 / 4.0, operation.execute(5));
	}
	
	@Test
	public void testNegative() {
		final Operation operation = OperationParser.parse("-x + 1 / 4");
		Assert.assertEquals(-5.0 + 1.0 / 4.0, operation.execute(5));
	}
	
	@Test
	public void testNegativeBlock() {
		final Operation operation = OperationParser.parse("-(x + -1 / 4)");
		Assert.assertEquals(-(5.0 + -1.0 / 4.0), operation.execute(5));
	}
	
	@Test
	public void testWithoutSpaces() {
		final Operation operation = OperationParser.parse("-3.0*pi/4.0");
		Assert.assertEquals(-3.0 * Math.PI / 4.0, operation.execute(5));
	}
	
	@Test
	public void testShift() {
		Assert.assertEquals("x * 2", OperationParser.shift("x * 2", 0, 0));
		Assert.assertEquals("(x + zoom(32)) * 2", OperationParser.shift("x * 2", 32, 0));
		Assert.assertEquals("(x - zoom(32)) * 2 - zoom(32)", OperationParser.shift("x * 2", -32, 32));
		Assert.assertEquals("x * 2 + zoom(32)", OperationParser.shift("x * 2", 0, -32));
	}
	
}
