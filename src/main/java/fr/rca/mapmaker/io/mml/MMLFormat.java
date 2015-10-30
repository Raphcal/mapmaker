package fr.rca.mapmaker.io.mml;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.HasProgress;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.internal.ColorDataHandler;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.io.internal.LayerDataHandler;
import fr.rca.mapmaker.io.internal.ScrollRateDataHandler;
import fr.rca.mapmaker.io.mkz.AnimationDataHandler;
import fr.rca.mapmaker.io.mkz.BufferedImageDataHandler;
import fr.rca.mapmaker.io.mkz.ImagePaletteDataHandler;
import fr.rca.mapmaker.io.mkz.InstanceDataHandler;
import fr.rca.mapmaker.io.mkz.PackMapDataHandler;
import fr.rca.mapmaker.io.mkz.ProjectDataHandler;
import fr.rca.mapmaker.io.mkz.SpriteDataHandler;
import fr.rca.mapmaker.io.mkz.TileMapDataHandler;
import fr.rca.mapmaker.model.map.PackMap;
import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

/**
 * Export d'une seule carte à destination d'autres projets.
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class MMLFormat extends AbstractFormat implements HasProgress {
	
	private static final String EXTENSION = ".mml";
	
	public MMLFormat() {
		super(EXTENSION, SupportedOperation.SAVE);
		
		addHandler(Color.class, new ColorDataHandler());
		addHandler(Palette.class, new ImagePaletteDataHandler());
		addHandler(BufferedImage.class, new BufferedImageDataHandler());
		addHandler(TileLayer.class, new LayerDataHandler(this));
		addHandler(ScrollRate.class, new ScrollRateDataHandler());
		addHandler(TileMap.class, new TileMapDataHandler(this));
		addHandler(Sprite.class, new SpriteDataHandler(this));
		addHandler(Instance.class, new InstanceDataHandler());
		addHandler(Animation.class, new AnimationDataHandler());
		addHandler(PackMap.class, new PackMapDataHandler());
	}

	@Override
	public void saveProject(Project project, File file) {
		saveProject(project, file, null);
	}

	@Override
	public void saveProject(Project project, File file, Listener progressListener) {
		setVersion(InternalFormat.LAST_VERSION);
		progress(0, progressListener);
		
		file.mkdir();
		
		// Suppression des fichiers existants
		for(final File child : file.listFiles()) {
			child.delete();
		}
		
		progress(10, progressListener);
		
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
		
		progress(20, progressListener);
		
		Palette palette = map.getPalette();
		if(palette instanceof PaletteReference) {
			palette = project.getPalette(((PaletteReference) palette).getPaletteIndex());
		}
		
		try {
			final DataHandler<Palette> paletteDataHandler = getHandler(Palette.class);
			paletteDataHandler.write(palette, new FileOutputStream(new File(file, "palette.pal")));
			progress(30, progressListener);
			
			// Palette
			final DataHandler<BufferedImage> imageHandler = getHandler(BufferedImage.class);
			imageHandler.write(ProjectDataHandler.renderPalette(palette, palette.getTileSize()), new FileOutputStream(new File(file, palette.toString() + '-' + palette.getTileSize() + ".png")));
			progress(40, progressListener);
			
			// Carte
			final DataHandler<TileMap> tileMapHandler = getHandler(TileMap.class);
			tileMapHandler.write(map, new FileOutputStream(new File(file, "map.map")));
			progress(50, progressListener);
			
			// Instances
			final DataHandler<Instance> instanceHandler = getHandler(Instance.class);
			final FileOutputStream instanceStream = new FileOutputStream(new File(file, "map.sprites"));
			Streams.write(instances.size(), instanceStream);
			for(final Instance instance : instances) {
				instanceHandler.write(instance, instanceStream);
			}
			instanceStream.close();
			progress(60, progressListener);
			
			// Sprites
			final PackMap packMap = PackMap.packSprites(project.getSprites(), 1);
			if(packMap != null) {
				// Image
				final DataHandler<BufferedImage> imageDataHandler = getHandler(BufferedImage.class);

				final FileOutputStream imageOutputStream = new FileOutputStream(new File(file, ProjectDataHandler.IMAGE_FILE_NAME));
				try {
					imageDataHandler.write(packMap.renderImage(), imageOutputStream);
				} finally {
					imageOutputStream.close();
				}
				progress(70, progressListener);

				// Atlas
				final DataHandler<PackMap> packMapDataHandler = getHandler(PackMap.class);

				final FileOutputStream dataOutputStream = new FileOutputStream(new File(file, ProjectDataHandler.DATA_FILE_NAME));
				try {
					packMapDataHandler.write(packMap, dataOutputStream);
				} finally {
					dataOutputStream.close();
				}
				progress(80, progressListener);
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
	
	private int progress(int value, HasProgress.Listener listener) {
		if (listener != null) {
			listener.onProgress(value);
		}
		return value;
	}
	
}
