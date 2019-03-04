package fr.rca.mapmaker.io.shmup;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.Packer;
import fr.rca.mapmaker.model.map.SingleLayerTileMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public class SpritesDataHandler implements DataHandler<Packer> {
    
    private static final Logger LOGGER = LoggerFactory.getLogger(SpritesDataHandler.class);
    
    private final Format format;

    public SpritesDataHandler(Format format) {
        this.format = format;
    }

    @Override
    public void write(Packer t, OutputStream outputStream) throws IOException {
        final Collection<Sprite> sprites = t.getSprites();
        
        final Map<TileLayer, SingleLayerTileMap> maps = t.getTileLayerToTileMap();
        final List<TileLayer> layers = new ArrayList<>(maps.keySet());

        Streams.write(sprites.size(), outputStream);

        for (final Sprite sprite : sprites) {
            Streams.writeNullable(sprite.getName(), outputStream);
            Streams.write(sprite.getWidth(), outputStream);
            Streams.write(sprite.getHeight(), outputStream);
            Streams.write(sprite.getType(), outputStream);
            Streams.writeNullable(sprite.getScriptFile(), outputStream);

            final Animation[] defaultAnimations = Animation.getDefaultAnimations();
            Streams.write(defaultAnimations.length, outputStream);

            for (Animation defaultAnimation : defaultAnimations) {
                final Animation animation = sprite.get(defaultAnimation.getName());

                Streams.write(animation.getName(), outputStream);
                Streams.write(animation.getFrequency(), outputStream);
                Streams.write(animation.isLooping(), outputStream);

                if (animation.isScrolling()) {
                    LOGGER.warn("Scrolling animation are not supported");
                }
                
                writeAnimation(animation, sprite, t, layers, outputStream);
            }
        }
    }

    private void writeAnimation(final Animation animation, final Sprite sprite, final Packer t, final List<TileLayer> layers, final OutputStream outputStream) throws IOException {
        final Set<Double> directions = animation.getAnglesWithValue();
        Streams.write(directions.size(), outputStream);

        final DataHandler<Rectangle> rectangleHandler = format.getHandler(Rectangle.class);

        for (final double direction : directions) {
            Streams.write(direction, outputStream);

            final List<TileLayer> frames = animation.getFrames(direction);
            Streams.write(frames.size(), outputStream);

            for (final TileLayer frame : frames) {
                Streams.write(layers.indexOf(frame), outputStream);
                final HitboxLayerPlugin plugin = frame.getPlugin(HitboxLayerPlugin.class);
                final Rectangle hitbox;
                if (plugin != null) {
                    hitbox = plugin.getHitbox();
                } else {
                    hitbox = null;
                }
                rectangleHandler.write(hitbox, outputStream);
            }
        }
    }

    @Override
    public Packer read(InputStream inputStream) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
