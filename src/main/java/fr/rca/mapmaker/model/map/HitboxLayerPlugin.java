package fr.rca.mapmaker.model.map;

import java.awt.Rectangle;
import lombok.AllArgsConstructor;
import lombok.EqualsAndHashCode;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.jetbrains.annotations.Nullable;

/**
 * Permet d'ajouter une hitbox à une couche.
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
@EqualsAndHashCode
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
public class HitboxLayerPlugin implements LayerPlugin {

	public static final String NAME = "hitbox";

	private @Nullable Rectangle hitbox;

	@Override
	public LayerPlugin copy() {
		final HitboxLayerPlugin copy = new HitboxLayerPlugin();

		if (hitbox != null) {
			copy.hitbox = new Rectangle(hitbox);
		}

		return copy;
	}

	@Override
	public String name() {
		return NAME;
	}

}
