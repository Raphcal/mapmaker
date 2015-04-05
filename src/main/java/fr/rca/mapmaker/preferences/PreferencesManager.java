package fr.rca.mapmaker.preferences;

import java.util.List;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class PreferencesManager {
	public static final String WIDTH = "width";
	public static final String HEIGHT = "height";
	public static final String EXTENDED_STATE = "extended_state";
	public static final String CURRENT_DIRECTORY = "current_dir";
	public static final String RECENT = "recent";
	public static final String LAST_GAME_PREVIEW_DIMENSION = "last_game_preview_dimension";
	public static final String LAST_GAME_PREVIEW_ZOOM = "last_game_preview_zoom";
	
	private static final Preferences PREFERENCES;
	
	static {
		PREFERENCES = Preferences.userNodeForPackage(PreferencesManager.class);
	}
	
	public static String get(String preference) {
		return get(preference, null);
	}
	public static String get(String preference, String defaultValue) {
		return PREFERENCES.get(preference, defaultValue);
	}
	
	public static int getInt(String preference) {
		return getInt(preference, 0);
	}
	public static int getInt(String preference, int defaultValue) {
		return PREFERENCES.getInt(preference, defaultValue);
	}
	
	public static double getDouble(String preference) {
		return getDouble(preference, 0.0);
	}
	public static double getDouble(String preference, double defaultValue) {
		return PREFERENCES.getDouble(preference, defaultValue);
	}
	
	public static List<String> getList(String name) {
		return new PreferencesList(name);
	}
	
	public static void set(String preference, String value) {
		PREFERENCES.put(preference, value);
	}
	
	public static void set(String preference, int value) {
		PREFERENCES.putInt(preference, value);
	}
	
	public static void set(String preference, double value) {
		PREFERENCES.putDouble(preference, value);
	}
	
	public static void remove(String preference) {
		PREFERENCES.remove(preference);
	}
	
	public static void sync() throws BackingStoreException {
		PREFERENCES.sync();
	}
}
