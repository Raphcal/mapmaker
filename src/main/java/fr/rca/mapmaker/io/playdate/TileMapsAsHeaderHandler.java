package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.TileMap;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapsAsHeaderHandler extends CodeDataHandler<List<TileMap>> {

	@Override
	public void write(List<TileMap> t, OutputStream outputStream) throws IOException {
		outputStream.write((generateHeader(t)
				+ "#ifndef maps_h\n"
				+ "#define maps_h\n"
				+ "\n"
				+ "#ifndef __clang__\n"
				+ "#define _Nonnull\n"
				+ "#define _Nullable\n"
				+ "#endif\n"
				+ "\n"
				+ "typedef enum {\n"
				+ t.stream()
						.map(map -> "    MapName" + Names.normalizeName(map, Names::toPascalCase) + ",\n")
						.collect(Collectors.joining())
				+ "} MapName;\n"
				+ "\n"
				+ "extern const int kMapNameCount;\n"
				+ "extern const char * _Nonnull kMapNameFileNames[" + t.size() + "];\n"
				+ "\n").getBytes(StandardCharsets.UTF_8));

		if (Optional.ofNullable(configuration).map(PlaydateExportConfiguration::getFlattenLayers).orElse(false)) {
			outputStream.write("LCDBitmap * _Nullable loadMapLayer(MapName mapName, unsigned int layer);\n\n".getBytes(StandardCharsets.UTF_8));
		}

		outputStream.write("#endif /* maps_h */\n".getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(List<TileMap> t) {
		return "maps.h";
	}
}
