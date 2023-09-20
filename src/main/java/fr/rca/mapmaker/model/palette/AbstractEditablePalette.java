package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.util.CanBeDirty;
import java.awt.Dimension;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.List;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe abstraite simplifiant la création d'une palette éditable.
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public abstract class AbstractEditablePalette<T> implements EditablePalette, HasSizeChangeListeners, CanBeDirty {

	protected String name;

	protected final List<BufferedImage> tiles = new ArrayList<>();
	protected final List<T> sources = new ArrayList<>();

	protected int tileSize;
	protected int columns;
	private int selectedTile;

	private final List<SizeChangeListener> sizeChangeListeners = new ArrayList<SizeChangeListener>();

	/**
	 * <code>true</code> si modifiée depuis le dernier enregistrement.
	 */
	@Getter @Setter
	private boolean dirty;

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public String toString() {
		if (name != null) {
			return name;
		} else {
			return super.toString();
		}
	}

	@Override
	public void removeTile(int index) {
		if (index >= 0 && index < sources.size()) {
			sources.set(index, createEmptySource());
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
		for (int i = 0; i < count; i++) {
			final T layer = createEmptySource();
			sources.add(index, layer);
			tiles.add(index, render(layer));
		}

		fireSizeChanged(oldSize, getSize());
	}

	@Override
	public void removeRow() {
		remove(selectedTile - selectedTile % columns, columns);
	}

	public void remove(int index, int count) {
		final Dimension oldSize = getSize();

		// Suppression des tuiles
		for (int i = 0; i < count; i++) {
			sources.remove(index);
			tiles.remove(index);
		}

		fireSizeChanged(oldSize, getSize());
	}

	protected Dimension getSize() {
		return new Dimension(columns, sources.size() / columns);
	}

	@Override
	public int getTileSize() {
		return tileSize;
	}

	@Override
	public int getTileSize(int tile) {
		return getTileSize();
	}

	public int getColumns() {
		return columns;
	}

	public void setColumns(int columns) {
		this.columns = columns;
	}

	@Override
	public int size() {
		return tiles.size();
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
	public boolean isEditable() {
		return true;
	}

	@Override
	public void refresh() {
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
		for (final SizeChangeListener listener : sizeChangeListeners) {
			listener.sizeChanged(this, oldSize, newSize);
		}
	}

	public void add(BufferedImage tile) {
		tiles.add(tile);
	}

	public void addSources(List<T> sources) {
		// TODO: Faire une copie des sources ?
		final Dimension oldSize = getSize();

		final int firstSource = this.sources.size();
		this.sources.addAll(sources);
		for (int index = firstSource; index < this.sources.size(); index++) {
			renderTile(index);
		}

		fireSizeChanged(oldSize, getSize());
	}

	public BufferedImage get(int index) {
		return tiles.get(index);
	}

	public T getSource(int index) {
		return sources.get(index);
	}

	public void refreshSource(int index) {
		if (index >= sources.size() - columns) {
			final Dimension oldSize = getSize();

			for (int i = 0; i < columns; i++) {
				sources.add(createEmptySource());
				renderTile(sources.size() - 1);
			}

			fireSizeChanged(oldSize, getSize());
		}

		renderTile(index);
	}

	public final void renderTile(int index) {
		final BufferedImage image = render(sources.get(index));

		if (tiles.size() == index) {
			tiles.add(image);

		} else if (tiles.size() > index) {
			tiles.set(index, image);

		} else {
			throw new IndexOutOfBoundsException("L'index " + index + " n'existe pas.");
		}
	}

	protected abstract BufferedImage render(T t);

	protected abstract T createEmptySource();

}
