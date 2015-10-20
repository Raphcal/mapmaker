package fr.rca.mapmaker.io.bundle;

import fr.rca.mapmaker.io.plist.Plists;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.nio.charset.Charset;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import junit.framework.Assert;
import org.junit.Test;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PlistsTest {
	
	/**
	 * Test of write method, of class Plists.
	 * @throws java.io.IOException If the test fails.
	 */
	@Test
	public void testWriteEmptyMap() throws IOException {
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
	 * @throws java.io.IOException If the test fails.
	 */
	@Test
	public void testWriteArray() throws IOException {
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
	
	/**
	 * Test of read method, of class Plists.
	 * @throws java.io.IOException If the test fails.
	 */
	@Test
	public void testRead() throws IOException {
		System.out.println("read");
		
		final Map<String, Object> expected = new LinkedHashMap<String, Object>();
		expected.put("list", Arrays.asList("A", "B", "C"));
		
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n"
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
			+ "</plist>").getBytes(Charset.forName("UTF-8")));
		final Map<String, Object> result = Plists.read(inputStream);
		
		Assert.assertEquals(expected.size(), result.size());
		Assert.assertNotNull(result.get("list"));
		Assert.assertTrue(result.get("list") instanceof List);
		
		final List<String> expectedList = (List<String>) expected.get("list");
		final List<String> resultList = (List<String>) result.get("list");
		Assert.assertEquals(expectedList.size(), resultList.size());
		Assert.assertEquals(expectedList.get(0), resultList.get(0));
		Assert.assertEquals(expectedList.get(1), resultList.get(1));
		Assert.assertEquals(expectedList.get(2), resultList.get(2));
	}
}
