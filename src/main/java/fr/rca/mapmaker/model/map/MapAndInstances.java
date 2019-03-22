package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.sprite.Instance;
import java.util.List;

/**
 * Couple une carte avec ses instances de sprites.
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class MapAndInstances {

    /**
     * Carte.
     */
    private TileMap tileMap;

    /**
     * Instance des sprites.
     */
    private List<Instance> spriteInstances;

    public MapAndInstances(TileMap tileMap, List<Instance> spriteInstances) {
        this.tileMap = tileMap;
        this.spriteInstances = spriteInstances;
    }

    public TileMap getTileMap() {
        return tileMap;
    }

    public void setTileMap(TileMap tileMap) {
        this.tileMap = tileMap;
    }

    public List<Instance> getSpriteInstances() {
        return spriteInstances;
    }

    public void setSpriteInstances(List<Instance> spriteInstances) {
        this.spriteInstances = spriteInstances;
    }

}
