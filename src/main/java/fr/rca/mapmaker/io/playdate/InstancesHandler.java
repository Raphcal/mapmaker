package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
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

		final List<Sprite> sprites;
		final Map<String, Set<String>> variablesForSprites;
		if (!t.isEmpty()) {
			sprites = PlaydateFormat.spritesForProject(t.get(0).getProject());
			variablesForSprites = PlaydateFormat.variablesForSprites(t.get(0).getProject());
		} else {
			sprites = Collections.emptyList();
			variablesForSprites = Collections.emptyMap();
		}
		final HashMap<Sprite, Integer> indexBySprite = new HashMap<>();

		ArrayList<Instance> instances = new ArrayList<>(t);
		instances.sort((lhs, rhs) -> Integer.compare(lhs.getX(), rhs.getX()));
		for (Instance instance : instances) {
			final Point point = instance.getPoint();
			final Sprite sprite = instance.getSprite();
			final int x = point.x + sprite.getWidth() / 2;
			final int y = point.y + sprite.getHeight() / 2;

			Streams.writeUnsignedShort(indexBySprite.computeIfAbsent(sprite, aSprite -> sprites.indexOf(aSprite)), outputStream);
			Streams.write((byte) (instance.getDirection().ordinal()), outputStream);
			Streams.write(x, outputStream);
			Streams.write(y, outputStream);
			Streams.write((byte)instance.getZIndex(), outputStream);
			Streams.write(instance.isUnique(), outputStream);

			final Set<String> variables = variablesForSprites.getOrDefault(instance.getSprite().getScriptFile(), Collections.emptySet());
			Streams.write(variables.stream()
					.mapToInt(variable -> instance.getVariables().getOrDefault(variable, (double) SpriteVariablesAsHeaderHandler.NO_VALUE).intValue())
					.toArray(), outputStream);
		}
	}

	@Override
	public List<Instance> read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}
	
}
