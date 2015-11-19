package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.HasPropertyChangeListeners;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.LayerPlugin;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Rectangle;
import java.awt.Shape;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;

/**
 * Permet de dessiner la hitbox de l'objet en cours d'édition.
 * <p>
 * 
 * </p>
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class HitboxTool extends AbstractShapeFillTool {
	
	private TileLayer hitboxLayer;

	public HitboxTool(Grid grid) {
		super(grid);
		
		final Layer layer = grid.getActiveLayer();
		if (layer instanceof HasPropertyChangeListeners) {
			((HasPropertyChangeListeners) layer).addPropertyChangeListener("plugin", new PropertyChangeListener() {
				@Override
				public void propertyChange(PropertyChangeEvent evt) {
					final LayerPlugin plugin = (LayerPlugin) evt.getNewValue();
					if (plugin instanceof HitboxLayerPlugin) {
						// TODO: Dessiner la hitbox
					}
				}
			});
		}
	}

	@Override
	protected Shape createShape(int x, int y, int width, int height) {
		return new Rectangle(x, y, width, height);
	}

	@Override
	public void setup() {
		getGrid().getTileMap().add(hitboxLayer);
	}

	@Override
	public void reset() {
		getGrid().getTileMap().remove(hitboxLayer);
	}
	
}
