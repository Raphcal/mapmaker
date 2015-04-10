package fr.rca.mapmaker.io.bundle;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.ImagePalette;
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
import java.io.OutputStream;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class BundleFormat extends AbstractFormat {
	
	private static final String EXTENSION = ".mmkb";

	public BundleFormat() {
		super(EXTENSION, SupportedOperation.SAVE, SupportedOperation.LOAD);
		
		addHandler(Color.class, new fr.rca.mapmaker.io.internal.ColorDataHandler());
		addHandler(Palette.class, new fr.rca.mapmaker.io.internal.PaletteDataHandler(this));
		addHandler(ColorPalette.class, new fr.rca.mapmaker.io.internal.ColorPaletteDataHandler(this));
		addHandler(AlphaColorPalette.class, new fr.rca.mapmaker.io.internal.AlphaColorPaletteDataHandler(this));
		addHandler(EditableColorPalette.class, new fr.rca.mapmaker.io.internal.EditableColorPaletteDataHandler(this));
		addHandler(ImagePalette.class, new fr.rca.mapmaker.io.internal.ImagePaletteDataHandler(this));
		addHandler(EditableImagePalette.class, new fr.rca.mapmaker.io.internal.EditableImagePaletteDataHandler(this));
		addHandler(PaletteReference.class, new fr.rca.mapmaker.io.internal.PaletteReferenceDataHandler());
		addHandler(BufferedImage.class, new fr.rca.mapmaker.io.internal.BufferedImageDataHandler());
		addHandler(TileLayer.class, new fr.rca.mapmaker.io.internal.LayerDataHandler());
		addHandler(TileMap.class, new fr.rca.mapmaker.io.internal.TileMapDataHandler(this));
		addHandler(Sprite.class, new fr.rca.mapmaker.io.internal.SpriteDataHandler(this));
		addHandler(Animation.class, new fr.rca.mapmaker.io.internal.AnimationDataHandler(this));
		addHandler(Instance.class, new fr.rca.mapmaker.io.internal.InstanceDataHandler());
	}

	@Override
	public void saveProject(Project project, File file) {
		file.mkdir();
		try {
			// Palettes
			final DataHandler<Palette> paletteDataHandler = getHandler(Palette.class);
			int index = 0;
			for(final Palette palette : project.getPalettes()) {
				final OutputStream outputStream = new FileOutputStream(new File(file, "palette" + index + ".pal"));
				try {
					paletteDataHandler.write(palette, outputStream);
				} finally {
					outputStream.close();
				}
				
				index++;
			}

			// Cartes
			final DataHandler<TileMap> tileMapHandler = getHandler(TileMap.class);
			index = 0;
			for(final TileMap map : project.getMaps()) {
				final OutputStream outputStream = new FileOutputStream(new File(file, "map" + index + ".map"));
				try {
					tileMapHandler.write(map, outputStream);
				} finally {
					outputStream.close();
				}
				
				index++;
			}

			// Sprites
			final DataHandler<Sprite> spriteHandler = getHandler(Sprite.class);
			index = 0;
			for(final Sprite sprite : project.getSprites()) {
				final OutputStream outputStream = new FileOutputStream(new File(file, "sprite" + index + ".spr"));
				try {
					spriteHandler.write(sprite, outputStream);
				} finally {
					outputStream.close();
				}
				
				index++;
			}

			// Instances
			final DataHandler<Instance> instanceHandler = getHandler(Instance.class);
			index = 0;
			for(final List<Instance> instances : project.getAllInstances()) {
				final OutputStream outputStream = new FileOutputStream(new File(file, "map" + index + ".ins"));
				try {
					Streams.write(instances.size(), outputStream);
					for(Instance instance : instances) {
						instanceHandler.write(instance, outputStream);
					}
				} finally {
					outputStream.close();
				}
				
				index++;
			}
			
		} catch(IOException e) {
			Exceptions.showStackTrace(e, null);
		}
	}
	
}
