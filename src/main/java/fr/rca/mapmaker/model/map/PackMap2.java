package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;
import java.util.Set;

/**
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public class PackMap2 implements Packer {
    
    @Override
    public void addAll(EditableImagePalette palette, Collection<Sprite> sprites, Double direction) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public boolean addAll(Set<SingleLayerTileMap> maps) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Point getPoint(SingleLayerTileMap map) {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Collection<Sprite> getSprites() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public EditableImagePalette getImagePalette() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public Map<TileLayer, SingleLayerTileMap> getTileLayerToTileMap() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }

    @Override
    public BufferedImage renderImage() {
        throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
    }
    
}
