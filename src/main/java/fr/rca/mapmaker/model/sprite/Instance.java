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
	
	private double zoom = 1.0;

	// TODO: Ajouter la possibilité de définir un script d'initialisation pour
	// chaque instance.
	// Voir comment stocker des données / créer des variables depuis le Lua.
	// Passer une map en argument et la donner dans Update() ?
	private String scriptFile;
	
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
	
	public void redraw() {
		updateSprite();
		repaint();
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
		updateBounds();
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
			updateBounds();
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
	
	private void updateBounds() {
		final Sprite sprite = getSprite();
		
		setPreferredSize(new Dimension((int) (sprite.getWidth() * zoom), (int) (sprite.getHeight() * zoom)));
		setBounds((int) (point.x * zoom), (int) (point.y * zoom), (int) (sprite.getWidth() * zoom), (int) (sprite.getHeight() * zoom));
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, (int) (image.getWidth() * zoom), (int) (image.getHeight() * zoom), null);
	}
}
