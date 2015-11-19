package fr.rca.mapmaker.model.map;

import java.awt.Rectangle;
import org.jetbrains.annotations.Nullable;

/**
 * Permet d'ajouter une hitbox à une couche.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class HitboxLayerPlugin implements LayerPlugin {
	
	private Rectangle hitbox = new Rectangle(1, 1, 2, 3);

	@Override
	public LayerPlugin copy() {
		final HitboxLayerPlugin copy = new HitboxLayerPlugin();
		
		if(hitbox != null) {
			copy.hitbox = new Rectangle(hitbox);
		}
		
		return copy;
	}
	
	@Nullable
	public Rectangle getHitbox() {
		return hitbox;
	}

	public void setHitbox(@Nullable Rectangle hitbox) {
		this.hitbox = hitbox;
	}
	
}
