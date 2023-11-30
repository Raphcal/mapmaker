package fr.rca.mapmaker;

import fr.rca.mapmaker.editor.MapEditor;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.autodeploy.AutoDeployer;
import fr.rca.mapmaker.io.autodeploy.MeltedIceAutoDeployer;
import fr.rca.mapmaker.io.autodeploy.PlaydateAutoDeployer;
import fr.rca.mapmaker.io.autodeploy.PuzzleSuitAutoDeployer;
import fr.rca.mapmaker.io.common.Formats;
import fr.rca.mapmaker.preferences.PreferencesManager;
import java.awt.Desktop;
import java.awt.EventQueue;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.IOException;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.InvocationTargetException;
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
		if (args.length > 0 && "autodeploy".equals(args[0])) {
			String[] copy = new String[args.length - 1];
			System.arraycopy(args, 1, copy, 0, args.length - 1);
			autoDeploy(copy);
			return;
		}

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
		final Class<?> openFileHandlerClass;
		try {
			openFileHandlerClass = MapMaker.class.getClassLoader().loadClass(nsApplication != null
				? "com.apple.eawt.OpenFilesHandler"
				: "java.awt.desktop.OpenFilesHandler");
		} catch (ClassNotFoundException e) {
			LOGGER.trace("Unable to load OpenFilesHandler class", e);
			return;
		}

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

		final Object parent;
		if (nsApplication != null) {
			parent = nsApplication;
		} else if (Desktop.isDesktopSupported()) {
			parent = Desktop.getDesktop();
		} else {
			return;
		}

		try {
			final Method setOpenFileHandlerMethod = parent.getClass().getMethod("setOpenFileHandler", openFileHandlerClass);
			setOpenFileHandlerMethod.invoke(parent, proxy);
		} catch (IllegalAccessException | IllegalArgumentException | NoSuchMethodException | SecurityException | InvocationTargetException ex) {
			LOGGER.debug("Unable to set macOS open action", ex);
		}
	}

	private static void printAutoDeployUsage() {
		System.out.println("Usage: autodeploy <playdate|meltedice|puzzlesuit> <project file> [target folder]");
	}

	private static void autoDeploy(String[] args) {
		if (args.length < 2 || args.length > 3) {
			printAutoDeployUsage();
			return;
		}
		final AutoDeployer deployer;
		switch (args[0].toLowerCase()) {
			case "playdate":
				deployer = new PlaydateAutoDeployer();
				break;
			case "meltedice":
				deployer = new MeltedIceAutoDeployer();
				break;
			case "puzzlesuit":
				deployer = new PuzzleSuitAutoDeployer();
				break;
			default:
				printAutoDeployUsage();
				return;
		}
		final File projectFile = new File(args[1]);
		final Format format = Formats.getFormat(projectFile.getName());
		if (!projectFile.exists() || format == null) {
			System.err.println("File '" + projectFile + "' is not supported or does not exists.");
			return;
		}
		final File outputFolder = new File(args.length == 3 ? args[2] : "./");
		if (!outputFolder.exists()) {
			outputFolder.mkdirs();
		}
		if (!outputFolder.isDirectory()) {
			System.err.println("Output '" + outputFolder + "' is not a directory.");
			return;
		}
		try {
			deployer.deployProjectInFolder(format.openProject(projectFile), outputFolder);
		} catch (IOException ex) {
			System.err.println("Unable to deploy: " + ex.getClass().getSimpleName() + ' ' + ex.getMessage());
		}
	}
}
