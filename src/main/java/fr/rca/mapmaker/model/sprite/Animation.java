package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.stream.Collectors;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Animation {
	private static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("resources/language");
	
	public static final String STAND = "stand";
	public static final String WALK = "walk";
	public static final String RUN = "run";
	public static final String JUMP = "jump";
	public static final String FALL = "fall";
	public static final String BOUNCE = "bounce";
	public static final String DUCK = "duck";
	public static final String RAISE = "raise";
	public static final String APPEAR = "appear";
	public static final String DISAPPEAR = "disappear";
	public static final String ATTACK = "attack";
	public static final String HURT = "hurt";
	public static final String DIE = "die";
	public static final String SKID = "skid";
	public static final String SHAKY = "shaky";
	
	private String name;
	private int frequency;
	private Map<Double, List<TileLayer>> frames;
	private boolean looping;
	private boolean scrolling;

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
		copy.looping = looping;
		copy.scrolling = scrolling;
		
		for(final Map.Entry<Double, List<TileLayer>> entry : frames.entrySet()) {
			final List<TileLayer> layersCopy = new ArrayList<TileLayer>();
			for(final TileLayer layer : entry.getValue()) {
				layersCopy.add(new TileLayer(layer));
			}
			copy.frames.put(entry.getKey(), layersCopy);
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

	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public boolean isScrolling() {
		return scrolling;
	}

	public void setScrolling(boolean scrolling) {
		this.scrolling = scrolling;
	}

	public Set<Double> getAnglesWithValue() {
		final Set<Double> anglesWithValue = new HashSet<Double>();
		for(final Map.Entry<Double, List<TileLayer>> entry : frames.entrySet()) {
			if(entry.getValue() != null && !entry.getValue().isEmpty()) {
				anglesWithValue.add(entry.getKey());
			}
		}
		return anglesWithValue;
	}
	
	public void overrideFrameNames() {
		for(final Map.Entry<Double, List<TileLayer>> entry : frames.entrySet()) {
			final String prefix = name + '.' + entry.getKey()  + '.';
			int index = 0;
			for(final TileLayer frame : entry.getValue()) {
				frame.setName(prefix + index);
				index++;
			}
		}
	}

	public static Animation[] getDefaultAnimations() {
		return new Animation[] {
			new Animation(STAND),
			new Animation(WALK),
			new Animation(RUN),
			new Animation(SKID),
			new Animation(JUMP),
			new Animation(FALL),
			new Animation(SHAKY),
			new Animation(BOUNCE),
			new Animation(DUCK),
			new Animation(RAISE),
			new Animation(APPEAR),
			new Animation(DISAPPEAR),
			new Animation(ATTACK),
			new Animation(HURT),
			new Animation(DIE)
		};
	}

	public static final List<String> ANIMATION_NAMES = Collections.unmodifiableList(Arrays.stream(getDefaultAnimations())
			.map(Animation::getName)
			.collect(Collectors.toList()));
}
