package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.Date;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapAsCodeHandler implements DataHandler<TileMap> {

	private static final String MAP_TYPE = "MELConstMap";
	private static final String LAYER_TYPE = "MELConstLayer";

	@Override
	public void write(TileMap t, OutputStream outputStream) throws IOException {
		final int layerCount = t.getLayers().size();

		Palette palette = t.getPalette();

		final SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy");
		final String name = Names.normalizeName(t, Names::toLowerCase);
		final String camelCasedName = Names.normalizeName(t, Names::toCamelCase);
		if (camelCasedName == null) {
			throw new IllegalArgumentException("Le nom de la map ne doit pas être null.");
		}
		outputStream.write(("//\n"
				+ "// map" + name + ".c\n"
				+ "//\n"
				+ "// Generated by MapMaker on " + dateFormat.format(new Date()) + ".\n"
				+ "//\n"
				+ "\n"
				+ "#include \"map" + name + ".h\"\n"
				+ "\n"
				+ MAP_TYPE + " * _Nonnull createMap" + Names.capitalize(camelCasedName) + "(void) {\n"
				+ "    " + MAP_TYPE + " *self = playdate->system->realloc(NULL, sizeof(" + MAP_TYPE + "));\n"
				+ "    *self = (" + MAP_TYPE + ") {\n"
				+ "        MELIntSizeMake(" + t.getPalette().getTileSize() + ", " + t.getPalette().getTileSize() + "),\n"
				+ "        &palette" + Names.capitalize(Names.normalizeName(palette, Names::toCamelCase)) + "Hitbox,\n"
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
				+ "        playdate->system->realloc(NULL, sizeof(uint8_t) * " + tileCount + ")\n"
				+ "    };\n").getBytes(StandardCharsets.UTF_8));
			if (tileCount == 0) {
				continue;
			}
			outputStream.write(("    {\n"
					+ "        uint8_t tiles[" + tileCount + "] = {" + getUint8Tile(layer, 0)).getBytes(StandardCharsets.UTF_8));

			for (int tileIndex = 1; tileIndex < tileCount; tileIndex++) {
				outputStream.write((", " + getUint8Tile(layer, tileIndex)).getBytes(StandardCharsets.UTF_8));
			}

			outputStream.write(("};\n"
				+ "        memcpy(self->layers[" + layerIndex + "].tiles, tiles, sizeof(tiles));\n"
				+ "    }\n").getBytes(StandardCharsets.UTF_8));
		}

		outputStream.write(("    return self;\n"
				+ "}\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public TileMap read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String fileNameFor(TileMap t) {
		return "map" + Names.normalizeName(t, Names::toLowerCase) + ".c";
	}

	private static int getUint8Tile(Layer layer, int index) {
		int tile = layer.getTile(index % layer.getWidth(), index / layer.getWidth());
		return tile != -1 ? tile : 0xFF;
	}

}
