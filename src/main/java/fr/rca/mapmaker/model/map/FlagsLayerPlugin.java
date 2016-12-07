package fr.rca.mapmaker.model.map;

/**
 *
 * @author rca
 */
public class FlagsLayerPlugin implements LayerPlugin {
    
    public static final String NAME = "flags";
    
    private byte flags;

    public FlagsLayerPlugin() {
    }

    public FlagsLayerPlugin(byte flags) {
        this.flags = flags;
    }

    @Override
    public String name() {
        return NAME;
    }

    @Override
    public LayerPlugin copy() {
        return new FlagsLayerPlugin(flags);
    }
    
}
