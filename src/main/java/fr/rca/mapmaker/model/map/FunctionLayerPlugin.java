package fr.rca.mapmaker.model.map;

import org.jetbrains.annotations.Nullable;

/**
 * Permet d'attacher une fonction mathématique à une couche.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class FunctionLayerPlugin implements LayerPlugin {
	
    public static final String NAME = "function";
    
	private String function;

	public FunctionLayerPlugin() {
	}

	public FunctionLayerPlugin(String function) {
		this.function = function;
	}
	
	@Override
	public LayerPlugin copy() {
		return new FunctionLayerPlugin(function);
	}

	public @Nullable String getFunction() {
		return function;
	}

	public void setFunction(@Nullable String function) {
		this.function = function;
	}

    @Override
    public String name() {
        return NAME;
    }
	
}
