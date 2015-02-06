
package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.editor.SpriteEditor;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.List;
import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class SpritePalette implements EditablePalette {
	
	/**
	 * Sprite actuellement sélectionné.
	 */
	private int selectedTile;
	
	/**
	 * Nom de la palette.
	 */
	private String name;

	/**
	 * Liste des sprites présents dans la palette.
	 */
	private List<Sprite> sprites;

	public SpritePalette() {
		sprites = new ArrayList<Sprite>();
	}

	public SpritePalette(List<Sprite> sprites) {
		this.sprites = sprites;
	}
	
	@Override
	public void paintTile(Graphics g, int tile, int refX, int refY, int size) {
		if(tile >= 0 && tile < sprites.size()) {
			final Sprite sprite = sprites.get(tile);
			final ColorPalette palette = sprite.getPalette();
			
			final TileLayer defaultLayer = sprite.getDefaultLayer();
			
			if(defaultLayer != null) {
				final int tileSize = size / defaultLayer.getWidth();
				
				for(int y = 0; y < size; y++) {
					for(int x = 0; x < size; x++) {
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
		if(!sprites.isEmpty() && selectedTile >= 0 && selectedTile < sprites.size()) {
			final Sprite sprite = sprites.get(selectedTile);
			return sprite.getSize();
		}
		return 32;
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

	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void setName(String name) {
		this.name = name;
	}

	@Override
	public void editTile(int index, JFrame parent) {
		final SpriteEditor editor = new SpriteEditor(parent);
		editor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				// TODO
			}
		});
		editor.setVisible(true);
	}
	
}
