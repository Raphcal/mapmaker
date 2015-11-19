package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.HasPropertyChangeListeners;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.LayerPlugin;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Permet de dessiner la hitbox de l'objet en cours d'édition.
 * <p>
 * 
 * </p>
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class HitboxTool extends MouseAdapter implements Tool {
	
	private final Grid grid;
	private TileLayer hitboxLayer;
	
	private HitboxLayerPlugin hitboxPlugin;

	public HitboxTool(Grid grid) {
		this.grid = grid;
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
	
	private void redrawHitbox() {
		hitboxLayer.clear();
		if (hitboxPlugin != null && hitboxPlugin.getHitbox() != null) {
			hitboxLayer.setTiles(hitboxPlugin.getHitbox(), 0);
		}
	}
	
}
