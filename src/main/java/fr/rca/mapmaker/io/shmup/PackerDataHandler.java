package fr.rca.mapmaker.io.shmup;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.Packer;
import fr.rca.mapmaker.model.map.SingleLayerTileMap;
import fr.rca.mapmaker.model.map.TileLayer;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Collection;
import java.util.Map;

/**
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public class PackerDataHandler implements DataHandler<Packer> {
    
    private final Format format;

    public PackerDataHandler(Format format) {
        this.format = format;
    }

    @Override
    public void write(Packer packer, OutputStream outputStream) throws IOException {
        final Map<TileLayer, SingleLayerTileMap> tiles = packer.getTileLayerToTileMap();
        final Collection<SingleLayerTileMap> layers = tiles.values();
        Streams.write(layers.size(), outputStream);
        
        final DataHandler<Point> pointHandler = format.getHandler(Point.class);
        for (final SingleLayerTileMap layer : layers) {
            Point point = packer.getPoint(layer);
            if (point == null) {
                point = new Point();
            }
            pointHandler.write(point, outputStream);
            Streams.write(layer.getEffectiveWidth(), outputStream);
            Streams.write(layer.getEffectiveHeight(), outputStream);
        }
    }

    @Override
    public Packer read(InputStream inputStream) throws IOException {
        throw new UnsupportedOperationException("Not supported yet.");
    }
    
}
