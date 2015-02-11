package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Sprite {
	private ColorPalette palette;
	private int size;
	private Set<Animation> animations;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public Sprite() {
		this.palette = AlphaColorPalette.getDefaultColorPalette();
		this.size = 32;
		this.animations = new HashSet<Animation>();
	}

	public Sprite(int size, Set<Animation> animations) {
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
		this.animations.removeAll(sprite.animations);
		this.animations.addAll(sprite.animations);
		morphTo(sprite);
	}
	
	public boolean contains(Animation animation) {
		return animations.contains(animation);
	}
	
	public Animation get(String name) {
		Animation animation = findByName(name);
		if(animation == null) {
			animation = new Animation(name);
			animations.add(animation);
		}
		return animation;
	}
	
	private Animation findByName(String name) {
		if(name == null) {
			return null;
		}
		for(final Animation animation : animations) {
			if(name.equals(animation.getName())) {
				return animation;
			}
		}
		return null;
	}
	
	public void clear() {
		animations.clear();
	}
	
	public TileLayer getDefaultLayer() {
		final double[] favoriteDirections = {0.0, 3.14, 4.71, 1.57};
		
		for(final Animation defaultAnimation : Animation.getDefaultAnimations()) {
			final Animation animation = findByName(defaultAnimation.getName());
			if(animation != null) {
				for(final double direction : favoriteDirections) {
					final List<TileLayer> layers = animation.getFrames(direction);
					if(layers != null && !layers.isEmpty()) {
						return layers.get(0);
					}
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

	public void setSize(int size) {
		final int oldSize = this.size;
		this.size = size;
		
		propertyChangeSupport.firePropertyChange("size", oldSize, size);
	}

	public Set<Animation> getAnimations() {
		return animations;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
	
}
