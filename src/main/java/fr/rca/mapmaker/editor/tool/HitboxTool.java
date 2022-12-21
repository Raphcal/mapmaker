package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.ui.Grid;

/**
 * Permet de dessiner la hitbox de l'objet en cours d'édition.
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class HitboxTool extends AbstractHitboxTool<HitboxLayerPlugin> {

	public HitboxTool(Grid grid) {
		super(grid);
	}

	@Override
	public String getPluginName() {
		return HitboxLayerPlugin.NAME;
	}

	@Override
	public int getHitboxColor() {
		return 0;
	}

}
