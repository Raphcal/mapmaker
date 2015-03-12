package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Function;
import fr.rca.mapmaker.ui.Grid;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class FunctionHitboxLayer {
	
	private Grid grid;
	private String function;
	private TileLayer functionLayer;
	
	private void createLayer() {
		functionLayer = Function.asTileLayer(function, grid.getTileMapWidth(), grid.getTileMapHeight());
	}
	
	public void setVisible(boolean visible) {
		if(visible) {
			grid.getTileMap().add(functionLayer);
		} else {
			grid.getTileMap().remove(functionLayer);
		}
	}
	
}
