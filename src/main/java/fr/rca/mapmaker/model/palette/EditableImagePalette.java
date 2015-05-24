package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.editor.TileMapEditor;
import fr.rca.mapmaker.model.Duplicatable;
import fr.rca.mapmaker.model.HasFunctionHitbox;
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

public class EditableImagePalette implements EditablePalette, HasSizeChangeListeners, HasFunctionHitbox, Duplicatable<EditableImagePalette> {

	private final List<BufferedImage> tiles = new ArrayList<BufferedImage>();
	private final List<TileLayer> sources = new ArrayList<TileLayer>();
	private final int tileSize;
	private final int columns;
	private int selectedTile;
	
	private String[] hitboxes;
	
	private String name;
	
	private ColorPalette palette;
	
	private final ImageRenderer renderer = new ImageRenderer();
			
	private final List<SizeChangeListener> sizeChangeListeners = new ArrayList<SizeChangeListener>();
	
	public EditableImagePalette(int tileSize, int columns) {
		this.tileSize = tileSize;
		this.columns = columns;
		this.palette = AlphaColorPalette.getDefaultColorPalette();
		this.hitboxes = new String[columns];
		
		for(int index = 0; index < columns; index++) {
			sources.add(new TileLayer(tileSize, tileSize));
			renderTile(index);
		}
	}
	
	public EditableImagePalette(int tileSize, int columns, ColorPalette palette, List<TileLayer> tiles) {
		this(tileSize, columns, palette, tiles, new String[tiles.size()]);
	}
	
	public EditableImagePalette(int tileSize, int columns, ColorPalette palette, List<TileLayer> tiles, String[] hitboxes) {
		this.tileSize = tileSize;
		this.columns = columns;
		this.palette = palette;
		this.hitboxes = hitboxes.clone();
		
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
		final BufferedImage image = render(sources.get(index));
		
		if(tiles.size() == index) {
			tiles.add(image);
		
		} else if(tiles.size() > index) {
			tiles.set(index, image);
		
		} else {
			throw new IndexOutOfBoundsException("L'index " + index + " n'existe pas.");
		}
	}
	
	private BufferedImage render(TileLayer layer) {
		return renderer.renderImage(layer, palette, 1);
	}
	
	public TileLayer getSource(int index) {
		return sources.get(index);
	}

	@Override
	public String getFunction(int index) {
		return hitboxes[index];
	}

	@Override
	public void setFunction(int index, String function) {
		hitboxes[index] = function;
	}

	public ColorPalette getColorPalette() {
		return palette;
	}

	@Override
	public void editTile(final int index, JFrame parent) {
		final TileMapEditor editor = new TileMapEditor(parent);
		editor.setLayerAndPalette(sources.get(index), palette);
		editor.setHitbox(hitboxes[index]);
		editor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshSource(index);
			}
		});
		editor.setVisible(true);
	}

	@Override
	public void removeTile(int index) {
		if(index >= 0 && index < sources.size()) {
			sources.set(index, new TileLayer(tileSize, tileSize));
			refreshSource(index);
		}
	}
	
	@Override
	public void insertRowBefore() {
		insert(selectedTile - selectedTile % columns, columns);
	}
	
	@Override
	public void insertRowAfter() {
		insert(columns + selectedTile - selectedTile % columns, columns);
	}
	
	public void insert(int index, int count) {
		final Dimension oldSize = getSize();
		
		// Ajout des nouvelles tuiles
		for(int i = 0; i < count; i++) {
			final TileLayer layer = new TileLayer(tileSize, tileSize);
			sources.add(index, layer);
			tiles.add(index, render(layer));
		}
		
		// Redimensionnement du tableau de hitbox.
		final String[] newArray = new String[sources.size()];
		System.arraycopy(hitboxes, 0, newArray, 0, index);
		System.arraycopy(hitboxes, index, newArray, index + count, sources.size() - index - count);
		hitboxes = newArray;
		
		fireSizeChanged(oldSize, getSize());
	}
	
	@Override
	public void removeRow() {
		remove(selectedTile - selectedTile % columns, columns);
	}
	
	public void remove(int index, int count) {
		final Dimension oldSize = getSize();
		
		// Suppression des tuiles
		for(int i = 0; i < count; i++) {
			sources.remove(index);
			tiles.remove(index);
		}
		
		// Redimensionnement du tableau de hitbox.
		final String[] newArray = new String[sources.size()];
		System.arraycopy(hitboxes, 0, newArray, 0, index);
		System.arraycopy(hitboxes, index + count, newArray, index, sources.size() - index - count);
		hitboxes = newArray;
		
		fireSizeChanged(oldSize, getSize());
	}
	
	public void refreshSource(int index) {
		if(index >= sources.size() - columns) {
			final Dimension oldSize = getSize();
			
			for(int i = 0; i < columns; i++) {
				sources.add(new TileLayer(tileSize, tileSize));
				renderTile(sources.size() - 1);
			}
			
			// Redimensionnement du tableau de hitbox.
			final String[] newArray = new String[sources.size()];
			System.arraycopy(hitboxes, 0, newArray, 0, hitboxes.length);
			hitboxes = newArray;
			
			fireSizeChanged(oldSize, getSize());
		}
		
		renderTile(index);
	}
	
	private Dimension getSize() {
		return new Dimension(columns, sources.size() / columns);
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

	@Override
	public EditableImagePalette duplicate() {
		final List<TileLayer> duplicatedSources = new ArrayList<TileLayer>();
		final String[] duplicatedHitboxes = hitboxes.clone();
		
		for(final TileLayer source : sources) {
			final TileLayer duplicate = new TileLayer(source.getWidth(), source.getHeight());
			duplicate.setName(source.toString());
			duplicate.setScrollRate(source.getScrollRate());
			duplicate.restoreData(source.copyData(), null);
			
			duplicatedSources.add(duplicate);
		}
		
		final EditableImagePalette duplicate = new EditableImagePalette(tileSize, columns, AlphaColorPalette.getDefaultColorPalette(), duplicatedSources, duplicatedHitboxes);
		duplicate.name = name + " 2";
		
		return duplicate;
	}
}
