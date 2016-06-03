package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.sprite.Sprite;
import java.util.Collection;

/**
 *
 * @author rca
 */
public class SpriteAtlas {
    // TODO: Finir l'implémentation
    
    private Object topLeft;
    private int width = 1;
    private int height = 1;
    private int margin;
    private Collection<Sprite> sprites;
    
    public SpriteAtlas(Collection<Sprite> sprites, int margin) {
        this.margin = margin;
        this.sprites = sprites;
    }
    
    private void pack() {
        
    }
    
    private static class Line {
        void divideAt(int point) {
            
        }
    }
}
