package fr.rca.mapmaker.model.map;

import java.awt.Dimension;
import java.awt.Point;
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

	/**
	 * Test of getTile method, of class TileLayer.
	 */
	@Test
	public void testGetTile_int_int() {
		System.out.println("getTile");
		TileLayer instance = new TileLayer(3, 3);
		instance.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		Assert.assertEquals(1, instance.getTile(0, 0));
		Assert.assertEquals(2, instance.getTile(1, 0));
		Assert.assertEquals(3, instance.getTile(2, 0));
		Assert.assertEquals(4, instance.getTile(0, 1));
		Assert.assertEquals(5, instance.getTile(1, 1));
		Assert.assertEquals(6, instance.getTile(2, 1));
		Assert.assertEquals(7, instance.getTile(0, 2));
		Assert.assertEquals(8, instance.getTile(1, 2));
		Assert.assertEquals(9, instance.getTile(2, 2));
	}

	/**
	 * Test of getTile method, of class TileLayer.
	 */
	@Test
	public void testGetTile_Point() {
		System.out.println("getTile");
		
		TileLayer instance = new TileLayer(3, 3);
		instance.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		Assert.assertEquals(instance.getTile(0, 0), instance.getTile(new Point(0, 0)));
		Assert.assertEquals(instance.getTile(1, 0), instance.getTile(new Point(1, 0)));
		Assert.assertEquals(instance.getTile(2, 2), instance.getTile(new Point(2, 2)));
	}

	/**
	 * Test of setTile method, of class TileLayer.
	 */
	@Test
	public void testSetTile_3args() {
		System.out.println("setTile");
		
		TileLayer instance = new TileLayer(3, 3);
		instance.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		instance.setTile(1, 1, 0);
		
		Assert.assertArrayEquals(new int[] {
			1, 2, 3,
			4, 0, 6,
			7, 8, 9
		}, instance.copyData());
	}

	/**
	 * Test of setRawTile method, of class TileLayer.
	 */
	@Test
	public void testSetRawTile() {
		System.out.println("setRawTile");
		
		TileLayer instance = new TileLayer(3, 3);
		instance.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		instance.setRawTile(2, 0, 0);
		
		Assert.assertArrayEquals(new int[] {
			1, 2, 0,
			4, 5, 6,
			7, 8, 9
		}, instance.copyData());
	}

	/**
	 * Test of setTile method, of class TileLayer.
	 */
	@Test
	public void testSetTile_Point_int() {
		System.out.println("setTile");
		
		TileLayer instance = new TileLayer(3, 3);
		instance.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		instance.setTile(new Point(1, 2), 0);
		
		Assert.assertArrayEquals(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 0, 9
		}, instance.copyData());
	}

	/**
	 * Test of getWidth method, of class TileLayer.
	 */
	@Test
	public void testGetWidth() {
		System.out.println("getWidth");
		TileLayer instance = new TileLayer(4, 6);
		int expResult = 4;
		int result = instance.getWidth();
		
		Assert.assertEquals(expResult, result);
	}

	/**
	 * Test of getHeight method, of class TileLayer.
	 */
	@Test
	public void testGetHeight() {
		System.out.println("getHeight");
		TileLayer instance = new TileLayer(4, 6);
		int expResult = 6;
		int result = instance.getHeight();
		
		Assert.assertEquals(expResult, result);
	}

	/**
	 * Test of getScrollRate method, of class TileLayer.
	 */
	@Test
	public void testGetScrollRate() {
		System.out.println("getScrollRate");
		TileLayer instance = new TileLayer(4, 6);
		Assert.assertEquals(1.0f, instance.getScrollRate(), 0.0001f);
		
		instance.setScrollRate(0.75f);
		float expResult = 0.75f;
		float result = instance.getScrollRate();
		
		Assert.assertEquals(expResult, result, 0.0001f);
	}

	/**
	 * Test of isEmpty method, of class TileLayer.
	 */
	@Test
	public void testIsEmpty() {
		System.out.println("isEmpty");
		TileLayer instance = new TileLayer(3, 3);
		
		Assert.assertTrue(instance.isEmpty());
		
		instance.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		Assert.assertFalse(instance.isEmpty());
		
		instance.restoreData(new int[] {
			-1, -1, -1,
			-1, -1, -1,
			-1, -1, -1,
		}, null);
		Assert.assertTrue(instance.isEmpty());
	}
}
