package fr.rca.mapmaker.operation;

import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class OperationTest {
	/**
	 * Test of toString method, of class Operation.
	 */
	@Test
	public void toString_should_use_human_readable_order() {
		Operation instance = OperationParser.parse("cos(3 * x^2 * pi / 2) * -sqrt(x * 2)");
		String expResult = "cos(3 * x ^ 2 * pi / 2) * -sqrt(x * 2)";
		String result = instance.toString();
		assertEquals(expResult, result);
	}

	/**
	 * Test of toString method, of class Operation.
	 */
	@Test
	public void toString_should_translate_operation_to_given_development_language() {
		Operation instance = OperationParser.parse("max(cos(1.5 * (x^2) * pi / 2) * -sqrt(x * 2), 0)");
		String expResult = "max(cosf(1.5f * powf(x, 2) * M_PI / 2) * -sqrtf(x * 2), 0)";
		String result = instance.toString(Language.C);
		assertEquals(expResult, result);
	}
}
