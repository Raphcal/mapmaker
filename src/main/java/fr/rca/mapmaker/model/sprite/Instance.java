package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Instance extends JComponent {
	private static final int TILE_SIZE = 1;
	
	private Project project;
	private int index;
	private BufferedImage image;
	private Point point;

	public Instance() {
	}

	public Instance(int index, Project project, Point location) {
		this.project = project;
		this.index = index;
		this.point = location;
		updateSprite();
	}

	public Instance(int index, int x, int y) {
		this.index = index;
		this.point = new Point(x, y);
		updateSprite();
	}

	public int getIndex() {
		return index;
	}

	public void setIndex(int index) {
		this.index = index;
		updateSprite();
	}

	public Project getProject() {
		return project;
	}
	
	public void setProject(Project project) {
		this.project = project;
		updateSprite();
	}

	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		this.point = point;
	}
	
	private Sprite getSprite() {
		if(project != null && index >= 0 && index < project.getSprites().size()) {
			return project.getSprites().get(index);
		} else {
			return null;
		}
	}

	private void updateSprite() {
		final Sprite sprite = getSprite();
		
		final TileLayer defaultLayer;
		if(sprite != null) {
			setPreferredSize(new Dimension(sprite.getWidth(), sprite.getHeight()));
			setBounds(point.x, point.y, sprite.getWidth(), sprite.getHeight());
			defaultLayer = sprite.getDefaultLayer();
			
		} else {
			defaultLayer = null;
		}
		
		final ImageRenderer renderer = new ImageRenderer();
		if(sprite != null && defaultLayer != null) {
			image = renderer.renderImage(defaultLayer, sprite.getPalette(), TILE_SIZE);
		} else {
			final int width = sprite != null ? sprite.getWidth(): 32;
			final int height = sprite != null ? sprite.getHeight(): 32;
			image = renderer.renderImage(width, height, TILE_SIZE);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
}
