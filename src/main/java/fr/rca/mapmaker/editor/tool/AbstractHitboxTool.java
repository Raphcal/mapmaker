package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.HasPropertyChangeListeners;
import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.util.Collection;
import java.util.Collections;
import lombok.Getter;
import lombok.Setter;

/**
 * Classe de base pour gérer les éditeurs de hitbox.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 * @param <T> Type du plugin supporté.
 */
@Getter
public abstract class AbstractHitboxTool<T extends HitboxLayerPlugin> extends AbstractShapeTool implements Tool {
	private TileLayer hitboxLayer;

	@Setter
	private Collection<TileLayer> otherHitboxes = Collections.emptyList();

	private T hitboxPlugin;

	private Layer currentLayer;

	public AbstractHitboxTool(Grid grid) {
		super(grid);

		final Layer layer = grid.getActiveLayer();
		this.hitboxLayer = new TileLayer(layer.getDimension().width, layer.getDimension().height);

		grid.addPropertyChangeListener("activeLayer", event -> listenToLayerChanges((Layer) event.getNewValue()));

		listenToLayerChanges(layer);
	}

	public abstract String getPluginName();
	public abstract int getHitboxColor();

	private void listenToLayerChanges(Layer layer) {
		final String hitboxPluginName = "plugin-" + getPluginName();
		if (currentLayer != null) {
			if (layer instanceof HasSizeChangeListeners) {
				((HasSizeChangeListeners) currentLayer).removeSizeChangeListener(this::onLayerSizeChange);
			}
			if (layer instanceof HasPropertyChangeListeners) {
				((HasPropertyChangeListeners) currentLayer).removePropertyChangeListener(hitboxPluginName, this::onHitboxPluginChange);
			}
		}
		if (layer instanceof HasSizeChangeListeners) {
			((HasSizeChangeListeners) layer).addSizeChangeListener(this::onLayerSizeChange);
		}
		if (layer instanceof HasPropertyChangeListeners) {
			((HasPropertyChangeListeners) layer).addPropertyChangeListener(hitboxPluginName, this::onHitboxPluginChange);
		}
		this.currentLayer = layer;
	}

	private void onHitboxPluginChange(PropertyChangeEvent event) {
		hitboxPlugin = (T) event.getNewValue();
		redrawHitbox();
	}

	private void onLayerSizeChange(Object source, Dimension oldSize, Dimension newSize) {
		hitboxLayer.resize(newSize.width, newSize.height);
		redrawHitbox();
	}

	@Override
	public void setup() {
		otherHitboxes.forEach(grid.getTileMap()::add);
		grid.getTileMap().add(hitboxLayer);
		grid.repaint();
	}

	@Override
	public void reset() {
		grid.getTileMap().remove(hitboxLayer);
		otherHitboxes.forEach(grid.getTileMap()::remove);
		grid.repaint();
	}

	@Override
	protected void drawShape(Rectangle rectangle, int tile, TileLayer layer) {
		if (hitboxPlugin.getHitbox() != null) {
			hitboxLayer.clear(hitboxPlugin.getHitbox());
		}

		hitboxPlugin.setHitbox(rectangle);

		final Rectangle inner = new Rectangle(rectangle.x + 1, rectangle.y + 1, rectangle.width - 2, rectangle.height - 2);
		final int color = getHitboxColor();
		hitboxLayer.setTiles(rectangle, AlphaColorPalette.getTile(color, 3));
		hitboxLayer.setTiles(inner, AlphaColorPalette.getTile(color, 5));
	}

	private void redrawHitbox() {
		hitboxLayer.clear();
		if (hitboxPlugin != null && hitboxPlugin.getHitbox() != null) {
			drawShape(hitboxPlugin.getHitbox(), 0, hitboxLayer);
		}
	}
}
