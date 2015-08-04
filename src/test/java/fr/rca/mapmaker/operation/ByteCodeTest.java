package fr.rca.mapmaker.operation;

import java.util.HashSet;
import java.util.Set;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ByteCodeTest {
	
	@Test
	public void testUnicity() {
		final Set<Byte> characters = new HashSet<Byte>();
		
		for(final ByteCode byteCode : ByteCode.values()) {
			Assert.assertFalse("L'octet '" + (char) byteCode.getByte() + "' est présent en double.", characters.contains(byteCode.getByte()));
			characters.add(byteCode.getByte());
		}
	}
	
}
