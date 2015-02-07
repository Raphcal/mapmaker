package fr.rca.mapmaker;

import fr.rca.mapmaker.editor.MapEditor;
import fr.rca.mapmaker.exception.Exceptions;
import java.io.IOException;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.util.prefs.BackingStoreException;
import java.util.prefs.Preferences;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class MapMaker {
	public static final String PREFERENCES_WIDTH = "width";
	public static final String PREFERENCES_HEIGHT = "height";
	public static final String PREFERENCES_EXTENDED_STATE = "extended_state";
	public static final String PREFERENCES_CURRENT_DIRECTORY = "current_dir";
	
	public static void main(final String[] args) throws IOException {
		
		// Style Mac OS X
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.brushMetalLook", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MapMaker");
		
//		final Application application = Application.getApplication();
//		if(application != null) {
//			application.setDockIconImage(null);
//		}
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (Exception ex) {
			// Look & Feel système non disponible, ignoré.
		}
		
		final Preferences preferences = Preferences.userNodeForPackage(MapMaker.class);
		
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				final MapEditor editor = new MapEditor();
				editor.setDefaultCloseOperation(JFrame.DISPOSE_ON_CLOSE);
				
				editor.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						preferences.putInt(PREFERENCES_WIDTH, editor.getWidth());
						preferences.putInt(PREFERENCES_HEIGHT, editor.getHeight());
						preferences.putInt(PREFERENCES_EXTENDED_STATE, editor.getExtendedState());
						
						try {
							preferences.sync();
						} catch (BackingStoreException ex) {
							Exceptions.showStackTrace(ex, editor);
						}
						
						System.exit(0);
					}
				});
				
				final int width = preferences.getInt(PREFERENCES_WIDTH, editor.getWidth());
				final int height = preferences.getInt(PREFERENCES_HEIGHT, editor.getHeight());
				final int extendedState = preferences.getInt(PREFERENCES_EXTENDED_STATE, editor.getExtendedState());
				
				editor.setSize(width, height);
				editor.setExtendedState(extendedState);
				editor.setVisible(true);
				
				if(args.length == 1) {
					final File file = new File(args[0]);
					if(file.exists()) {
						editor.openFile(file);
					}
				}
			}
		});
	}
}
