/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package fr.rca.mapmaker.io.common;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import org.junit.Test;
import org.junit.Assert;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class StreamsTest {
	
	@Test
	public void testInt() throws IOException {
		
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

		outputStream.reset();
		Streams.write(-1, outputStream);
		Assert.assertEquals(-1, Streams.readInt(new ByteArrayInputStream(outputStream.toByteArray())));
	}
}
