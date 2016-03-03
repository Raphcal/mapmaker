package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.DataLayer;
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
		final Operation operation = OperationParser.parse(function);
		
		final int[] tiles = layer.copyData();
		final int width = layer.getWidth();
		final int height = layer.getHeight();
		
		for (int x = 0; x < width; x++) {
			final int top = (int) operation.execute(x);
			for (int y = 0; y < top && y < height; y++) {
				tiles[y * width + x] = -1;
			}
			for (int y = Math.max(top, 0); y < height; y++) {
				if (y - top >= 0) {
					tiles[y * width + x] = tiles[(y - top) * width + x];
				}
			}
		}
		
		layer.restoreData(tiles, null);
	}
	
}
