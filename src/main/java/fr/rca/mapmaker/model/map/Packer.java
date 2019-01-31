package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Point;
import java.awt.image.BufferedImage;
import java.util.Collection;
import java.util.Map;
import java.util.Set;
import org.jetbrains.annotations.Nullable;

/**
 * Table qui agence ses objets pour tenir dans la surface donnée.
 * <p>
 * Les objets doivent-être triés dans l'ordre du plus haut au moins haut pour
 * que les résultats soient meilleurs.
 *
 * @author Raphaël Calabro (ddaeke-github@yahoo.fr)
 */
public interface Packer {
    
    /**
     * Ajoute tous les layers des objets donnés.
     * @param palette Palette d'image.
     * @param sprites Sprites.
     * @param direction Direction à exporter pour les sprites ou <code>null</code> pour tout exporter.
     */
    void addAll(EditableImagePalette palette, Collection<Sprite> sprites, Double direction);
    
    /**
     * Ajoute toutes les maps données.
     * @param maps Maps à ajouter.
     * @return
     */
    boolean addAll(Set<SingleLayerTileMap> maps);

    /**
     * 
     * @param map
     * @return 
     */
    @Nullable
    Point getPoint(SingleLayerTileMap map);

    Collection<Sprite> getSprites();
    EditableImagePalette getImagePalette();

    Map<TileLayer, SingleLayerTileMap> getTileLayerToTileMap();

    BufferedImage renderImage();
    
}
