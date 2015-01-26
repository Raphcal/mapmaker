package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.Layer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Sprite {
	private int width;
	private int height;
	private final Map<String, List<Layer>> animations = new HashMap<String, List<Layer>>();

	public Sprite() {
	}
	
	public void add(String animation, Layer layer) {
		List<Layer> layers = animations.get(animation);
		if(layers == null) {
			layers = new ArrayList<Layer>();
			animations.put(animation, layers);
		}
		layers.add(layer);
	}
	
	public void set(String animation, int index, Layer layer) {
		List<Layer> layers = animations.get(animation);
		if(layers == null) {
			layers = new ArrayList<Layer>();
			animations.put(animation, layers);
		}
		if(index == layers.size()) {
			layers.add(layer);
		} else {
			layers.set(index, layer);
		}
	}
	
	public List<Layer> get(String animation) {
		List<Layer> layers = animations.get(animation);
		if(layers == null) {
			layers = new ArrayList<Layer>();
			animations.put(animation, layers);
		}
		return layers;
	}
	
	public Layer get(String animation, int index) {
		return animations.get(animation).get(index);
	}

	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		this.width = width;
	}

	public int getHeight() {
		return height;
	}

	public void setHeight(int height) {
		this.height = height;
	}

}
