package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.io.common.Streams;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author daeke
 */
public class ColorPaletteDataHandlerTest {
	
	@Test
	public void testStreams() throws Exception {
		System.out.println("streams");
		

		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		Streams.write((int) 1, outputStream);
		Assert.assertArrayEquals(new byte[] {0x01, 0x00, 0x00, 0x00}, outputStream.toByteArray());
		Assert.assertEquals((int) 1, Streams.readInt(new ByteArrayInputStream(outputStream.toByteArray())));
		
		outputStream.reset();
		Streams.write((int) 255, outputStream);
		Assert.assertArrayEquals(new byte[] {(byte) 0xFF, 0x00, 0x00, 0x00}, outputStream.toByteArray());
		Assert.assertEquals((int) 255, Streams.readInt(new ByteArrayInputStream(outputStream.toByteArray())));
		
		outputStream.reset();
		Streams.write((int) 256, outputStream);
		Assert.assertArrayEquals(new byte[] {0x00, 0x01, 0x00, 0x00}, outputStream.toByteArray());
		Assert.assertEquals((int) 256, Streams.readInt(new ByteArrayInputStream(outputStream.toByteArray())));
		
		outputStream.reset();
		Streams.write((int) 65536, outputStream);
		Assert.assertArrayEquals(new byte[] {0x00, 0x00, 0x01, 0x00}, outputStream.toByteArray());
		Assert.assertEquals((int) 65536, Streams.readInt(new ByteArrayInputStream(outputStream.toByteArray())));
		
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(new byte[] {0x01, 0x00, 0x01, 0x00});
		Assert.assertEquals(65537, Streams.readInt(inputStream));
	}
	

	/**
	 * Test of read method, of class ColorPaletteDataHandler.
	 */
	@Test
	public void testWriteRead() throws Exception {
		System.out.println("writeRead");
		
		final ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
		
		final ColorPaletteDataHandler instance = new ColorPaletteDataHandler(new InternalFormat());
		final ColorPalette palette = ColorPalette.getDefaultColorPalette();
		
		instance.write(palette, outputStream);
		
		outputStream.close();
		
		final ByteArrayInputStream inputStream = new ByteArrayInputStream(outputStream.toByteArray());
		
		final ColorPalette result = instance.read(inputStream);
		
		inputStream.close();
		
		Assert.assertEquals(palette.size(), result.size());
		
		for(int index = 0; index < palette.size(); index++) {
			Assert.assertEquals(palette.getColors()[index], result.getColors()[index]);
		}
	}
}
