package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Streams;
import fr.rca.mapmaker.model.sprite.Instance;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class InstanceDataHandler implements DataHandler<Instance> {

	@Override
	public void write(Instance t, OutputStream outputStream) throws IOException {
		Streams.write(t.getPoint().x, outputStream);
		Streams.write(t.getPoint().y, outputStream);
		Streams.write(t.getIndex(), outputStream);
	}

	@Override
	public Instance read(InputStream inputStream) throws IOException {
		final int x = Streams.readInt(inputStream);
		final int y = Streams.readInt(inputStream);
		final int index = Streams.readInt(inputStream);
		
		return new Instance(index, x, y);
	}
	
}
