package fr.rca.mapmaker.editor.tool;

import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import javax.swing.JComponent;

public class MagnifierTool extends MouseAdapter implements Tool {

	private static final int MIN_ZOOM = 1;
	private static final int MAX_ZOOM = 32;
	
	private final Grid grid;
	private final JComponent parent;
	
	public MagnifierTool(Grid grid, JComponent parent) {
		this.grid = grid;
		this.parent = parent;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		
		final int currentZoom = grid.getTileSize();
		
		if(e.getButton() == MouseEvent.BUTTON1) {
			
			if(currentZoom < MAX_ZOOM) {
				grid.setCustomTileSize(currentZoom + 1);
			}
			
		} else if(currentZoom > MIN_ZOOM) {
			grid.setCustomTileSize(currentZoom - 1);
		}
		
		parent.validate();
	}
	
	@Override
	public void reset() {
	}
}
