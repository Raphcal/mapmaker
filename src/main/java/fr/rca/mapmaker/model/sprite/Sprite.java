package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
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
	private int size = 32;
	private final Map<String, List<TileLayer>> animations;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public Sprite() {
		this.palette = AlphaColorPalette.getDefaultColorPalette();
		this.animations = new HashMap<String, List<TileLayer>>();
	}

	public Sprite(int size, Map<String, List<TileLayer>> animations) {
		this.palette = AlphaColorPalette.getDefaultColorPalette();
		this.size = size;
		this.animations = animations;
	}

	public void morphTo(Sprite sprite) {
		final ColorPalette oldPalette = getPalette();
		final int oldSize = getSize();
		
		this.palette = sprite.palette;
		this.size = sprite.size;
		
		propertyChangeSupport.firePropertyChange("palette", oldPalette, getPalette());
		propertyChangeSupport.firePropertyChange("size", oldSize, getSize());
	}
	
	public void merge(Sprite sprite) {
		this.animations.putAll(sprite.animations);
		morphTo(sprite);
	}
	
	public void add(String animation, TileLayer layer) {
		List<TileLayer> layers = animations.get(animation);
		if(layers == null) {
			layers = new ArrayList<TileLayer>();
			animations.put(animation, layers);
		}
		layers.add(layer);
	}
	
	public boolean contains(String animation) {
		return animations.containsKey(animation);
	}
	
	public List<TileLayer> get(String animation) {
		List<TileLayer> layers = animations.get(animation);
		if(layers == null) {
			layers = new ArrayList<TileLayer>();
			animations.put(animation, layers);
		}
		return layers;
	}
	
	public void set(String animation, List<TileLayer> frames) {
		animations.put(animation, frames);
	}
	
	public void clear() {
		animations.clear();
	}
	
	public TileLayer getDefaultLayer() {
		final double[] favoriteDirections = {0.0, 3.14, 4.71, 1.57};
		
		for(final Animation animation : Animation.getDefaultAnimations()) {
			for(final double direction : favoriteDirections) {
				final List<TileLayer> layers = animations.get(animation.getNameForDirection(direction));
				if(layers != null && !layers.isEmpty()) {
					return layers.get(0);
				}
			}
		}
		
		return null;
	}
	
	public ColorPalette getPalette() {
		return palette;
	}
	
	public int getSize() {
		return size;
	}

	public Map<String, List<TileLayer>> getAnimations() {
		return animations;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
	
}
