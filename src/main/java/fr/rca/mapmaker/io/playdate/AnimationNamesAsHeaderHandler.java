package fr.rca.mapmaker.io.playdate;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class AnimationNamesAsHeaderHandler extends CodeDataHandler<List<String>> {

	@Override
	public void write(List<String> t, OutputStream outputStream) throws IOException {
		outputStream.write((generateHeader(t)
				+ "#ifndef animationnames_h\n"
				+ "#define animationnames_h\n"
				+ "\n"
				+ "#define kAnimationNameCount " + t.size() + "\n"
				+ "\n"
				+ "typedef enum {\n").getBytes(StandardCharsets.UTF_8));

		for (String animationName : t) {
			outputStream.write(("    AnimationName" + Names.toPascalCase(animationName) + ",\n").getBytes(StandardCharsets.UTF_8));
		}

		outputStream.write(("} AnimationName;\n"
				+ "\n"
				+ "#endif /* animationnames_h */\n").getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public List<String> read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String fileNameFor(List<String> t) {
		return "animationnames.h";
	}

}
