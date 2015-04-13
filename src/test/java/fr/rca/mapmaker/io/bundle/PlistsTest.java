package fr.rca.mapmaker.io.bundle;

import java.io.ByteArrayOutputStream;
import java.io.OutputStream;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PlistsTest {
	
	/**
	 * Test of write method, of class Plists.
	 */
	@Test
	public void testWriteEmptyMap() throws Exception {
		System.out.println("writeEmptyMap");
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Plists.write(values, outputStream);
		
		Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
			+ "<plist version=\"1.0\">\n"
			+ "<dict>\n"
			+ "</dict>\n"
			+ "</plist>", outputStream.toString("UTF-8"));
	}
	
	/**
	 * Test of write method, of class Plists.
	 */
	@Test
	public void testWriteArray() throws Exception {
		System.out.println("writeArray");
		Map<String, Object> values = new LinkedHashMap<String, Object>();
		
		values.put("list", Arrays.asList("A", "B", "C"));
//		values.put("deux", 2);
//		values.put("deuxL", 2L);
//		values.put("pi", 3.14);
//		values.put("piF", 3.14f);
		
		ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		Plists.write(values, outputStream);
		
		Assert.assertEquals("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
			+ "<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n"
			+ "<plist version=\"1.0\">\n"
			+ "<dict>\n"
			+ "\t<key>list</key>\n"
			+ "\t<array>\n"
			+ "\t\t<string>A</string>\n"
			+ "\t\t<string>B</string>\n"
			+ "\t\t<string>C</string>\n"
			+ "\t</array>\n"
			+ "</dict>\n"
			+ "</plist>", outputStream.toString("UTF-8"));
	}
	
}
