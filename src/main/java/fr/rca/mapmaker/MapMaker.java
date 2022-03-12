package fr.rca.mapmaker;

import fr.rca.mapmaker.editor.MapEditor;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.preferences.PreferencesManager;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.desktop.OpenFilesEvent;
import java.awt.desktop.OpenFilesHandler;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.List;
import java.util.prefs.BackingStoreException;
import javax.imageio.ImageIO;
import javax.swing.JFrame;
import javax.swing.UIManager;
import javax.swing.UnsupportedLookAndFeelException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Classe principale du projet.
 * Contient la méthode {@link #main(java.lang.String[])} pour démarrer 
 * l'exécution.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class MapMaker {

	private static final Logger LOGGER = LoggerFactory.getLogger(MapMaker.class);
	private static Object nsApplication;

	/**
	 * Démarre l'application.
	 * 
	 * @param args Liste des arguments de l'application.
	 */
	public static void main(final String[] args) {
		
		// Style Mac OS X
		System.setProperty("apple.laf.useScreenMenuBar", "true");
		System.setProperty("apple.awt.brushMetalLook", "true");
		System.setProperty("com.apple.mrj.application.apple.menu.about.name", "MapMaker");
		
		getNSApplication();
		setDockIcon();
		setLookAndFeel();
		
		EventQueue.invokeLater(new Runnable() {

			@Override
			public void run() {
				final MapEditor editor = new MapEditor();
				editor.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);

				editor.addWindowListener(new WindowAdapter() {

					@Override
					public void windowClosing(WindowEvent e) {
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

				setOpenAction(editor);
				
				if(args.length == 1) {
					final File file = new File(args[0]);
					if(file.exists()) {
						editor.openFile(file);
					}
				}
			}
		});
	}
	
	/**
	 * Défini le look and feel du système qui exécute l'application.
	 */
	private static void setLookAndFeel() {
		try {
			UIManager.setLookAndFeel(UIManager.getSystemLookAndFeelClassName());
		} catch (ClassNotFoundException ex) {
			// Look & Feel système non disponible, ignoré.
		} catch (InstantiationException ex) {
			// Look & Feel système non disponible, ignoré.
		} catch (IllegalAccessException ex) {
			// Look & Feel système non disponible, ignoré.
		} catch (UnsupportedLookAndFeelException ex) {
			// Look & Feel système non disponible, ignoré.
		}
	}
	
	/**
	 * Récupère l'instance de l'objet Application de Mac OS X.
	 */
	private static void getNSApplication() {
		try {
			final Class<?> applicationClass = MapMaker.class.getClassLoader().loadClass("com.apple.eawt.Application");
			final Method getApplicationMethod = applicationClass.getMethod("getApplication");
			nsApplication = getApplicationMethod.invoke(null);
		} catch (Exception ex) {
			LOGGER.debug("Unable to find macOS NSApplication", ex);
		}
	}
	
	/**
	 * Défini l'icône de l'application dans le dock sous Mac OS X.
	 */
	private static void setDockIcon() {
		try {
			if (nsApplication != null) {
				final Method setDockIconImageMethod = nsApplication.getClass().getMethod("setDockIconImage", java.awt.Image.class);
				setDockIconImageMethod.invoke(nsApplication, ImageIO.read(MapMaker.class.getResourceAsStream("/resources/icon.png")));
			}
		} catch (Exception ex) {
			LOGGER.debug("Unable to set macOS dock icon", ex);
		}
	}
	
	/**
	 * Configure l'action d'ouverture des fichiers sous Mac OS X.
	 * @param editor Fenêtre principale de l'éditeur.
	 */
	private static void setOpenAction(final MapEditor editor) {
		if (nsApplication != null) {
			try {
				final Class<?> openFileHandlerClass = MapMaker.class.getClassLoader().loadClass("com.apple.eawt.OpenFilesHandler");
				final Object proxy = Proxy.newProxyInstance(MapMaker.class.getClassLoader(), new Class<?>[] {openFileHandlerClass}, new InvocationHandler() {
					@Override
					public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
						if ("openFiles".equals(method.getName())) {
							final Object event = args[0];
							final Method getFilesMethod = event.getClass().getMethod("getFiles");
							final Object result = getFilesMethod.invoke(event);
							
							if (result instanceof List) {
								final List<File> files = (List<File>) result;
								if (files.size() == 1) {
									editor.openFile(files.get(0));
								}
							}
						}
						return null;
					}
				});
				
				final Method setOpenFileHandlerMethod = nsApplication.getClass().getMethod("setOpenFileHandler", openFileHandlerClass);
				setOpenFileHandlerMethod.invoke(nsApplication, proxy);
				
			} catch (Exception ex) {
				LOGGER.debug("Unable to set macOS open action", ex);
			}
		}
		else if (Desktop.isDesktopSupported()) {
			Desktop.getDesktop().setOpenFileHandler(new OpenFilesHandler() {
				@Override
				public void openFiles(OpenFilesEvent e) {
					final List<File> files = e.getFiles();
					if (files.size() == 1) {
						editor.openFile(files.get(0));
					}
				}
			});
		}
	}
	
}
