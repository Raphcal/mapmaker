package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.HasPropertyChangeListeners;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.LayerPlugin;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Dimension;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Permet de dessiner la hitbox de l'objet en cours d'édition.
 * <p>
 * 
 * </p>
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class HitboxTool extends AbstractShapeTool implements Tool {
	
	private TileLayer hitboxLayer;
	
	private HitboxLayerPlugin hitboxPlugin;

	public HitboxTool(Grid grid) {
		super(grid);
		this.hitboxLayer = new TileLayer(grid.getTileMapWidth(), grid.getTileMapHeight());
		
		// TODO: Faire un HitboxLayer qui se base sur HitboxLayerPlugin ?
		
		grid.getTileMap().addSizeChangeListener(new SizeChangeListener() {
			@Override
			public void sizeChanged(Object source, Dimension oldSize, Dimension newSize) {
				hitboxLayer.resize(newSize.width, newSize.height);
			}
		});
		
		final Layer layer = grid.getActiveLayer();
		if (layer instanceof HasPropertyChangeListeners) {
			((HasPropertyChangeListeners) layer).addPropertyChangeListener("plugin", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					final LayerPlugin plugin = (LayerPlugin) evt.getNewValue();
					if (plugin instanceof HitboxLayerPlugin) {
						hitboxPlugin = (HitboxLayerPlugin) plugin;
					} else {
						hitboxPlugin = null;
					}
					redrawHitbox();
				}
			});
		}
	}

	@Override
	public void setup() {
		grid.getTileMap().add(hitboxLayer);
		grid.repaint();
	}

	@Override
	public void reset() {
		grid.getTileMap().remove(hitboxLayer);
		grid.repaint();
	}

	@Override
	protected void drawShape(Rectangle rectangle, int tile, TileLayer layer) {
		if (hitboxPlugin.getHitbox() != null) {
			hitboxLayer.clear(hitboxPlugin.getHitbox());
		}
		
		hitboxPlugin.setHitbox(rectangle);
		
		final Rectangle inner = new Rectangle(rectangle.x + 1, rectangle.y + 1, rectangle.width - 2, rectangle.height - 2);
		hitboxLayer.setTiles(rectangle, AlphaColorPalette.getTile(0, 3));
		hitboxLayer.setTiles(inner, AlphaColorPalette.getTile(0, 5));
	}
	
	private void redrawHitbox() {
		hitboxLayer.clear();
		if (hitboxPlugin != null && hitboxPlugin.getHitbox() != null) {
			drawShape(hitboxPlugin.getHitbox(), 0, hitboxLayer);
		}
	}

}
