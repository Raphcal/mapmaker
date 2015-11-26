package fr.rca.mapmaker.io.mml;

import fr.rca.mapmaker.event.Event;
import fr.rca.mapmaker.event.EventBus;
import fr.rca.mapmaker.event.EventListener;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.HasProgress;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.io.mkz.ProjectDataHandler;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import javax.swing.SwingWorker;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Export d'une seule carte à destination d'autres projets.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class MMLFormat extends AbstractFormat implements HasProgress {
	
	private static final String EXTENSION = ".mml";
	
	private static final Logger LOGGER = LoggerFactory.getLogger(MMLFormat.class);
	private static final double STEP = 100 / 8;
	
	private PackMap cachedPackMap;
	private BufferedImage cachedAtlas;
	
	public MMLFormat() {
		super(EXTENSION, SupportedOperation.SAVE);
		
		// Handlers du format MKZ.
		addHandler(Palette.class, new fr.rca.mapmaker.io.mkz.ImagePaletteDataHandler());
		addHandler(BufferedImage.class, new fr.rca.mapmaker.io.mkz.BufferedImageDataHandler());
		addHandler(TileMap.class, new fr.rca.mapmaker.io.mkz.TileMapDataHandler(this));
		addHandler(Sprite.class, new fr.rca.mapmaker.io.mkz.SpriteDataHandler(this));
		addHandler(Instance.class, new fr.rca.mapmaker.io.mkz.InstanceDataHandler());
		addHandler(PackMap.class, new fr.rca.mapmaker.io.mkz.PackMapDataHandler(this));
		
		// Handlers du format interne.
		addHandler(Color.class, new fr.rca.mapmaker.io.internal.ColorDataHandler());
		addHandler(TileLayer.class, new fr.rca.mapmaker.io.internal.LayerDataHandler(this));
		addHandler(ScrollRate.class, new fr.rca.mapmaker.io.internal.ScrollRateDataHandler());
		addHandler(Rectangle.class, new fr.rca.mapmaker.io.internal.RectangleDataHandler());
		
		EventBus.INSTANCE.listenToEventsOfType(Event.SPRITE_CHANGED, new EventListener() {
			@Override
			public void onEvent(Event event, Object[] arguments) {
				cachedPackMap = null;
				cachedAtlas = null;
				
				if (arguments.length == 1 && arguments[0] instanceof List) {
					final List<Sprite> sprites = (List<Sprite>) arguments[0];
					
					// Met à jour le cache.
					final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
						@Override
						protected Void doInBackground() throws Exception {
							LOGGER.info("Mise en cache des sprites...");
							cachedPackMap = PackMap.packSprites(sprites, 1);
							LOGGER.info("PackMap mis en cache.");
							cachedAtlas = cachedPackMap.renderImage();
							LOGGER.info("Atlas mis en cache.");
							return null;
						}
					};
					worker.execute();
				}
			}
		});
	}

	@Override
	public void saveProject(Project project, File file) {
		saveProject(project, file, null);
	}

	@Override
	public void saveProject(Project project, File file, Listener progressListener) {
		double progression = 0;
		
		setVersion(InternalFormat.LAST_VERSION);
		progress(0, progressListener);
		
		file.mkdir();
		
		// Suppression des fichiers existants
		for(final File child : file.listFiles()) {
			child.delete();
		}
		
		progression = progress(progression + STEP, progressListener);
		
		final TileMap map = project.getCurrentMap();
		final List<Instance> instances = new ArrayList<Instance>(project.getInstances());
		
		Collections.sort(instances, new Comparator<Instance>() {

			@Override
			public int compare(Instance o1, Instance o2) {
				int order = Integer.valueOf(o1.getX()).compareTo(o2.getX());
				if(order == 0) {
					return Integer.valueOf(o1.getY()).compareTo(o2.getY());
				} else {
					return order;
				}
			}
		});
		
		progression = progress(progression + STEP, progressListener);
		
		Palette palette = map.getPalette();
		if(palette instanceof PaletteReference) {
			palette = project.getPalette(((PaletteReference) palette).getPaletteIndex());
		}
		
		try {
			final DataHandler<Palette> paletteDataHandler = getHandler(Palette.class);
			paletteDataHandler.write(palette, new FileOutputStream(new File(file, "palette.pal")));
			progression = progress(progression + STEP, progressListener);
			
			// Palette
			final DataHandler<BufferedImage> imageHandler = getHandler(BufferedImage.class);
			imageHandler.write(ProjectDataHandler.renderPalette(palette, palette.getTileSize()), new FileOutputStream(new File(file, palette.toString() + '-' + palette.getTileSize() + ".png")));
			progression = progress(progression + STEP, progressListener);
			
			// Carte
			final DataHandler<TileMap> tileMapHandler = getHandler(TileMap.class);
			tileMapHandler.write(map, new FileOutputStream(new File(file, "map.map")));
			progression = progress(progression + STEP, progressListener);
			
			// Instances
			final DataHandler<Instance> instanceHandler = getHandler(Instance.class);
			final FileOutputStream instanceStream = new FileOutputStream(new File(file, "map.sprites"));
			Streams.write(instances.size(), instanceStream);
			
			final double instanceStep = STEP / instances.size();
			
			for(final Instance instance : instances) {
				instanceHandler.write(instance, instanceStream);
				progression = progress(progression + instanceStep, progressListener);
			}
			instanceStream.close();
			
			// Sprites
			final PackMap packMap = cachedPackMap != null ? cachedPackMap : PackMap.packSprites(project.getSprites(), 1);
			cachedPackMap = packMap;
			
			if(packMap != null) {
				// Image
				final DataHandler<BufferedImage> imageDataHandler = getHandler(BufferedImage.class);

				final FileOutputStream imageOutputStream = new FileOutputStream(new File(file, ProjectDataHandler.IMAGE_FILE_NAME));
				try {
					final BufferedImage image = cachedAtlas != null ? cachedAtlas : packMap.renderImage();
					cachedAtlas = image;
					imageDataHandler.write(image, imageOutputStream);
				} finally {
					imageOutputStream.close();
				}
				progression = progress(progression + STEP, progressListener);

				// Atlas
				final DataHandler<PackMap> packMapDataHandler = getHandler(PackMap.class);

				final FileOutputStream dataOutputStream = new FileOutputStream(new File(file, ProjectDataHandler.DATA_FILE_NAME));
				try {
					packMapDataHandler.write(packMap, dataOutputStream);
				} finally {
					dataOutputStream.close();
				}
				progress(progression + STEP, progressListener);
			}
			
			progress(100, progressListener);
		} catch(IOException e) {
			Exceptions.showStackTrace(e, null);
		}
	}

	@Override
	public Project openProject(File file, Listener progressListener) {
		throw new UnsupportedOperationException("Not supported.");
	}
	
	private double progress(double value, HasProgress.Listener listener) {
		if (listener != null) {
			listener.onProgress((int) value);
		}
		return value;
	}
	
}
