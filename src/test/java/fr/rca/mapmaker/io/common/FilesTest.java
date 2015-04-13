package fr.rca.mapmaker.io.common;

import java.io.File;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class FilesTest {
	
	/**
	 * Test of getRelativePath method, of class Files.
	 */
	@Test
	public void testGetRelativePath() {
		System.out.println("getRelativePath");
		final File parent = new File("/tmp/mapmaker/repo");
		final File child = new File("/tmp/mapmaker/repo/dev/mmk/Map.mmk");
		final String expResult = "dev/mmk/Map.mmk";
		final String result = Files.getRelativePath(parent, child);
		assertEquals(expResult, result);
	}
	
}
