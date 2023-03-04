package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.editor.InstanceInspector;
import fr.rca.mapmaker.model.palette.SpritePalette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import javax.swing.JComponent;
import javax.swing.JMenuItem;
import javax.swing.JPopupMenu;
import javax.swing.border.LineBorder;
import lombok.Getter;
import lombok.Setter;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
@Getter @Setter
public class SpriteTool extends MouseAdapter implements Tool {

	private static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("resources/language"); // NO18N

	private Project project;
	private JComponent spriteLayer;
	private Grid spritePaletteGrid;
	private List<Instance> instances;
	private int zIndex;
	private Map<Instance, MouseAdapter> mouseAdapters;
	private double zoom;

	private final InstanceInspector inspector = new InstanceInspector(null, false);

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

		if (project != null) {
			setInstances(project.getInstances());
		} else {
			setInstances(new ArrayList<Instance>());
		}
	}

	public void setInstances(List<Instance> instances) {
		if (this.instances != null) {
			for (final Instance instance : this.instances) {
				unregisterInsance(instance);
			}
		}

		this.instances = instances;

		if (instances != null) {
			for (final Instance instance : instances) {
				registerInstance(instance);
			}
		}
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
	}

	public SpritePalette getPalette() {
		if (spritePaletteGrid != null) {
			return (SpritePalette) spritePaletteGrid.getTileMap().getPalette();
		} else {
			return null;
		}
	}

	private void registerInstance(final Instance instance) {
		final MouseAdapter adapter = createMouseAdapter(instance);
		instance.addMouseListener(adapter);
		instance.addMouseMotionListener(adapter);
		instance.setZoom(zoom);
		instance.setComponentPopupMenu(createPopupMenu(instance));

		spriteLayer.add(instance);
		mouseAdapters.put(instance, adapter);
	}

	private void unregisterInsance(final Instance instance) {
		instance.removeMouseListener(mouseAdapters.get(instance));
		instance.removeMouseMotionListener(mouseAdapters.remove(instance));
		spriteLayer.remove(instance);
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		final Sprite sprite = getPalette().getSelectedSprite();
		if (sprite != null) {
			final int mouseX = (int) ((double) e.getX() / zoom);
			final int mouseY = (int) ((double) e.getY() / zoom);

			final int width = sprite.getWidth();
			final int height = sprite.getHeight();

			final int x = (mouseX / width) * width;
			final int y = (mouseY / height) * height;

			final Instance instance = new Instance(getPalette().getSelectedTile(), project, new Point(x, y));
			instance.setZIndex(zIndex);
			instances.add(instance);

			registerInstance(instance);

			spriteLayer.repaint(instance.getBounds());
		}
	}

	@Override
	public void setup() {
		// Pas d'action.
	}

	@Override
	public void reset() {
		// Pas d'action.
	}

	private MouseAdapter createMouseAdapter(final Instance instance) {
		return new MouseAdapter() {
			private Point startPoint;
			private Point originalPoint;

			@Override
			public void mouseEntered(MouseEvent e) {
				instance.setBorder(new LineBorder(Color.BLACK, 1));
			}

			@Override
			public void mouseExited(MouseEvent e) {
				instance.setBorder(null);
			}

			@Override
			public void mouseDragged(MouseEvent e) {
				if (startPoint == null) {
					startPoint = e.getLocationOnScreen();
					originalPoint = instance.getPoint();
				}

				final int translationX = (int) ((double) (e.getXOnScreen() - startPoint.getX()) / zoom);
				final int translationY = (int) ((double) (e.getYOnScreen() - startPoint.getY()) / zoom);

				instance.setPoint(new Point(originalPoint.x + translationX, originalPoint.y + translationY));
				instance.updateBounds();
				instance.repaint();
			}

			@Override
			public void mousePressed(MouseEvent e) {
				inspector.setInstance(instance);
				maybeShowPopupMenu(instance, e);
			}

			@Override
			public void mouseReleased(MouseEvent e) {
				maybeShowPopupMenu(instance, e);
				startPoint = null;
				originalPoint = null;
			}

			@Override
			public void mouseClicked(MouseEvent e) {
				inspector.setInstance(instance);
			}
		};
	}

	private void maybeShowPopupMenu(Instance instance, MouseEvent e) {
		if (e.isPopupTrigger()) {
			instance.getComponentPopupMenu().show(instance, e.getX(), e.getY());
		}
	}

	private JPopupMenu createPopupMenu(final Instance instance) {
		final JMenuItem inspectMenuItem = new JMenuItem(LANGUAGE.getString("inspector.inspect"));
		inspectMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				inspector.setInstance(instance);
				inspector.setVisible(true);
			}
		});

		final JMenuItem removeMenuItem = new JMenuItem(LANGUAGE.getString("popupmenu.instance.remove"));
		removeMenuItem.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				project.getInstances().remove(instance);
				instance.setComponentPopupMenu(null);
				unregisterInsance(instance);
				spriteLayer.repaint(instance.getBounds());
			}
		});

		final JPopupMenu popupMenu = new JPopupMenu();
		popupMenu.add(inspectMenuItem);
		popupMenu.add(removeMenuItem);
		return popupMenu;
	}
}
