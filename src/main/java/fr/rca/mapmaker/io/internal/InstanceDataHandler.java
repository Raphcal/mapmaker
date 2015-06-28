package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.sprite.Instance;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class InstanceDataHandler implements DataHandler<Instance>, HasVersion {

	private int version;
	
	@Override
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public void write(Instance t, OutputStream outputStream) throws IOException {
		Streams.write(t.getPoint().x, outputStream);
		Streams.write(t.getPoint().y, outputStream);
		Streams.write(t.getIndex(), outputStream);
		
		if(version >= InternalFormat.VERSION_4) {
			Streams.write(t.isUnique(), outputStream);
			Streams.writeNullable(t.getScript(), outputStream);
		}
	}

	@Override
	public Instance read(InputStream inputStream) throws IOException {
		final int x = Streams.readInt(inputStream);
		final int y = Streams.readInt(inputStream);
		final int index = Streams.readInt(inputStream);
		final boolean unique;
		final String script;
		
		if(version >= InternalFormat.VERSION_4) {
			unique = Streams.readBoolean(inputStream);
			script = Streams.readNullableString(inputStream);
		} else {
			unique = false;
			script = null;
		}
		
		return new Instance(index, x, y, unique, script);
	}
	
}
