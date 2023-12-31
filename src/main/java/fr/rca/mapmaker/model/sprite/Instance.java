package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.operation.VariableDeclarationParser;
import fr.rca.mapmaker.ui.ImageRenderer;
import fr.rca.mapmaker.util.CanBeDirty;
import fr.rca.mapmaker.util.CleanEdge;
import fr.rca.mapmaker.util.Random;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.SwingWorker;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Getter
@Setter
@EqualsAndHashCode(callSuper = false)
public class Instance extends JComponent implements CanBeDirty {

	private static final int TILE_SIZE = 1;

	private long id;

	/**
	 * Projet parent.
	 */
	private Project project;

	/**
	 * Numéro du sprite.
	 */
	private int index;

	private BufferedImage image;
	private Point point;
	/**
	 * Numéro de la couche où est affiché l'instance du sprite.
	 */
	private int zIndex;
	private boolean unique;

	private double zoom = 1.0;

	private Direction direction = Direction.RIGHT;
	private String animationName;
	private String oldAnimationName;

	private final Map<String, Double> variables = new LinkedHashMap<String, Double>();

	/**
	 * Script d'initialisation de l'instance.
	 * <p/>
	 * Doit contenir une méthode <code>Load(<i>sprite</i>)</code> pour
	 * initialiser une instance.
	 */
	private String script;

	private SwingWorker<Void, Void> worker;

	/**
	 * <code>true</code> si modifiée depuis le dernier enregistrement.
	 */
	private boolean dirty;

	public Instance() {
		// Vide.
	}

	public Instance(int index, Project project, Point location) {
		this.id = Random.nextLong();
		this.project = project;
		this.index = index;
		this.point = location;
		updateSprite();
	}

	public Instance(int index, int x, int y, boolean unique, String script, int zIndex) {
		this.id = Random.nextLong();
		this.index = index;
		this.point = new Point(x, y);
		this.unique = unique;
		this.script = script;
		this.zIndex = zIndex;
		updateSprite();
	}

	public Instance(Instance other) {
		this.id = other.id;
		this.index = other.index;
		this.project = other.project;
		this.point = new Point(other.point.x, other.point.y);
		this.unique = other.unique;
		this.script = other.script;
		this.zIndex = other.zIndex;
		updateSprite();
	}

	private void updateSprite() {
		final Sprite sprite = getSprite();

		final TileLayer defaultLayer;
		if (sprite != null) {
			updateBounds();
			defaultLayer = sprite.getDefaultLayer(project.getAnimationNames(), animationName);
			oldAnimationName = animationName;
		} else {
			defaultLayer = null;
		}

		if (worker != null) {
			worker.cancel(true);
		}
		SwingWorker<Void, Void> aWorker = new SwingWorker() {
			BufferedImage result;

			@Override
			protected Void doInBackground() throws Exception {
				result = drawLayer(sprite, defaultLayer);
				return null;
			}

			@Override
			protected void done() {
				if (isCancelled()) {
					return;
				}
				worker = null;
				image = result;
				repaint();
			}
		};
		aWorker.execute();
		worker = aWorker;
	}

	private BufferedImage drawLayer(final Sprite sprite, final TileLayer defaultLayer) {
		final ImageRenderer renderer = new ImageRenderer();
		if (sprite != null && defaultLayer != null) {
			ColorPalette palette = sprite.getPalette();
			if (palette == null) {
				final TileMap map = getMap();
				palette = map != null ? map.getColorPalette() : project.getColorPalette();
			}
			final TileLayer layerToDraw;
			Dimension dimension = getDimension();
			if (!dimension.equals(defaultLayer.getDimension())) {
				layerToDraw = CleanEdge.builder()
						.palette(palette)
						.dimension(dimension)
						.slope(true)
						.cleanUpSmallDetails(true)
						.build()
						.shaded(defaultLayer);
			} else {
				layerToDraw = defaultLayer;
			}
			return renderer.renderImage(layerToDraw, palette, TILE_SIZE);
		} else {
			final int width = sprite != null ? sprite.getWidth() : 32;
			final int height = sprite != null ? sprite.getHeight() : 32;
			return renderer.renderImage(width, height, TILE_SIZE);
		}
	}

	public void updateBounds() {
		updateBounds(getDimension());
	}

	private void updateBounds(Dimension dimension) {
		setPreferredSize(dimension);
		setBounds((int) (point.x * zoom), (int) (point.y * zoom), dimension.width, dimension.height);
	}

	public void previewTranslation(int x, int y) {
		final Dimension dimension = getDimension();

		int translationX = (int) ((point.x + x) * zoom);
		int translationY = (int) ((point.y + y) * zoom);
		setBounds(translationX, translationY, dimension.width, dimension.height);
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Dimension dimension = getDimension();

		if (!getPreferredSize().equals(dimension)) {
			updateBounds(dimension);
		}

		if (animationName != null && !animationName.equals(oldAnimationName)) {
			updateSprite();
		}

		final Point topLeft = new Point(0, 0);
		if (direction == Direction.LEFT) {
			topLeft.x = dimension.width;
			dimension.width = -dimension.width;
		}
		else if (direction == Direction.UP) {
			topLeft.y = dimension.height;
			dimension.height = -dimension.height;
		}

		g.drawImage(image, topLeft.x, topLeft.y, dimension.width, dimension.height, null);
	}

	/**
	 * Met à jour le cache et demande un repaint.
	 */
	public void redraw() {
		updateSprite();
		repaint();
	}

	public void setIndex(int index) {
		this.index = index;
		updateSprite();
	}

	public void setProject(Project project) {
		this.project = project;
		updateSprite();
	}

	public void setPoint(Point point) {
		final String oldPointInfo = getPointInfo();
		final String oldCenterInfo = getCenterInfo();

		this.point = point;
		setDirty(dirty);

		firePropertyChange("pointInfo", oldPointInfo, getPointInfo());
		firePropertyChange("centerInfo", oldCenterInfo, getCenterInfo());
	}

	public String getPointInfo() {
		if (point == null) {
			return "-";
		}
		return point.x + " x " + point.y;
	}

	public String getCenterInfo() {
		final Sprite sprite = getSprite();
		if (point == null || sprite == null) {
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
		if (project != null && index >= 0 && index < project.getSprites().size()) {
			return project.getSprites().get(index);
		} else {
			return null;
		}
	}

	@Nullable
	public TileMap getMap() {
		if (project == null) {
			return null;
		}
		for (final TileMap mapAndInstances : project.getMaps()) {
			if (mapAndInstances.getSpriteInstances().contains(this)) {
				return mapAndInstances;
			}
		}
		return null;
	}

	@Nullable
	public String getScript() {
		return script;
	}

	public void setScript(String script) {
		this.script = script;
		setDirty(dirty);
		// Repaint appelle getDimension qui va mettre à jour les variables.
		repaint();
	}

	@NotNull
	public Map<String, Double> getVariables() {
		return variables;
	}

	public boolean hasVariable() {
		return !variables.isEmpty();
	}

	@NotNull
	public Direction getDirection() {
		return direction;
	}

	public Dimension getDimension() {
		final Sprite sprite = getSprite();

		if (sprite == null) {
			return new Dimension(32, 32);
		}

		runScript();

		final Double variableWidth = variables.get("width");
		final Double variableHeight = variables.get("height");

		final int width = (int) (zoom * (variableWidth == null ? sprite.getWidth() : variableWidth));
		final int height = (int) (zoom * (variableHeight == null ? sprite.getHeight() : variableHeight));

		return new Dimension(width, height);
	}

	public void runScript() {
		variables.clear();
		VariableDeclarationParser.parse(script, project).execute(this);
	}

}
