package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import java.awt.Rectangle;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class RectangleDataHandler implements DataHandler<Rectangle> {

	@Override
	public void write(Rectangle t, OutputStream outputStream) throws IOException {
		Streams.write(t != null, outputStream);
		if (t != null) {
			Streams.write(t.x, outputStream);
			Streams.write(t.y, outputStream);
			Streams.write(t.width, outputStream);
			Streams.write(t.height, outputStream);
		}
	}

	@Override
	public Rectangle read(InputStream inputStream) throws IOException {
		final Rectangle rectangle;
		
		if (Streams.readBoolean(inputStream)) {
			final int x = Streams.readInt(inputStream);
			final int y = Streams.readInt(inputStream);
			final int width = Streams.readInt(inputStream);
			final int height = Streams.readInt(inputStream);
			rectangle = new Rectangle(x, y, width, height);
		} else {
			rectangle = null;
		}
		
		return rectangle;
	}
	
}
