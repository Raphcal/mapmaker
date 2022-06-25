package fr.rca.mapmaker.model.map;

import org.jetbrains.annotations.Nullable;

/**
 * Permet d'attacher une fonction mathématique à une couche.
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class FunctionLayerPlugin implements LayerPlugin {

	public static final String NAME = "function";

	public static final String Y_FUNCTION_NAME = "yFunction";

	private String name = NAME;
	private String function;

	public FunctionLayerPlugin() {
		// Vide
	}

	public FunctionLayerPlugin(String function) {
		this.function = function;
	}

	public static FunctionLayerPlugin yFunction(String function) {
		final FunctionLayerPlugin plugin = new FunctionLayerPlugin(function);
		plugin.setName(Y_FUNCTION_NAME);
		return plugin;
	}

	@Override
	public LayerPlugin copy() {
		return new FunctionLayerPlugin(function);
	}

	public @Nullable
	String getFunction() {
		return function;
	}

	public void setFunction(@Nullable String function) {
		this.function = function;
	}

	@Override
	public String name() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

}
