package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.List;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class InstancesHandler implements DataHandler<List<Instance>> {

	@Override
	public void write(List<Instance> t, OutputStream outputStream) throws IOException {
		Streams.write(t.size(), outputStream);

		for (Instance instance : t) {
			final Point point = instance.getPoint();
			final Sprite sprite = instance.getSprite();
			final int x = point.x + sprite.getWidth() / 2;
			final int y = point.y + sprite.getHeight() / 2;

			Streams.writeUnsignedShort(instance.getIndex(), outputStream);
			Streams.write((byte) (instance.getDirection().ordinal()), outputStream);
			Streams.write(x, outputStream);
			Streams.write(y, outputStream);

			// TODO: Écrire les variables : instance.getVariables().entrySet()
		}
	}

	@Override
	public List<Instance> read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}
	
}