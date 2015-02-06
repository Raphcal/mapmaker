package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Sprite {
	private ColorPalette palette;
	private int size;
	private final Map<String, List<TileLayer>> animations = new HashMap<String, List<TileLayer>>();

	public Sprite() {
		palette = AlphaColorPalette.getDefaultColorPalette();
	}

	public ColorPalette getPalette() {
		return palette;
	}
	
	public void add(String animation, TileLayer layer) {
		List<TileLayer> layers = animations.get(animation);
		if(layers == null) {
			layers = new ArrayList<TileLayer>();
			animations.put(animation, layers);
		}
		layers.add(layer);
	}
	
	public void set(String animation, int index, TileLayer layer) {
		List<TileLayer> layers = animations.get(animation);
		if(layers == null) {
			layers = new ArrayList<TileLayer>();
			animations.put(animation, layers);
		}
		if(index == layers.size()) {
			layers.add(layer);
		} else {
			layers.set(index, layer);
		}
	}
	
	public List<TileLayer> get(String animation, double direction) {
		return get(animation + '-' + direction);
	}
	
	public List<TileLayer> get(String animation) {
		List<TileLayer> layers = animations.get(animation);
		if(layers == null) {
			layers = new ArrayList<TileLayer>();
			animations.put(animation, layers);
		}
		return layers;
	}
	
	public TileLayer get(String animation, int index) {
		return animations.get(animation).get(index);
	}
	
	public int getSize() {
		return size;
	}
}
