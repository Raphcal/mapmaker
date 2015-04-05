package fr.rca.mapmaker;

import fr.rca.mapmaker.editor.MapEditor;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.preferences.PreferencesManager;

import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.Method;
import java.util.prefs.BackingStoreException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;

public class MapMaker {
	
	public static void main(final String[] args) {
		
		// Style Mac OS X
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.brushMetalLook", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MapMaker");
		
		setDockIcon();
		
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (/* ClassNotFoundException | InstantiationException | IllegalAccessException | UnsupportedLookAndFeel */ Exception ex) {
			// Look & Feel système non disponible, ignoré.
		}
		
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				final MapEditor editor = new MapEditor();
				editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				editor.addWindowListener(new WindowAdapter() {
					@Override
					public void windowClosed(WindowEvent e) {
						PreferencesManager.set(PreferencesManager.WIDTH, editor.getWidth());
						PreferencesManager.set(PreferencesManager.HEIGHT, editor.getHeight());
						PreferencesManager.set(PreferencesManager.EXTENDED_STATE, editor.getExtendedState());

						try {
							PreferencesManager.sync();
						} catch (BackingStoreException ex) {
							Exceptions.showStackTrace(ex, editor);
						}
					}
				});

				final int width = PreferencesManager.getInt(PreferencesManager.WIDTH, editor.getWidth());
				final int height = PreferencesManager.getInt(PreferencesManager.HEIGHT, editor.getHeight());
				final int extendedState = PreferencesManager.getInt(PreferencesManager.EXTENDED_STATE, editor.getExtendedState());

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
	
	private static void setDockIcon() {
		try {
			final Class<?> applicationClass = MapMaker.class.getClassLoader().loadClass("com.apple.eawt.Application");
			final Method getApplicationMethod = applicationClass.getMethod("getApplication");
			final Object application = getApplicationMethod.invoke(null);
			
			if(application != null) {
				final Method setDockIconImageMethod = applicationClass.getMethod("setDockIconImage", java.awt.Image.class);
				setDockIconImageMethod.invoke(application, ImageIO.read(MapMaker.class.getResourceAsStream("/resources/icon.png")));
			}
			
		} catch (/* ClassNotFoundException | NoSuchMethodException | SecurityException | IllegalAccessException | IllegalArgumentException | InvocationTargetException | IO */ Exception ex) {
			// Ignoré.
		}
	}
}
