package fr.rca.mapmaker.model.selection;

import java.awt.Graphics;

public interface SelectionStyle {

	void paintCursor(Graphics g, int x, int y, int size);
}
