package fr.rca.mapmaker.model.map;

import java.awt.Point;
import java.awt.Rectangle;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpanningTileLayerTest {
	
	private SpanningTileLayer createSpanningTileLayer() {
		final SpanningTileLayer layer = new SpanningTileLayer();
		layer.setSize(2, 2);
		
		final TileLayer topLeft = new TileLayer(3, 3);
		topLeft.restoreData(new int[] {
			0, 0, 0,
			0, 0, 0,
			0, 0, 0,
		}, null);
		
		final TileLayer topRight = new TileLayer(3, 3);
		topRight.restoreData(new int[] {
			1, 1, 1,
			1, 1, 1,
			1, 1, 1,
		}, null);
		
		final TileLayer bottomLeft = new TileLayer(3, 3);
		bottomLeft.restoreData(new int[] {
			2, 2, 2,
			2, 2, 2,
			2, 2, 2,
		}, null);
		
		final TileLayer bottomRight = new TileLayer(3, 3);
		bottomRight.restoreData(new int[] {
			3, 3, 3,
			3, 3, 3,
			3, 3, 3,
		}, null);
		
		layer.setLayer(topLeft, 0, 0);
		layer.setLayer(topRight, 1, 0);
		layer.setLayer(bottomLeft, 0, 1);
		layer.setLayer(bottomRight, 1, 1);
		
		layer.updateSize();
		
		return layer;
	}
	
	/**
	 * Test of copyData method, of class SpanningTileLayer.
	 */
	@Test
	public void testCopyData() {
		System.out.println("copyData");
		final SpanningTileLayer instance = createSpanningTileLayer();
		final int[] expResult = new int[] {
			0, 0, 0, 1, 1, 1,
			0, 0, 0, 1, 1, 1,
			0, 0, 0, 1, 1, 1,
			2, 2, 2, 3, 3, 3,
			2, 2, 2, 3, 3, 3,
			2, 2, 2, 3, 3, 3,
		};
		final int[] result = instance.copyData();
		assertArrayEquals(expResult, result);
	}

	/**
	 * Test of restoreData method, of class SpanningTileLayer.
	 */
	@Test
	public void testRestoreData() {
		System.out.println("restoreData");
		
		
		
		final int[] tiles = new int[] {
			4, 4, 4, 5, 5, 5,
			4, 4, 4, 6, 6, 6,
			4, 4, 4, 7, 7, 7,
			8, 9, 0, 1, 2, 3,
			8, 9, 0, 4, 5, 6,
			8, 9, 0, 7, 8, 9,
		};
		Rectangle source = null;
		SpanningTileLayer instance = createSpanningTileLayer();
		
		assertEquals(9, instance.getLayer(0, 0).copyData().length);
		assertEquals(9, instance.getLayer(1, 0).copyData().length);
		assertEquals(9, instance.getLayer(0, 1).copyData().length);
		assertEquals(9, instance.getLayer(1, 1).copyData().length);
		
		instance.restoreData(tiles, source);
		
		assertArrayEquals(new int[] {
			4, 4, 4, 
			4, 4, 4, 
			4, 4, 4, 
		}, instance.getLayer(0, 0).copyData());
		
		assertArrayEquals(new int[] {
			5, 5, 5, 
			6, 6, 6, 
			7, 7, 7, 
		}, instance.getLayer(1, 0).copyData());
		
		assertArrayEquals(new int[] {
			8, 9, 0, 
			8, 9, 0, 
			8, 9, 0, 
		}, instance.getLayer(0, 1).copyData());
		
		assertArrayEquals(new int[] {
			1, 2, 3, 
			4, 5, 6, 
			7, 8, 9, 
		}, instance.getLayer(1, 1).copyData());
	}

	/**
	 * Test of getWidth method, of class SpanningTileLayer.
	 */
	@Test
	public void testGetWidth() {
		System.out.println("getWidth");
		SpanningTileLayer instance = createSpanningTileLayer();
		int expResult = 6;
		int result = instance.getWidth();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getHeight method, of class SpanningTileLayer.
	 */
	@Test
	public void testGetHeight() {
		System.out.println("getHeight");
		SpanningTileLayer instance = createSpanningTileLayer();
		int expResult = 6;
		int result = instance.getHeight();
		assertEquals(expResult, result);
	}

	/**
	 * Test of getTile method, of class SpanningTileLayer.
	 */
	@Test
	public void testGetTile_int_int() {
		System.out.println("getTile");
		SpanningTileLayer instance = createSpanningTileLayer();
		
		final int[] expResult = new int[] {
			0, 0, 0, 1, 1, 1,
			0, 0, 0, 1, 1, 1,
			0, 0, 0, 1, 1, 1,
			2, 2, 2, 3, 3, 3,
			2, 2, 2, 3, 3, 3,
			2, 2, 2, 3, 3, 3,
		};
		
		for(int y = 0; y < 6; y++) {
			for(int x = 0; x < 6; x++) {
				assertEquals(expResult[y * 6 + x], instance.getTile(x, y));
			}
		}
	}

	/**
	 * Test of getTile method, of class SpanningTileLayer.
	 */
	@Test
	public void testGetTile_Point() {
		System.out.println("getTile");
		SpanningTileLayer instance = createSpanningTileLayer();
		
		final int[] expResult = new int[] {
			0, 0, 0, 1, 1, 1,
			0, 0, 0, 1, 1, 1,
			0, 0, 0, 1, 1, 1,
			2, 2, 2, 3, 3, 3,
			2, 2, 2, 3, 3, 3,
			2, 2, 2, 3, 3, 3,
		};
		
		for(int y = 0; y < 6; y++) {
			for(int x = 0; x < 6; x++) {
				assertEquals(expResult[y * 6 + x], instance.getTile(new Point(x, y)));
			}
		}
	}

}
