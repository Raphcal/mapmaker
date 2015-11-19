package fr.rca.mapmaker.model.map;

import java.awt.Rectangle;

/**
 * Permet d'ajouter une hitbox à une couche.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class HitboxLayerPlugin implements LayerPlugin {
	
	private Rectangle hitbox;

	@Override
	public LayerPlugin copy() {
		final HitboxLayerPlugin copy = new HitboxLayerPlugin();
		
		if(hitbox != null) {
			copy.hitbox = new Rectangle(hitbox);
		}
		
		return copy;
	}
	
	public Rectangle getHitbox() {
		return hitbox;
	}

	public void setHitbox(Rectangle hitbox) {
		this.hitbox = hitbox;
	}
	
}
