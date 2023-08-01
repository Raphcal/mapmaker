package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.TileMap;
import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;
import java.util.stream.IntStream;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
@NoArgsConstructor
@AllArgsConstructor
public class TileMapsAsCodeHandler extends CodeDataHandler<List<TileMap>> {

	private File resourceDir;

	@Override
	public void write(List<TileMap> t, OutputStream outputStream) throws IOException {
		final boolean flattenLayers = Optional.ofNullable(configuration).map(PlaydateExportConfiguration::getFlattenLayers).orElse(false);

		outputStream.write((generateHeader(t)
				+ "#include \"maps.h\"\n"
				+ (flattenLayers ? "#include \"../lib/bitmap.h\"\n" : "")
				+ "\n"
				+ "const int kMapNameCount = " + t.size() + ";\n"
				+ "\n"
				+ "const char * _Nonnull kMapNameFileNames[" + t.size() + "] = {\n"
				+ t.stream()
						.map(tileMap -> "    \"map-" + Names.normalizeName(tileMap, Names::toLowerCase) + ".data\",\n")
						.collect(Collectors.joining())
				+ "};\n").getBytes(StandardCharsets.UTF_8));

		if (flattenLayers) {
			outputStream.write(("LCDBitmap * _Nullable loadMapLayer(MapName mapName, int layer) {\n"
					+ "    switch (mapName * 100 + layer) {\n"
					+ IntStream.range(0, t.size())
						.mapToObj(mapIndex -> {
								final TileMap tileMap = t.get(mapIndex);
								return IntStream.range(0, tileMap.getLayers().size())
										.filter(layerIndex -> new File(resourceDir, getLayerFileName(tileMap, layerIndex) + ".png").canRead())
										.mapToObj(layerIndex -> "        case " + (mapIndex * 100 + layerIndex) + ":\n"
												+ "            return LCDBitmapLoadOrError(\"" + getLayerFileName(tileMap, layerIndex) + "\");\n");
						})
						.flatMap(stream -> stream)
						.collect(Collectors.joining())
					+ "        default:\n"
					+ "            return NULL;\n"
					+ "    }\n"
					+ "}").getBytes(StandardCharsets.UTF_8));
		}
	}

	private static String getLayerFileName(TileMap tileMap, int layerIndex) {
		return "map-" + Names.normalizeName(tileMap, Names::toLowerCase) + "-layer-" + layerIndex;
	}

	@Override
	public String fileNameFor(List<TileMap> t) {
		return "maps.c";
	}
}
