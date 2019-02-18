package fr.rca.mapmaker.io.shmup;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.map.Packer;
import fr.rca.mapmaker.model.map.PackerFactory;
import fr.rca.mapmaker.model.map.ScrollRate;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;

/**
 * Format dédié à l'export vers le projet de Shoot'em Up développé avec Hakim
 * Haji.
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public class ShmupFormat extends AbstractFormat {

    private static final String EXTENSION = ".shmp";

    public ShmupFormat() {
        super(EXTENSION, SupportedOperation.SAVE);
        
        addHandler(Point.class, new PointDataHandler());
        addHandler(Packer.class, new PackerDataHandler(this));
        addNamedHandler(Packer.class, "Sprites", new SpritesDataHandler(this));
        addHandler(BufferedImage.class, new BmpWithAlphaBufferedImageDataHandler());
        
        addHandler(TileMap.class, new fr.rca.mapmaker.io.mkz.TileMapDataHandler(this));
        addHandler(Instance.class, new fr.rca.mapmaker.io.mkz.InstanceDataHandler());
        
        addHandler(Color.class, new fr.rca.mapmaker.io.internal.ColorDataHandler());
		addHandler(TileLayer.class, new fr.rca.mapmaker.io.internal.LayerDataHandler(this));
		addHandler(ScrollRate.class, new fr.rca.mapmaker.io.internal.ScrollRateDataHandler());
		addHandler(Rectangle.class, new fr.rca.mapmaker.io.internal.RectangleDataHandler());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveProject(Project project, File file) {
        setVersion(InternalFormat.LAST_VERSION);
        
        if (!file.exists()) {
            file.mkdirs();
        }
        
        final DataHandler<Packer> packerDataHandler = getHandler(Packer.class);
        final DataHandler<Packer> spritePackerDataHandler = getNamedHandler(Packer.class, "Sprites");
        final DataHandler<BufferedImage> imageHandler = getHandler(BufferedImage.class);
        final DataHandler<TileMap> tileMapHandler = getHandler(TileMap.class);
        final DataHandler<Instance> instanceHandler = getHandler(Instance.class);

        for (int index = 0; index < project.getSize(); index++) {
            final TileMap map = project.getMaps().get(index);
            final List<Instance> instances = project.getAllInstances().get(index);
            final HashSet<Sprite> sprites = new HashSet<>();
            for (final Instance instance : instances) {
                sprites.add(instance.getSprite());
            }

            Palette palette = map.getPalette();
            if (palette instanceof PaletteReference) {
                palette = ((PaletteReference)palette).getProject().getPalette(((PaletteReference)palette).getPaletteIndex());
            }
            final EditableImagePalette imagePalette = palette instanceof EditableImagePalette ?
                    (EditableImagePalette) palette : null;

            final Packer packer = PackerFactory.createPacker();
            packer.addAll(imagePalette, sprites, null);
            
            try {
                try (final FileOutputStream outputStream = new FileOutputStream(new File(file, "map" + index + "-texture.bmp"))) {
                    final BufferedImage image = packer.renderImage();
                    imageHandler.write(image, outputStream);
                }
                try (final FileOutputStream outputStream = new FileOutputStream(new File(file, "map" + index + "-texture.atlas"))) {
                    packerDataHandler.write(packer, outputStream);
                }
                try (final FileOutputStream outputStream = new FileOutputStream(new File(file, "map" + index + ".grid"))) {
                    tileMapHandler.write(map, outputStream);
                }
                try (final FileOutputStream outputStream = new FileOutputStream(new File(file, "map" + index + "-sprites.def"))) {
                    spritePackerDataHandler.write(packer, outputStream);
                }
                try (final FileOutputStream outputStream = new FileOutputStream(new File(file, "map" + index + "-sprites.inst"))) {
                    Streams.write(instances.size(), outputStream);
					for(final Instance instance : instances) {
						instanceHandler.write(instance, outputStream);
					}
                }
            } catch (IOException ex) {
                Exceptions.showStackTrace(ex, null);
            }
        }
    }

}
