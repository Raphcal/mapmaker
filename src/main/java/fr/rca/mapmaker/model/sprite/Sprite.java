package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.event.Event;
import fr.rca.mapmaker.event.EventBus;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 * Définition d'un objet actif.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Sprite {

	private String name;
	private int type;
	private ColorPalette palette = AlphaColorPalette.getDefaultColorPalette();
	private int width;
	private int height;
	private final Set<Animation> animations;
	private Distance distance = Distance.BEHIND;
	
	/**
	 * Défini si ce sprite sera inclus dans les exports MKZ.
	 */
	private boolean exportable = true;
    
    /**
     * Défini si ce sprite doit être exporté même s'il n'est pas présent dans une carte.
     */
    private boolean global;
	
	/**
	 * Script utilisé pour gérer le mouvement de ce type de sprite.
	 * Doit définir une méthode <code>Update(<i>delta</i>)</code>.
	 */
	private String scriptFile;
	
	/**
	 * Script exécuté à l'initialisation du sprite. Ses valeurs peuvent-être
	 * écrasée par le script d'initialisation de l'instance.
	 * <p/>
	 * Doit définir une méthode <code>Load(<i>sprite</i>)</code>.
	 */
	private String loadScript;

	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	public Sprite() {
		this.width = 32;
		this.height = 32;
		this.animations = new HashSet<Animation>();
	}

	public Sprite(int size, Set<Animation> animations) {
		this.width = size;
		this.height = size;
		this.animations = animations;
	}

	public Sprite(String name, int width, int height, int type, Distance distance, boolean exportable, boolean global, String loadScript, String scriptFile, Set<Animation> animations) {
        this.name = name;
        this.width = width;
        this.height = height;
		this.type = type;
		this.distance = distance;
		this.exportable = exportable;
        this.global = global;
		this.loadScript = loadScript;
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
	
	@NotNull
	public Animation get(String name) {
		Animation animation = findByName(name);
		if(animation == null) {
			animation = new Animation(name);
			animations.add(animation);
		}
		return animation;
	}

	public Animation findByName(String name) {
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
		return getDefaultLayer(Animation.ANIMATION_NAMES);
	}

	@Nullable
	public TileLayer getDefaultLayer(List<String> animationNames) {
		final double[] favoriteDirections = {0.0, 3.14, 4.71, 1.57};

		for(final String animationName : animationNames) {
			final Animation animation = findByName(animationName);
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

    public void setPalette(ColorPalette palette) {
        final ColorPalette oldPalette = this.palette;
        this.palette = palette;
        propertyChangeSupport.firePropertyChange("palette", oldPalette, palette);
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

	public String getLoadScript() {
		return loadScript;
	}

	public void setLoadScript(String loadScript) {
		this.loadScript = loadScript;
	}

	public Distance getDistance() {
		return distance;
	}

	public void setDistance(Distance distance) {
		this.distance = distance;
	}

	public boolean isExportable() {
		return exportable;
	}

	public void setExportable(boolean exportable) {
		final boolean oldExportable = this.exportable;
		this.exportable = exportable;
		
		propertyChangeSupport.firePropertyChange("exportable", oldExportable, exportable);
        
        if (oldExportable != exportable) {
            EventBus.INSTANCE.fireEvent(Event.SPRITE_CHANGED);
        }
	}

    public boolean isGlobal() {
        return global;
    }

    public void setGlobal(boolean global) {
        final boolean oldGlobal = this.global;
		this.global = global;
		
		propertyChangeSupport.firePropertyChange("global", oldGlobal, global);
        
        if (oldGlobal != global) {
            EventBus.INSTANCE.fireEvent(Event.SPRITE_CHANGED);
        }
    }
    
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
	
}
