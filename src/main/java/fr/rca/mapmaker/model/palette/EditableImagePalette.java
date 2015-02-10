package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.editor.TileMapEditor;
import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

public class EditableImagePalette implements EditablePalette, HasSizeChangeListeners {

	private final ArrayList<BufferedImage> tiles = new ArrayList<BufferedImage>();
	private final ArrayList<TileLayer> sources = new ArrayList<TileLayer>();
	private final int tileSize;
	private final int columns;
	private int selectedTile;
	
	private String name;
	
	private ColorPalette palette;
	
	private final ImageRenderer renderer = new ImageRenderer();
			
	private ArrayList<SizeChangeListener> sizeChangeListeners = new ArrayList<SizeChangeListener>();
	
	public EditableImagePalette(int tileSize, int columns) {
		this.tileSize = tileSize;
		this.columns = columns;
		this.palette = AlphaColorPalette.getDefaultColorPalette();
		
		for(int index = 0; index < columns; index++) {
			sources.add(new TileLayer(tileSize, tileSize));
			renderTile(index);
		}
	}
	
	public EditableImagePalette(int tileSize, int columns, ColorPalette palette, List<TileLayer> tiles) {
		this.tileSize = tileSize;
		this.columns = columns;
		this.palette = palette;
		
		for(int index = 0; index < tiles.size(); index++) {
			sources.add(tiles.get(index));
			renderTile(index);
		}
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}
	
	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void paintTile(Graphics g, int tile, int x, int y, int size) {
		if(tile >= 0 && tile < tiles.size()) {
			g.drawImage(tiles.get(tile), x, y, size, size, null);
		}
	}

	@Override
	public int getTileSize() {
		return tileSize;
	}

	@Override
	public int getTileSize(int tile) {
		return getTileSize();
	}
	
	@Override
	public int size() {
		return tiles.size(); //  + (tiles.size() % columns) + columns
	}
	
	@Override
	public int getSelectedTile() {
		return selectedTile;
	}
	
	@Override
	public void setSelectedTile(int tile) {
		this.selectedTile = tile;
	}
	
	@Override
	public void refresh() {
	}

	public int getColumns() {
		return columns;
	}
	
	public void add(BufferedImage tile) {
		tiles.add(tile);
	}
	
	public BufferedImage get(int index) {
		return tiles.get(index);
	}
	
	private void renderTile(int index) {
		final BufferedImage image = renderer.renderImage(sources.get(index), palette, 1);
		
		if(tiles.size() == index) {
			tiles.add(image);
		
		} else if(tiles.size() > index) {
			tiles.set(index, image);
		
		} else {
			throw new IndexOutOfBoundsException("L'index " + index + " n'existe pas.");
		}
	}
	
	public TileLayer getSource(int index) {
		return sources.get(index);
	}

	public ColorPalette getColorPalette() {
		return palette;
	}

	@Override
	public void editTile(final int index, JFrame parent) {
		final TileMapEditor editor = new TileMapEditor(parent);
		editor.setLayerAndPalette(sources.get(index), palette);
		editor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshSource(index);
			}
		});
		editor.setVisible(true);
	}
	
	public void refreshSource(int index) {
		if(index >= sources.size() - columns) {
			final Dimension oldSize = new Dimension(columns, sources.size() / columns);
			
			for(int i = 0; i < columns; i++) {
				sources.add(new TileLayer(tileSize, tileSize));
				renderTile(sources.size() - 1);
			}
			
			final Dimension newSize = new Dimension(columns, sources.size() / columns);
			
			fireSizeChanged(oldSize, newSize);
		}
		
		renderTile(index);
	}
	
	@Override
	public void addSizeChangeListener(SizeChangeListener listener) {
		sizeChangeListeners.add(listener);
	}
	
	@Override
	public void removeSizeChangeListener(SizeChangeListener listener) {
		sizeChangeListeners.remove(listener);
	}
	
	protected void fireSizeChanged(Dimension oldSize, Dimension newSize) {
		for(final SizeChangeListener listener : sizeChangeListeners) {
			listener.sizeChanged(this, oldSize, newSize);
		}
	}

	@Override
	public String toString() {
		if(name != null) {
			return name;
		} else {
			return super.toString();
		}
	}
}
