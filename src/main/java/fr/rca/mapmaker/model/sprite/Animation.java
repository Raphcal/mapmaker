package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Animation {
	private static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("resources/language");
	
	private String name;
	private int frequency;
	private Map<Double, List<TileLayer>> frames;

	public Animation() {
		this(null);
	}

	public Animation(String name) {
		this.name = name;
		this.frames = new HashMap<Double, List<TileLayer>>();
		this.frequency = 12;
	}

	@Override
	public String toString() {
		return getDisplayName();
	}
	
	public Animation copy() {
		final Animation copy = new Animation();
		copy.name = name;
		copy.frequency = frequency;
		
		for(final Map.Entry<Double, List<TileLayer>> entry : frames.entrySet()) {
			final ArrayList<TileLayer> layers = new ArrayList<TileLayer>();
			for(final TileLayer layer : entry.getValue()) {
				final TileLayer layerCopy = new TileLayer(layer.getWidth(), layer.getHeight());
				layerCopy.restoreData(layer.copyData(), null);
				layers.add(layerCopy);
			}
			copy.frames.put(entry.getKey(), layers);
		}
		
		return copy;
	}

	@Override
	public int hashCode() {
		int hash = 7;
		hash = 11 * hash + (this.name != null ? this.name.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final Animation other = (Animation) obj;
		return !((this.name == null) ? (other.name != null) : !this.name.equals(other.name));
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}
	
	public String getDisplayName() {
		return LANGUAGE.getString("animation." + name);
	}

	public int getFrequency() {
		return frequency;
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
	}

	public Map<Double, List<TileLayer>> getFrames() {
		return frames;
	}

	public List<TileLayer> getFrames(double direction) {
		return frames.get(direction);
	}

	public List<TileLayer> getOrCreateFrames(double direction) {
		List<TileLayer> layers = frames.get(direction);
		if(layers == null) {
			layers = new ArrayList<TileLayer>();
			frames.put(direction, layers);
		}
		return layers;
	}

	public void setFrames(double direction, List<TileLayer> frames) {
		this.frames.put(direction, frames);
	}
	
	public Set<Double> getAnglesWithValue() {
		return frames.keySet();
	}

	public static Animation[] getDefaultAnimations() {
		return new Animation[] {
			new Animation("stand"),
			new Animation("walk"),
			new Animation("run"),
			new Animation("jump"),
			new Animation("fall"),
			new Animation("duck"),
			new Animation("raise"),
			new Animation("attack"),
			new Animation("hurt"),
			new Animation("die")
		};
	}
}
