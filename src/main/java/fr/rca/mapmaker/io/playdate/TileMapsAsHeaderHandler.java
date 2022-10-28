package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.MapAndInstances;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapsAsHeaderHandler extends CodeDataHandler<List<MapAndInstances>> {

	@Override
	public void write(List<MapAndInstances> t, OutputStream outputStream) throws IOException {
		outputStream.write((generateHeader(t)
				+ "#ifndef maps_h\n"
				+ "#define maps_h\n"
				+ "\n"
				+ "typedef enum {\n"
				+ t.stream()
						.map(MapAndInstances::getTileMap)
						.map(map -> "    MapName" + Names.normalizeName(map, Names::toPascalCase) + ",\n")
						.collect(Collectors.joining())
				+ "} MapName;\n"
				+ "\n"
				+ "extern const int kMapNameCount;\n"
				+ "extern const char * _Nonnull kMapNameFileNames[" + t.size() + "];\n"
				+ "\n"
				+ "#endif /* maps_h */\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(List<MapAndInstances> t) {
		return "maps.h";
	}
}
