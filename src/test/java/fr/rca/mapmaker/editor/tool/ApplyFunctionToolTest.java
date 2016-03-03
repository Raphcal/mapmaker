package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ApplyFunctionToolTest {
	
	/**
	 * Test of execute method, of class ApplyFunctionTool.
	 */
	@Test
	public void testExecuteWithX() {
		System.out.println("execute");
		
		final TileLayer layer = new TileLayer(3, 3);
		layer.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		ApplyFunctionTool.execute(layer, "x");
		
		Assert.assertArrayEquals(new int[] {
			1,-1,-1,
			4, 2,-1,
			7, 5, 3
		}, layer.copyData());
	}
	
	/**
	 * Test of execute method, of class ApplyFunctionTool.
	 */
	@Test
	public void testExecuteWithMinusX() {
		System.out.println("execute");
		
		final TileLayer layer = new TileLayer(3, 3);
		layer.restoreData(new int[] {
			1, 2, 3,
			4, 5, 6,
			7, 8, 9
		}, null);
		
		ApplyFunctionTool.execute(layer, "-x + zoom(2)");
		
		Assert.assertArrayEquals(new int[] {
			-1,-1, 3,
			-1, 2, 6,
		     1, 5, 9
		}, layer.copyData());
	}
	
}
