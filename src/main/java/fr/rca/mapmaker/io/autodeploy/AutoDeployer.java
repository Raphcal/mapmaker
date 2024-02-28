package fr.rca.mapmaker.io.autodeploy;

import fr.rca.mapmaker.editor.ProgressDialog;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.mkz.MKZFormat;
import fr.rca.mapmaker.model.map.Packer;
import fr.rca.mapmaker.model.map.PackerFactory;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.SwingWorker;
import javax.swing.filechooser.FileFilter;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe abstraite pour l'écriture d'une classe de déploiement automatique.
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public abstract class AutoDeployer extends FileFilter {

	private static final String PALETTE_IMAGE_EXTENSION = ".png";
	private static final String PALETTE_EXTENSION = ".pal";
	private static final String MAP_INSTANCES_EXTENSION = ".sprites";
	private static final String MAP_EXTENSION = ".map";

	private static final MKZFormat FORMAT = new MKZFormat();

	@Getter @Setter
	private boolean headless;

	/**
	 * Renvoi le nom de l'auto-deployer.
	 *
	 * @return Nom de l'auto-deployer.
	 */
	public abstract String getName();

	/**
	 * Déploie le projet donné à l'emplacement donné.
	 *
	 * @param project Projet à déployer.
	 * @param root Emplacement où déployer.
	 * @throws IOException En cas d'erreur lors du déploiement.
	 */
	public abstract void deployProjectInFolder(Project project, File root) throws IOException;

	/**
	 * Demande l'emplacement où déployer le projet donné puis déploie en
	 * arrière-plan.
	 *
	 * @param project Projet à déployer.
	 * @param fileChooser Sélecteur de fichier.
	 * @param parent Frame parente.
	 */
	public void selectDeployTargetForProject(final Project project, final JFileChooser fileChooser, final JFrame parent) {
		fileChooser.setMultiSelectionEnabled(false);
		for (final FileFilter fileFilter : fileChooser.getChoosableFileFilters()) {
			fileChooser.removeChoosableFileFilter(fileFilter);
		}

		fileChooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
		fileChooser.setFileFilter(this);

		final int action = fileChooser.showSaveDialog(parent);
		if (action == JFileChooser.APPROVE_OPTION) {
			deployInBackground(project, fileChooser.getSelectedFile(), parent);
		}
	}

	/**
	 * Lance le déploiement en arrière-plan.
	 *
	 * @param project
	 * @param destination
	 * @param parent
	 */
	private void deployInBackground(final Project project, final File destination, final JFrame parent) {
		ProgressDialog.showFor(parent, new SwingWorker<Void, Integer>() {
			@Override
			protected Void doInBackground() throws Exception {
				try {
					deployProjectInFolder(project, destination);
				} catch (IOException e) {
					Exceptions.showStackTrace(e, parent);
				}
				return null;
			}
		}, null);
	}

	// --
	// Méthodes utilitaires pour le déploiement.
	// --
	static void setVersion(int version) {
		FORMAT.setVersion(version);
	}

	static void deploySprites(Collection<Sprite> sprites, File folder) throws IOException {
		deploySprites(sprites, folder, null);
	}

	static void deploySprites(Collection<Sprite> sprites, File folder, List<String> files) throws IOException {
		final Packer packMap = PackerFactory.createPacker();
		packMap.addAll(null, sprites, 0.0);

		// Image
		final DataHandler<BufferedImage> imageDataHandler = FORMAT.getHandler(BufferedImage.class);

		final File imageFile = new File(folder, fr.rca.mapmaker.io.mkz.ProjectDataHandler.IMAGE_FILE_NAME);
		final FileOutputStream imageOutputStream = new FileOutputStream(imageFile);
		try {
			imageDataHandler.write(packMap.renderImage(), imageOutputStream);
			addFile(imageFile, files);
		} finally {
			imageOutputStream.close();
		}

		// Atlas
		final DataHandler<Packer> packMapDataHandler = FORMAT.getHandler(Packer.class);

		final File dataFile = new File(folder, fr.rca.mapmaker.io.mkz.ProjectDataHandler.DATA_FILE_NAME);
		final FileOutputStream dataOutputStream = new FileOutputStream(dataFile);
		try {
			packMapDataHandler.write(packMap, dataOutputStream);
			addFile(dataFile, files);
		} finally {
			dataOutputStream.close();
		}
	}

	static void deployPalettes(List<Palette> palettes, File folder) throws IOException {
		deployPalettes(palettes, folder, null);
	}

	static void deployPalettes(List<Palette> palettes, File folder, List<String> files) throws IOException {
		final DataHandler<Palette> paletteDataHandler = FORMAT.getHandler(Palette.class);
		final DataHandler<BufferedImage> imageHandler = FORMAT.getHandler(BufferedImage.class);

		for (Palette palette : palettes) {
			final String baseName = getBaseName(palette);

			final File paletteFile = new File(folder, baseName + PALETTE_EXTENSION);
			final FileOutputStream paletteOutputStream = new FileOutputStream(paletteFile);
			try {
				paletteDataHandler.write(palette, paletteOutputStream);
				addFile(paletteFile, files);
			} finally {
				paletteOutputStream.close();
			}

			// Écriture des images.
			final File imageFile = new File(folder, palette.toString() + '-' + palette.getTileSize() + PALETTE_IMAGE_EXTENSION);
			final BufferedImage image = fr.rca.mapmaker.io.mkz.ProjectDataHandler.renderPalette(palette, palette.getTileSize());
			final FileOutputStream imageOutputStream = new FileOutputStream(imageFile);
			try {
				imageHandler.write(image, imageOutputStream);
				addFile(imageFile, files);
			} finally {
				imageOutputStream.close();
			}
		}
	}

	static void deployMaps(List<TileMap> maps, Project project, File folder) throws IOException {
		deployMaps(maps, project, folder, null);
	}

	static void deployMaps(List<TileMap> maps, Project project, File folder, List<String> files) throws IOException {
		final DataHandler<TileMap> tileMapHandler = FORMAT.getHandler(TileMap.class);
		final DataHandler<Instance> instanceHandler = FORMAT.getHandler(Instance.class);

		for (int index = 0; index < maps.size(); index++) {
			final TileMap mapAndInstances = maps.get(index);
			final TileMap map = mapAndInstances;
			final String baseName = getBaseName(map);

			if (baseName != null) {
				final List<Instance> instances = new ArrayList<>(mapAndInstances.getSpriteInstances());
				Collections.sort(instances, new Comparator<Instance>() {

					@Override
					public int compare(Instance o1, Instance o2) {
						int order = Integer.valueOf(o1.getX()).compareTo(o2.getX());
						if (order == 0) {
							return Integer.valueOf(o1.getY()).compareTo(o2.getY());
						} else {
							return order;
						}
					}
				});

				// Écriture de la carte
				final File mapFile = new File(folder, baseName + MAP_EXTENSION);
				try (FileOutputStream mapOutputStream = new FileOutputStream(mapFile)) {
					tileMapHandler.write(map, mapOutputStream);
					addFile(mapFile, files);
				}

				// Écriture des instances
				final File instancesFile = new File(folder, baseName + MAP_INSTANCES_EXTENSION);
				final FileOutputStream instancesOutputStream = new FileOutputStream(instancesFile);
				try {
					Streams.write(instances.size(), instancesOutputStream);
					for (final Instance instance : instances) {
						instanceHandler.write(instance, instancesOutputStream);
					}
					addFile(instancesFile, files);
				} finally {
					instancesOutputStream.close();
				}
			}
		}
	}

	private static void addFile(File file, List<String> files) {
		if (files != null) {
			files.add(file.getParentFile().getName() + '\\' + file.getName());
		}
	}

	private static String getBaseName(TileMap map) {
		return getBaseName(map.getName());
	}

	private static String getBaseName(Palette palette) {
		return getBaseName(palette.toString());
	}

	private static String getBaseName(String name) {
		if (name == null) {
			return name;
		}

		return name.toLowerCase().replace(' ', '-');
	}

}
