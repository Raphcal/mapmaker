package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Rectangle;
import java.awt.Shape;

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
	}

	@Override
	protected Shape createShape(int x, int y, int width, int height) {
		return new Rectangle(x, y, width, height);
	}

	@Override
	public void setup() {
		getGrid().getTileMap().add(hitboxLayer);
		getGrid().setActiveLayer(hitboxLayer);
	}

	@Override
	public void reset() {
		getGrid().getTileMap().remove(hitboxLayer);
	}
	
}
