package fr.rca.mapmaker.model.map;

import java.awt.Dimension;
import java.awt.Rectangle;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class TileLayerTest {
	
	/**
	 * Test of setName method, of class TileLayer.
	 */
	@Test
	public void testSetName() {
		System.out.println("setName");
		String name = "test";
		TileLayer instance = new TileLayer();
		instance.setName(name);
		Assert.assertEquals(name, instance.toString());
	}

	@Test
	public void testCopy() {
		System.out.println("copy");
		
		final TileLayer ref = new TileLayer(3, 3);
		ref.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		final TileLayer instance = new TileLayer(ref);
		
		Assert.assertEquals(ref.getWidth(), instance.getWidth());
		Assert.assertEquals(ref.getHeight(), instance.getHeight());
		Assert.assertArrayEquals(ref.copyData(), instance.copyData());
		
		final TileLayer instance2 = new TileLayer(ref.copyData(), new Dimension(3, 3), new Rectangle(1, 0, 2, 3));
		
		Assert.assertEquals(2, instance2.getWidth());
		Assert.assertEquals(3, instance2.getHeight());
		Assert.assertArrayEquals(new int[] {
			2, 3,
			5, 6,
			8, 9
		}, instance2.copyData());
	}

	@Test
	public void testCopyAndTranslate() {
		System.out.println("copyAndTranslate");
		
		final TileLayer ref = new TileLayer(3, 3);
		ref.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		final TileLayer largerInstance = new TileLayer(4, 4);
		largerInstance.copyAndTranslate(ref, 0, 0);
		
		Assert.assertEquals(4, largerInstance.getWidth());
		Assert.assertEquals(4, largerInstance.getHeight());
		Assert.assertArrayEquals(new int[] {
			 1,  2,  3, -1,
			 4,  5,  6, -1,
			 7,  8,  9, -1,
			-1, -1, -1, -1
		}, largerInstance.copyData());
		
		largerInstance.copyAndTranslate(ref, 1, 1);
		Assert.assertArrayEquals(new int[] {
			-1, -1, -1, -1,
			-1,  1,  2,  3,
			-1,  4,  5,  6,
			-1,  7,  8,  9
		}, largerInstance.copyData());
		
		final TileLayer smallerInstance = new TileLayer(2, 2);
		smallerInstance.copyAndTranslate(ref, 0, 0);
		
		Assert.assertEquals(2, smallerInstance.getWidth());
		Assert.assertEquals(2, smallerInstance.getHeight());
		Assert.assertArrayEquals(new int[] {
			1, 2,
			4, 5
		}, smallerInstance.copyData());
	}
}
