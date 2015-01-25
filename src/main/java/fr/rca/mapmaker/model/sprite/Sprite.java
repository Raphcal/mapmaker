package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.Layer;
import java.util.ArrayList;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Sprite {
	private ArrayList<Layer> frames;
	private int size;

	public int getSize() {
		return size;
	}
}
