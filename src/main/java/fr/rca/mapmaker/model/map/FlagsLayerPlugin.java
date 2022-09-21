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
	public int hashCode() {
		int hash = 7;
		hash = 89 * hash + this.flags;
		return hash;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj) {
			return true;
		}
		if (obj == null) {
			return false;
		}
		if (getClass() != obj.getClass()) {
			return false;
		}
		final FlagsLayerPlugin other = (FlagsLayerPlugin) obj;
		return this.flags == other.flags;
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
