package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.editor.TileMapEditor;
import fr.rca.mapmaker.model.Duplicatable;
import fr.rca.mapmaker.model.HasFunctionHitbox;
import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.FunctionLayerPlugin;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

public class EditableImagePalette extends AbstractEditablePalette<TileLayer> implements HasFunctionHitbox, Duplicatable<EditableImagePalette> {

	private final ColorPalette palette;

	private final ImageRenderer renderer = new ImageRenderer();

	public EditableImagePalette(int tileSize, int columns) {
		this.tileSize = tileSize;
		this.columns = columns;
		this.palette = AlphaColorPalette.getDefaultColorPalette();

		for (int index = 0; index < columns; index++) {
			sources.add(new TileLayer(tileSize, tileSize));
			renderTile(index);
		}
	}

	public EditableImagePalette(int tileSize, int columns, ColorPalette palette, List<TileLayer> tiles) {
		this.tileSize = tileSize;
		this.columns = columns;
		this.palette = palette;

		for (int index = 0; index < tiles.size(); index++) {
			sources.add(tiles.get(index));
			renderTile(index);
		}
	}

	@Override
	public void paintTile(Graphics g, int tile, int x, int y, int size) {
		if (tile >= 0 && tile < tiles.size()) {
			g.drawImage(tiles.get(tile), x, y, size, size, null);
		}
	}

	@Override
	protected BufferedImage render(TileLayer layer) {
		return renderer.renderImage(layer, palette, 1);
	}

	@Override
	public String getFunction(int index) {
		if (index < 0 || index >= sources.size()) {
			return null;
		}
		final FunctionLayerPlugin plugin = sources.get(index).getPlugin(FunctionLayerPlugin.class);
		if (plugin != null) {
			return plugin.getFunction();
		} else {
			return null;
		}
	}

	@Override
	public void setFunction(int index, String function) {
		if (function != null) {
			sources.get(index).setPlugin(new FunctionLayerPlugin(function));
		} else {
			sources.get(index).removePlugin(FunctionLayerPlugin.class);
		}
	}

	@Override
	public String getYFunction(int index) {
		final FunctionLayerPlugin plugin = sources.get(index).getPlugin(FunctionLayerPlugin.class, FunctionLayerPlugin.Y_FUNCTION_NAME);
		if (plugin != null) {
			return plugin.getFunction();
		} else {
			return null;
		}
	}

	@Override
	public void setYFunction(int index, String function) {
		if (function != null) {
			sources.get(index).setPlugin(FunctionLayerPlugin.yFunction(function));
		} else {
			sources.get(index).removePlugin(FunctionLayerPlugin.Y_FUNCTION_NAME);
		}
	}

	public ColorPalette getColorPalette() {
		return palette;
	}

	@Override
	public void editTile(final int index, java.awt.Frame parent) {
		final TileMapEditor editor = new TileMapEditor(parent);
		editor.setLayerAndPalette(sources.get(index), palette);
		editor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshSource(index);
				setDirty(true);
			}
		});
		editor.setVisible(true);
	}

	@Override
	public EditableImagePalette duplicate() {
		final List<TileLayer> duplicatedSources = new ArrayList<TileLayer>();

		for (final TileLayer source : sources) {
			duplicatedSources.add(new TileLayer(source));
		}

		final EditableImagePalette duplicate = new EditableImagePalette(tileSize, columns, AlphaColorPalette.getDefaultColorPalette(), duplicatedSources);
		duplicate.name = name + " 2";

		return duplicate;
	}

	@Override
	public TileLayer createEmptySource() {
		return new TileLayer(tileSize, tileSize);
	}

	public void addDataLayers(List<DataLayer> sources) {
		addSources(sources.stream()
				.map(TileLayer::new)
				.collect(Collectors.toList()));
		setDirty(true);
	}
}
