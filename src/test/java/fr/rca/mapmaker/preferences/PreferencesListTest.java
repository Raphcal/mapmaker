package fr.rca.mapmaker.preferences;

import org.junit.Assert;
import org.junit.Test;
import static org.junit.Assert.*;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PreferencesListTest {
	
	private PreferencesList createPreferencesList() {
		final PreferencesList preferencesList = new PreferencesList("PreferencesListTest");
		preferencesList.clear();
		return preferencesList;
	}
	
	/**
	 * Test of add method, of class PreferencesList.
	 */
	@Test
	public void testAdd() {
		System.out.println("add");
		
		int index = 0;
		String element = "un";
		String element2 = "deux";
		PreferencesList instance = createPreferencesList();
		
		Assert.assertTrue(instance.isEmpty());
		
		instance.add(index, element);
		
		Assert.assertEquals(1, instance.size());
		Assert.assertEquals(element, instance.get(0));
		
		instance.add(index, element2);
		
		Assert.assertEquals(2, instance.size());
		Assert.assertEquals(element2, instance.get(0));
		Assert.assertEquals(element, instance.get(1));
	}

	/**
	 * Test of get method, of class PreferencesList.
	 */
	@Test
	public void testGet() {
		System.out.println("get");
		PreferencesList instance = createPreferencesList();
		
		assertEquals(null, instance.get(0));
		
		instance.add("un");
		instance.add("deux");
		instance.add("trois");
		
		assertEquals("un", instance.get(0));
		assertEquals("deux", instance.get(1));
		assertEquals("trois", instance.get(2));
	}

	/**
	 * Test of remove method, of class PreferencesList.
	 */
	@Test
	public void testRemove() {
		System.out.println("remove");
		PreferencesList instance = createPreferencesList();
		
		assertEquals(null, instance.get(0));
		
		instance.add("un");
		instance.add("deux");
		instance.add("trois");
		assertEquals("un", instance.get(0));
		assertEquals("deux", instance.get(1));
		assertEquals("trois", instance.get(2));
		
		instance.remove(1);
		assertEquals("un", instance.get(0));
		assertEquals("trois", instance.get(1));
	}

	/**
	 * Test of size method, of class PreferencesList.
	 */
	@Test
	public void testSize() {
		System.out.println("size");
		PreferencesList instance = createPreferencesList();
		assertEquals(0, instance.size());
		
		instance.add("un");
		instance.add("deux");
		instance.add("trois");
		assertEquals(3, instance.size());
	}
	
}
