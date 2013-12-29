package fr.rca.mapmaker.io.avm;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.io.DataHandler;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.Arrays;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileLayerDataHandler implements DataHandler<TileLayer> {

	@Override
	public void write(TileLayer t, OutputStream outputStream) throws IOException {
		throw new UnsupportedOperationException("Not supported yet.");
	}

	@Override
	public TileLayer read(InputStream inputStream) throws IOException {
		
		TileLayer layer = null;
		
		final byte[] tag = new byte[] {'A','V','M','A','2'};
		final byte[] header = new byte[5];
		inputStream.read(header);

		if(Arrays.equals(header, tag)) {
			inputStream.read();

			int width = 0;
			for(int i = 0; i < 4; i++)
				width = width * 10 + (inputStream.read() - '0');

			int height = 0;
			for(int i = 0; i < 4; i++)
				height = height * 10 + (inputStream.read() - '0');

			inputStream.read();

			layer = new TileLayer(width, height);
//			layer.setScrollRate(scrollRate);

			for(int y = 0; y < height; y++) {
				for(int x = 0; x < width; x++) {
					final StringBuilder numberBuilder = new StringBuilder();
					for(int i = 0; i < 3; i++)
						numberBuilder.append((char)inputStream.read());

					layer.setTile(x, y, Integer.parseInt(numberBuilder.toString()) - 1);
				}
			}
		}
		
		return layer;
	}
	
}
