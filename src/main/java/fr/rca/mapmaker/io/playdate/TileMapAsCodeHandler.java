package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapAsCodeHandler extends CodeDataHandler<TileMap> {

	private static final String MAP_TYPE = "MELMap";
	private static final String LAYER_TYPE = "MELLayer";

	@Override
	public void write(TileMap t, OutputStream outputStream) throws IOException {
		final int layerCount = t.getLayers().size();

		Palette palette = t.getPalette();

		final String name = Names.normalizeName(t, Names::toLowerCase);
		final String pascalCasedName = Names.normalizeName(t, Names::toPascalCase);
		if (pascalCasedName == null) {
			throw new IllegalArgumentException("Le nom de la map ne doit pas être null.");
		}
		outputStream.write((generateHeader(t)
				+ "#include \"map" + name + ".h\"\n"
				+ "\n").getBytes(StandardCharsets.UTF_8));

		for (int layerIndex = 0; layerIndex < layerCount; layerIndex++) {
			final Layer layer = t.getLayers().get(layerIndex);
			final int tileCount = layer.getWidth() * layer.getHeight();
			outputStream.write(("static const uint8_t map" + pascalCasedName + "Layer" + layerIndex + '[' + tileCount + "] = {").getBytes(StandardCharsets.UTF_8));
			int lineLength = 0;
			for (int index = 0; index < tileCount; index++) {
				int tile = getUint8Tile(layer, index);
				if (lineLength == 0) {
					if (index > 0) {
						outputStream.write(',');
					}
					outputStream.write(("\n    " + tile).getBytes(StandardCharsets.UTF_8));
					lineLength += getTileLength(tile) + 4;
				} else {
					outputStream.write((", " + tile).getBytes(StandardCharsets.UTF_8));
					lineLength += getTileLength(tile) + 2;
					if (lineLength >= 80) {
						lineLength = 0;
					}
				}
			}
			outputStream.write((tileCount > 0 ? "\n}\n" : "}\n").getBytes(StandardCharsets.UTF_8));
		}

		outputStream.write(("\n"
				+ MAP_TYPE + " * _Nonnull createMap" + pascalCasedName + "(void) {\n"
				+ "    " + MAP_TYPE + " *self = playdate->system->realloc(NULL, sizeof(" + MAP_TYPE + "));\n"
				+ "    *self = (" + MAP_TYPE + ") {\n"
				+ "        MELIntSizeMake(" + t.getPalette().getTileSize() + ", " + t.getPalette().getTileSize() + "),\n"
				+ "        &palette" + Names.normalizeName(palette, Names::toPascalCase) + "Hitbox,\n"
				+ "        " + layerCount + ",\n"
				+ "        playdate->system->realloc(NULL, sizeof(" + LAYER_TYPE + ") * " + layerCount + ")\n"
				+ "    };\n").getBytes(StandardCharsets.UTF_8));

		for (int layerIndex = 0; layerIndex < layerCount; layerIndex++) {
			final Layer layer = t.getLayers().get(layerIndex);
			final int tileCount = layer.getWidth() * layer.getHeight();
			outputStream.write(("    self->layers[" + layerIndex + "] = (" + LAYER_TYPE + ") {\n"
				+ "        self,\n"
				+ "        MELIntSizeMake(" + layer.getWidth() + ", " + layer.getHeight() + "),\n"
				+ "        MELPointMake(" + layer.getScrollRate().getX() + "f, " + layer.getScrollRate().getY() + "f),\n"
				+ "        " + tileCount + ",\n"
				+ "        map" + pascalCasedName + "Layer" + layerIndex + "\n"
				+ "    };\n").getBytes(StandardCharsets.UTF_8));
		}

		outputStream.write(("    return self;\n"
				+ "}\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(TileMap t) {
		return "map" + Names.normalizeName(t, Names::toLowerCase) + ".c";
	}

	private static int getUint8Tile(Layer layer, int index) {
		int tile = layer.getTile(index % layer.getWidth(), index / layer.getWidth());
		return tile != -1 ? tile : 0xFF;
	}

	private static int getTileLength(int tile) {
		if (tile < 10) {
			return 1;
		} else if (tile < 100) {
			return 2;
		} else {
			return 3;
		}
	}

}
