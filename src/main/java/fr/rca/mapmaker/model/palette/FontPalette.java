package fr.rca.mapmaker.model.palette;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.image.BufferedImage;
import javax.swing.JFrame;

/**
 * Palette de lettres.
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class FontPalette extends AbstractEditablePalette<Character> {

	private Font font;
	
	@Override
	protected BufferedImage render(Character t) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	protected Character createEmptySource() {
		return ' ';
	}

	@Override
	public void editTile(int index, JFrame parent) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}

	@Override
	public void paintTile(Graphics g, int tile, int x, int y, int size) {
		throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
	}
	
	
}
