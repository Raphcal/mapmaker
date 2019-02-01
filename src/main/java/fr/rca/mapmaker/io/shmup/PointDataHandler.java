package fr.rca.mapmaker.io.shmup;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public class PointDataHandler implements DataHandler<Point> {

    @Override
    public void write(Point t, OutputStream outputStream) throws IOException {
        Streams.write(t.x, outputStream);
        Streams.write(t.y, outputStream);
    }

    @Override
    public Point read(InputStream inputStream) throws IOException {
        final int x = Streams.readInt(inputStream);
        final int y = Streams.readInt(inputStream);
        return new Point(x, y);
    }
    
}
