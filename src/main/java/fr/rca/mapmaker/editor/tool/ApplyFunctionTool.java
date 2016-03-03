package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.FunctionLayerPlugin;
import fr.rca.mapmaker.model.map.HasLayerPlugin;
import fr.rca.mapmaker.operation.Operation;
import fr.rca.mapmaker.operation.OperationParser;
import javax.swing.JOptionPane;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class ApplyFunctionTool {

	public static void execute(DataLayer layer) {
		final String function = JOptionPane.showInputDialog("Fonction à appliquer (exemple : \"x * 2 + zoom(12)\") ?");
		execute(layer, function);
	}

	public static void execute(DataLayer layer, String function) {
		final Operation operation = OperationParser.parse(function);
		
		final int[] source = layer.copyData();
		final int[] destination = new int[source.length];
		final int width = layer.getWidth();
		final int height = layer.getHeight();
		
		for (int x = 0; x < width; x++) {
			final int top = (int) operation.execute(x);
			for (int y = 0; y < top && y < height; y++) {
				destination[y * width + x] = -1;
			}
			for (int y = Math.max(top, 0); y < height; y++) {
				final int sourceY = y - top;
				if (sourceY >= 0 && sourceY < height) {
					destination[y * width + x] = source[sourceY * width + x];
				} else {
					destination[y * width + x] = -1;
				}
			}
		}
		
		if (layer instanceof HasLayerPlugin) {
			((HasLayerPlugin) layer).setPlugin(new FunctionLayerPlugin(function));
		}
		layer.restoreData(destination, null);
	}
	
}
