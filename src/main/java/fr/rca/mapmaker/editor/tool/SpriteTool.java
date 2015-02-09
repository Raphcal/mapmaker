package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.palette.SpritePalette;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.List;
import javax.swing.border.LineBorder;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpriteTool extends MouseAdapter implements Tool {
	private Grid grid;
	private Grid spritePaletteGrid;
	private List<Instance> instances;

	public SpriteTool(Grid grid, Grid spritePaletteGrid, List<Instance> instances) {
		this.grid = grid;
		this.spritePaletteGrid = spritePaletteGrid;
		this.instances = instances;
	}

	public SpritePalette getPalette() {
		return (SpritePalette) spritePaletteGrid.getTileMap().getPalette();
	}

	public void setInstances(List<Instance> instances) {
		this.instances = instances;
		
		for(final Instance instance : instances) {
			registerInstance(instance);
		}
	}
	
	private void registerInstance(final Instance instance) {
		instance.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				instance.setBorder(new LineBorder(Color.BLACK, 1));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				instance.setBorder(null);
			}
			
		});
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		final Sprite sprite = getPalette().getSelectedSprite();
		final int size = sprite.getSize();
		
		final int x = (e.getX() / size) * size;
		final int y = (e.getY() / size) * size;
		
		final Instance instance = new Instance(sprite, new Point(x, y));
		grid.add(instance);
		instances.add(instance);
		registerInstance(instance);
		
		grid.repaint(x, y, size, size);
	}
	
	@Override
	public void reset() {
	}
}
