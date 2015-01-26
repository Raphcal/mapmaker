
package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Graphics;
import java.util.List;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class SpritePalette implements Palette {
	
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
	
	@Override
	public void paintTile(Graphics g, int tile, int x, int y, int size) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public int getTileSize() {
		if(!sprites.isEmpty() && selectedTile >= 0 && selectedTile < sprites.size()) {
			final Sprite sprite = sprites.get(selectedTile);
			return 1; //sprite.getSize();
		}
		return 1;
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
	
}
