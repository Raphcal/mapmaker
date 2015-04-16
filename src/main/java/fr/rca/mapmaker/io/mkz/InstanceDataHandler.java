package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
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
		Streams.write(t.getIndex(), outputStream);
		Streams.write(t.getPoint().x, outputStream);
		Streams.write(t.getPoint().y, outputStream);
		Streams.writeNullable(t.getScript(), outputStream);
	}

	@Override
	public Instance read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}
	
}
