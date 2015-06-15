package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.ScrollRate;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class ScrollRateDataHandler implements DataHandler<ScrollRate>, HasVersion {

	private int version;
	
	@Override
	public void write(ScrollRate t, OutputStream outputStream) throws IOException {
		if(version < InternalFormat.VERSION_5) {
			Streams.write((float) t.getX(), outputStream);
		} else {
			Streams.write((float) t.getX(), outputStream);
			Streams.write((float) t.getY(), outputStream);
		}
	}

	@Override
	public ScrollRate read(InputStream inputStream) throws IOException {
		final float x = Streams.readFloat(inputStream);
		final float y;
		
		if(version < InternalFormat.VERSION_5) {
			y = x;
		} else {
			y = Streams.readFloat(inputStream);
		}
		
		return new ScrollRate(x, y);
	}

	@Override
	public void setVersion(int version) {
		this.version = version;
	}
	
}
