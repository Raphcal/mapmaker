package fr.rca.mapmaker.model.map;

import java.awt.Rectangle;
import lombok.EqualsAndHashCode;

/**
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
@EqualsAndHashCode(callSuper = true)
public class SecondaryHitboxLayerPlugin extends HitboxLayerPlugin {

	public static final String NAME = "secondary-hitbox";

	@Override
	public LayerPlugin copy() {
		final SecondaryHitboxLayerPlugin copy = new SecondaryHitboxLayerPlugin();

		if (getHitbox() != null) {
			copy.setHitbox(new Rectangle(getHitbox()));
		}

		return copy;
	}

	@Override
	public String name() {
		return NAME;
	}

}
