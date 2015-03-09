
package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.editor.SpriteEditor;
import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.ui.Paints;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class SpritePalette implements EditablePalette, HasSizeChangeListeners {
	
	/**
	 * Sprite actuellement sélectionné.
	 */
	private int selectedTile;
	
	/**
	 * Nom de la palette.
	 */
	private String name;
	
	/**
	 * Nombre de sprites alloués quand la palette est vide ou presque pleine.
	 */
	private int columns = 4;

	/**
	 * Liste des sprites présents dans la palette.
	 */
	private List<Sprite> sprites;
	
	private final ArrayList<SizeChangeListener> sizeChangeListeners = new ArrayList<SizeChangeListener>();

	public SpritePalette() {
		sprites = new ArrayList<Sprite>();
		
		expand(columns - 1);
	}

	public SpritePalette(List<Sprite> sprites) {
		this.sprites = sprites;
		
		expand(columns - 1);
	}
	
	@Override
	public void paintTile(Graphics g, int tile, int refX, int refY, int size) {
		if(tile >= 0 && tile < sprites.size()) {
			final Sprite sprite = sprites.get(tile);
			final ColorPalette palette = sprite.getPalette();
			
			// Fond
			((Graphics2D)g).setPaint(Paints.TRANSPARENT_PAINT);
			g.fillRect(refX, refY, size, size);

			// Sprite
			final TileLayer defaultLayer = sprite.getDefaultLayer();
			
			if(defaultLayer != null) {
				final int layerSize = Math.min(defaultLayer.getWidth(), size);
				final int tileSize = layerSize / defaultLayer.getWidth();
				
				for(int y = 0; y < layerSize; y++) {
					for(int x = 0; x < layerSize; x++) {
						palette.paintTile(g, defaultLayer.getTile(x, y), x * tileSize + refX, y * tileSize + refY, tileSize);
					}
				}
			}
		}
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int getTileSize() {
		return 32;
	}

	@Override
	public int getTileSize(int tile) {
		final Sprite sprite = sprites.get(tile);
		// TODO: Faire mieux, peut-être fixer la valeur et dessiner en centré.
		return sprite.getWidth();
	}

	@Override
	public int size() {
		return sprites.size();
	}

	@Override
	public void setSelectedTile(int tile) {
		this.selectedTile = tile;
	}

	@Override
	public int getSelectedTile() {
		return selectedTile;
	}
	
	public Sprite getSelectedSprite() {
		if(selectedTile >= 0 && selectedTile < sprites.size()) {
			return sprites.get(selectedTile);
		} else {
			return null;
		}
	}

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void editTile(final int index, JFrame parent) {
		final SpriteEditor editor = new SpriteEditor(parent);
		editor.setSprite(sprites.get(index));
		editor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				expand(index);
			}
		});
		editor.setVisible(true);
	}

	@Override
	public void removeTile(int index) {
		if(index >= 0 && index < sprites.size()) {
			sprites.set(index, new Sprite());
		}
	}
	
	@Override
	public void refresh() {
		expand(columns - 1);
	}
	
	private void expand(int index) {
		if(index >= sprites.size() - columns) {
			final Dimension oldSize = new Dimension(columns, sprites.size() / columns);
			
			for(int i = 0; i < columns; i++) {
				sprites.add(new Sprite());
			}
			
			final Dimension newSize = new Dimension(columns, sprites.size() / columns);
			
			fireSizeChanged(oldSize, newSize);
		}
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
}
