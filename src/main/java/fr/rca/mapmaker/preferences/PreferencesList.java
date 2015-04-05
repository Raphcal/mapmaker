package fr.rca.mapmaker.preferences;

import java.util.AbstractList;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
class PreferencesList extends AbstractList<String> {
	private static final String COUNT = "_count";
	
	private final String name;
	
	public PreferencesList(String name) {
		this.name = name;
	}

	@Override
	public void add(int index, String element) {
		final int size = size();
		
		for(int i = size - 1; i > index; i--) {
			final String value = get(i);
			if(value != null) {
				PreferencesManager.set(getKey(i + 1), get(i));
			} else if(get(i + 1) != null) {
				PreferencesManager.remove(getKey(i + 1));
			}
		}
		
		PreferencesManager.set(getKey(index), element);
		setSize(size + 1);
	}
	
	@Override
	public String get(int index) {
		return PreferencesManager.get(getKey(index));
	}

	@Override
	public String remove(int index) {
		final String entry = get(index);
		final int size = size();
		
		for(int i = index + 1; i < size; i++) {
			final String value = get(i);
			if(value != null) {
				PreferencesManager.set(getKey(i - 1), get(i));
			}
		}
		
		PreferencesManager.remove(getKey(size - 1));
		setSize(size - 1);
		return entry;
	}
	
	private String getKey(int index) {
		return name + '_' + index;
	}

	@Override
	public int size() {
		return PreferencesManager.getInt(name + COUNT);
	}
	
	private void setSize(int size) {
		PreferencesManager.set(name + COUNT, size);
	}
}
