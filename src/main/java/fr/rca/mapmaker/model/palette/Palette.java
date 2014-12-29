package fr.rca.mapmaker.model.palette;

import java.awt.Graphics;

public interface Palette {

	void paintTile(Graphics g, int tile, int x, int y, int size);
	int getTileSize();
	int size();
	
	void setSelectedTile(int tile);
	int getSelectedTile();
	
	boolean isEditable();
}
