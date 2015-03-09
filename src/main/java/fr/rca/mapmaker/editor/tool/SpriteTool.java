package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.palette.SpritePalette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.border.LineBorder;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpriteTool extends MouseAdapter implements Tool {
	private Project project;
	private JComponent spriteLayer;
	private Grid spritePaletteGrid;
	private List<Instance> instances;
	private Map<Instance, MouseAdapter> mouseAdapters;

	public SpriteTool() {
		this(null, null, null);
	}
	
	public SpriteTool(JComponent spriteLayer, Grid spritePaletteGrid, Project project) {
		this.spriteLayer = spriteLayer;
		this.spritePaletteGrid = spritePaletteGrid;
		this.instances = project != null ? project.getInstances() : new ArrayList<Instance>();
		this.mouseAdapters = new HashMap<Instance, MouseAdapter>();
	}

	public void setSpriteLayer(JComponent spriteLayer) {
		this.spriteLayer = spriteLayer;
	}

	public void setSpritePaletteGrid(Grid spritePaletteGrid) {
		this.spritePaletteGrid = spritePaletteGrid;
	}

	public void setProject(Project project) {
		this.project = project;
		
		if(project != null) {
			setInstances(project.getInstances());
		} else {
			setInstances(new ArrayList<Instance>());
		}
	}
	
	public void setInstances(List<Instance> instances) {
		if(this.instances != null) {
			for(final Instance instance : this.instances) {
				unregisterInsance(instance);
			}
		}
		
		this.instances = instances;
		
		if(instances != null) {
			for(final Instance instance : instances) {
				registerInstance(instance);
			}
		}
	}
	
	public SpritePalette getPalette() {
		return (SpritePalette) spritePaletteGrid.getTileMap().getPalette();
	}
	
	private void registerInstance(final Instance instance) {
		final MouseAdapter adapter = new MouseAdapter() {

			@Override
			public void mouseEntered(MouseEvent e) {
				instance.setBorder(new LineBorder(Color.BLACK, 1));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				instance.setBorder(null);
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				if(e.getButton() == MouseEvent.BUTTON3) {
					project.getInstances().remove(instance);
					mouseAdapters.remove(instance);
					spriteLayer.remove(instance);
					spriteLayer.repaint(instance.getBounds());
				}
			}
			
		};
		
		instance.addMouseListener(adapter);
		mouseAdapters.put(instance, adapter);
	}
	
	private void unregisterInsance(final Instance instance) {
		instance.removeMouseListener(mouseAdapters.remove(instance));
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		final Sprite sprite = getPalette().getSelectedSprite();
		if(sprite != null) {
			final int size = sprite.getSize();

			final int x = (e.getX() / size) * size;
			final int y = (e.getY() / size) * size;

			final Instance instance = new Instance(getPalette().getSelectedTile(), project, new Point(x, y));
			spriteLayer.add(instance);
			instances.add(instance);
			registerInstance(instance);

			spriteLayer.repaint(x, y, size, size);
		}
	}
	
	@Override
	public void reset() {
	}
}
