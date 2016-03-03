package fr.rca.mapmaker.io.internal;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.HasVersion;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.FunctionLayerPlugin;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.List;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class EditableImagePaletteDataHandler implements DataHandler<EditableImagePalette>, HasVersion {

	private final Format format;
	private int version;

	public EditableImagePaletteDataHandler(Format format) {
		this.format = format;
	}
	
	@Override
	public void setVersion(int version) {
		this.version = version;
	}
	
	@Override
	public void write(EditableImagePalette t, OutputStream outputStream) throws IOException {
		
		Streams.write(t.toString(), outputStream);
		Streams.write(t.getTileSize(), outputStream);
		Streams.write(t.getColumns(), outputStream);
		
		final DataHandler<ColorPalette> colorPaletteHandler = format.getHandler(ColorPalette.class);
		colorPaletteHandler.write(t.getColorPalette(), outputStream);
		
		final int size = t.size();
		Streams.write(size, outputStream);
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		for(int index = 0; index < size; index++) {
			tileLayerHandler.write(t.getSource(index), outputStream);
			
			if(version >= InternalFormat.VERSION_3) {
				Streams.writeNullable(t.getFunction(index), outputStream);
			}
		}
	}

	@Override
	public EditableImagePalette read(InputStream inputStream) throws IOException {
		
		final String name = Streams.readString(inputStream);
		
		final int tileSize = Streams.readInt(inputStream);
		final int columns = Streams.readInt(inputStream);
		
		final DataHandler<AlphaColorPalette> colorPaletteHandler = format.getHandler(AlphaColorPalette.class);
		final AlphaColorPalette palette = colorPaletteHandler.read(inputStream);
		
		final int size = Streams.readInt(inputStream);
		final List<TileLayer> tiles = new ArrayList<TileLayer>();
		
		final DataHandler<TileLayer> tileLayerHandler = format.getHandler(TileLayer.class);
		for(int index = 0; index < size; index++) {
			final TileLayer layer = tileLayerHandler.read(inputStream);
			
			if(version >= InternalFormat.VERSION_3) {
				final String function = Streams.readNullableString(inputStream);
				if (function != null) {
					layer.setPlugin(new FunctionLayerPlugin(function));
				}
			}
			
			tiles.add(layer);
		}
		
		final EditableImagePalette imagePalette = new EditableImagePalette(tileSize, columns, palette, tiles);
		imagePalette.setName(name);
		
		return imagePalette;
	}
}
