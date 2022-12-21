package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.HasPropertyChangeListeners;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Dimension;
import java.awt.Rectangle;
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

	public AbstractHitboxTool(Grid grid) {
		super(grid);
		this.hitboxLayer = new TileLayer(grid.getTileMapWidth(), grid.getTileMapHeight());

		// TODO: Faire un HitboxLayer qui se base sur HitboxLayerPlugin ?
		grid.getTileMap().addSizeChangeListener(
				(Object source, Dimension oldSize, Dimension newSize) -> hitboxLayer.resize(newSize.width, newSize.height));

		final Layer layer = grid.getActiveLayer();
		if (layer instanceof HasPropertyChangeListeners) {
			listenToLayerChanges((HasPropertyChangeListeners) layer);
		}
	}

	public abstract String getPluginName();
	public abstract int getHitboxColor();

	private void listenToLayerChanges(HasPropertyChangeListeners layer) {
		layer.addPropertyChangeListener("plugin-" + getPluginName(), (event) -> {
			hitboxPlugin = (T) event.getNewValue();
			redrawHitbox();
		});
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
