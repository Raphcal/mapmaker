package fr.rca.mapmaker.io.playdate;

import fr.rca.mapmaker.io.BufferOutputStream;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import java.util.List;
import javax.imageio.ImageIO;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class FontHandler implements DataHandler<Sprite> {
	private static final Logger LOGGER = LoggerFactory.getLogger(FontHandler.class);

	private List<String> animationNames;

	public FontHandler(List<String> animationNames) {
		this.animationNames = animationNames;
	}

	@Override
	public void write(Sprite t, OutputStream outputStream) throws IOException {
		final BufferedImage image = PlaydateFormat.renderSprite(t, animationNames, false, false);
		final BufferOutputStream buffer = new BufferOutputStream();
		ImageIO.write(image, "png", buffer);
		final String data = Base64.getEncoder().encodeToString(buffer.getBuffer().array());

		outputStream.write(("datalen=" + data.length() + "\n").getBytes(StandardCharsets.UTF_8));
		outputStream.write(("data=" + data + "\n").getBytes(StandardCharsets.UTF_8));
		outputStream.write(("width=" + t.getWidth() + "\n").getBytes(StandardCharsets.UTF_8));
		outputStream.write(("height=" + t.getHeight() + "\n\n").getBytes(StandardCharsets.UTF_8));
		outputStream.write(("tracking=0\n\n").getBytes(StandardCharsets.UTF_8));

		for (final String animationName : animationNames) {
			final Animation animation = t.findByName(animationName);
			if (animation == null) {
				continue;
			}
			for (double angle : animation.getAnglesWithValue()) {
				for (TileLayer frame : animation.getFrames(angle)) {
					String name = frame.getName();
					if ("\\a".equals(name)) {
						name = "\u0007";
					} else if ("\\b".equals(name)) {
						name = "\u0008";
					}
					outputStream.write((name + '\t' + frame.getWidth() + "\n").getBytes(StandardCharsets.UTF_8));
				}
			}
		}
		outputStream.write("\n".getBytes(StandardCharsets.UTF_8));
	}

	@Override
	public Sprite read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported.");
	}

	@Override
	public String fileNameFor(Sprite t) {
		return "font-" + Names.normalizeName(t, Names::toLowerCase) + ".fnt";
	}
}
