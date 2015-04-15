package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Sprite {
	private String name;
	private int type;
	private ColorPalette palette;
	private int width;
	private int height;
	private final Set<Animation> animations;
	
	/**
	 * Script utilisé pour gérer le mouvement de ce type de sprite.
	 * Doit définir une méthode <code>Update(<i>delta</i>)</code>.
	 */
	private String scriptFile;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public Sprite() {
		this.palette = AlphaColorPalette.getDefaultColorPalette();
		this.width = 32;
		this.height = 32;
		this.animations = new HashSet<Animation>();
	}

	public Sprite(int size, Set<Animation> animations) {
		this.palette = AlphaColorPalette.getDefaultColorPalette();
		this.width = size;
		this.height = size;
		this.animations = animations;
	}

	public Sprite(String name, int width, int height, int type, String scriptFile, Set<Animation> animations) {
		this.palette = AlphaColorPalette.getDefaultColorPalette();
		this.name = name;
		this.width = width;
		this.height = height;
		this.type = type;
		this.scriptFile = scriptFile;
		this.animations = animations;
	}

	public void morphTo(Sprite sprite) {
		final ColorPalette oldPalette = getPalette();
		final int oldWidth = width;
		final int oldHeight = height;
		
		this.palette = sprite.palette;
		this.width = sprite.width;
		this.height = sprite.height;
		
		propertyChangeSupport.firePropertyChange("palette", oldPalette, getPalette());
		propertyChangeSupport.firePropertyChange("width", oldWidth, width);
		propertyChangeSupport.firePropertyChange("height", oldHeight, height);
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
	
	public boolean isEmpty() {
		for(final Animation animation : animations) {
			if(!animation.getAnglesWithValue().isEmpty()) {
				return false;
			}
		}
		return true;
	}
	
	@Nullable
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
	
	@Nullable
	public ColorPalette getPalette() {
		return palette;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public int getType() {
		return type;
	}

	public void setType(int type) {
		this.type = type;
	}
	
	public int getWidth() {
		return width;
	}

	public void setWidth(int width) {
		final int oldWidth = this.width;
		this.width = width;
		
		propertyChangeSupport.firePropertyChange("width", oldWidth, width);
	}

	public int getHeight() {
		return height;
	}
	
	public void setHeight(int height) {
		final int oldHeight = this.height;
		this.height = height;
		
		propertyChangeSupport.firePropertyChange("height", oldHeight, height);
	}

	public Set<Animation> getAnimations() {
		return animations;
	}
	
	public String getScriptFile() {
		return scriptFile;
	}

	public void setScriptFile(String scriptFile) {
		this.scriptFile = scriptFile;
	}
	
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
	
}
