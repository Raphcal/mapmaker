package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.operation.VariableDeclarationParser;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.HashMap;
import java.util.Map;
import javax.swing.JComponent;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

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
	private boolean unique;
	
	private double zoom = 1.0;
	
	private Direction direction = Direction.RIGHT;

	private final Map<String, Double> variables = new HashMap<String, Double>();
	
	/**
	 * Script d'initialisation de l'instance. 
	 * <p/>
	 * Doit contenir une méthode <code>Load(<i>sprite</i>)</code> pour initialiser une instance.
	 */
	private String script;
	
	public Instance() {
	}

	public Instance(int index, Project project, Point location) {
		this.project = project;
		this.index = index;
		this.point = location;
		updateSprite();
	}
	
	public Instance(int index, int x, int y, boolean unique, String script) {
		this.index = index;
		this.point = new Point(x, y);
		this.unique = unique;
		this.script = script;
		updateSprite();
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
	
	public void updateBounds() {
		final Sprite sprite = getSprite();
		
		setPreferredSize(new Dimension((int) (sprite.getWidth() * zoom), (int) (sprite.getHeight() * zoom)));
		setBounds((int) (point.x * zoom), (int) (point.y * zoom), (int) (sprite.getWidth() * zoom), (int) (sprite.getHeight() * zoom));
	}
	
	public void previewTranslation(int x, int y) {
		final Sprite sprite = getSprite();
		
		int translationX = (int) ((point.x + x) * zoom);
		int translationY = (int) ((point.y + y) * zoom);
		setBounds(translationX, translationY, (int) (sprite.getWidth() * zoom), (int) (sprite.getHeight() * zoom));
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Double variableWidth = variables.get("width");
		final Double variableHeight = variables.get("height");
		
		final int width = (int) (zoom * (variableWidth == null ? image.getWidth() : variableWidth));
		final int height = (int) (zoom * (variableHeight == null ? image.getHeight() : variableHeight));
		
		VariableDeclarationParser.parse(script).execute(this);
		
		if (direction == Direction.RIGHT) {
			g.drawImage(image, 0, 0, width, height, null);
		} else {
			g.drawImage(image, width, 0, -width, height, null);
		}
	}
	
	/**
	 * Met à jour le cache et demande un repaint.
	 */
	public void redraw() {
		updateSprite();
		repaint();
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

	public boolean isUnique() {
		return unique;
	}

	public void setUnique(boolean unique) {
		this.unique = unique;
	}
	
	public Point getPoint() {
		return point;
	}

	public void setPoint(Point point) {
		final String oldPointInfo = getPointInfo();
		final String oldCenterInfo = getCenterInfo();
		
		this.point = point;
		
		firePropertyChange("pointInfo", oldPointInfo, getPointInfo());
		firePropertyChange("centerInfo", oldCenterInfo, getCenterInfo());
	}
	
	public String getPointInfo() {
		if(point == null) {
			return "-";
		}
		return point.x + " x " + point.y;
	}
	
	public String getCenterInfo() {
		final Sprite sprite = getSprite();
		if(point == null || sprite == null) {
			return "-";
		}
		return (point.x + sprite.getWidth() / 2) + " x " + (point.y + sprite.getHeight() / 2);
	}
	
	public void setZoom(double zoom) {
		this.zoom = zoom;
		updateBounds();
	}
	
	@Nullable
	public Sprite getSprite() {
		if(project != null && index >= 0 && index < project.getSprites().size()) {
			return project.getSprites().get(index);
		} else {
			return null;
		}
	}

	@Nullable
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
		repaint();
	}

	@NotNull
	public Map<String, Double> getVariables() {
		return variables;
	}

	@NotNull
	public Direction getDirection() {
		return direction;
	}

	public void setDirection(Direction direction) {
		this.direction = direction;
	}
	
}
