package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.model.map.TileMap;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class TileMapsAsCodeHandler extends CodeDataHandler<List<TileMap>> {

	@Override
	public void write(List<TileMap> t, OutputStream outputStream) throws IOException {
		outputStream.write((generateHeader(t)
				+ "#include \"maps.h\"\n"
				+ "\n"
				+ "const int kMapNameCount = " + t.size() + ";\n"
				+ "\n"
				+ "const char * _Nonnull kMapNameFileNames[" + t.size() + "] = {\n"
				+ t.stream()
						.map(tileMap -> "    \"map-" + Names.normalizeName(tileMap, Names::toLowerCase) + ".data\",\n")
						.collect(Collectors.joining())
				+ "};\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public String fileNameFor(List<TileMap> t) {
		return "maps.c";
	}
}
