package fr.rca.mapmaker.io.shmup;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.AbstractFormat;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.mkz.BufferedImageDataHandler;
import fr.rca.mapmaker.model.map.Packer;
import fr.rca.mapmaker.model.map.PackerFactory;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
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
        
        addHandler(BufferedImage.class, new BufferedImageDataHandler());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void saveProject(Project project, File file) {
        final DataHandler<BufferedImage> imageHandler = getHandler(BufferedImage.class);
        
        if (!file.exists()) {
            file.mkdirs();
        }

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
            final BufferedImage image = packer.renderImage();
            
            try (final FileOutputStream outputStream = new FileOutputStream(new File(file, "map" + index + ".png"))) {
                imageHandler.write(image, outputStream);
            } catch (IOException ex) {
                Exceptions.showStackTrace(ex, null);
            }
        }
    }

}
