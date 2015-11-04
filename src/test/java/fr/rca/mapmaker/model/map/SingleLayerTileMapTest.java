package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import static org.hamcrest.CoreMatchers.*;
import org.junit.Assert;
import org.junit.Test;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class SingleLayerTileMapTest {
	
	@Test
	public void testEquality() {
		final TileLayer tileLayer = new TileLayer();
		tileLayer.restoreData(new int[] {
			1, 2, 3, 
			4, 5, 6, 
			7, 8, 9, 
		}, 3, 3);
		
		final SingleLayerTileMap first = new SingleLayerTileMap(tileLayer, AlphaColorPalette.getDefaultColorPalette());
		final SingleLayerTileMap second = new SingleLayerTileMap(tileLayer, AlphaColorPalette.getDefaultColorPalette());
		
		Assert.assertTrue(first.equals(second));
		Assert.assertThat(first.hashCode(), is(second.hashCode()));
		Assert.assertThat(first.compareTo(second), is(0));
		
		final TileLayer otherTileLayer = new TileLayer();
		otherTileLayer.restoreData(new int[] {
			1, 2, 3, 
			4, 5, 6, 
			7, 8, 9, 
		}, 3, 3);
		final SingleLayerTileMap third = new SingleLayerTileMap(otherTileLayer, AlphaColorPalette.getDefaultColorPalette());
		
		Assert.assertTrue(first.equals(third));
		Assert.assertThat(first.hashCode(), is(third.hashCode()));
		Assert.assertThat(first.compareTo(third), is(0));
	}
	
	@Test
	public void testInequality() {
		final TileLayer firstTileLayer = new TileLayer();
		firstTileLayer.restoreData(new int[] {
			1, 2, 3, 
			4, 5, 6, 
			7, 8, 9, 
		}, 3, 3);
		
		final TileLayer secondTileLayer = new TileLayer();
		secondTileLayer.restoreData(new int[] {
			9, 8, 7, 
			6, 5, 4, 
			3, 2, 1, 
		}, 3, 3);
		
		final SingleLayerTileMap first = new SingleLayerTileMap(firstTileLayer, AlphaColorPalette.getDefaultColorPalette());
		final SingleLayerTileMap second = new SingleLayerTileMap(secondTileLayer, AlphaColorPalette.getDefaultColorPalette());
		
		Assert.assertFalse(first.equals(second));
		Assert.assertThat(first.hashCode(), is(not(second.hashCode())));
		Assert.assertThat(first.compareTo(second), is(not(0)));
	}
	
}
