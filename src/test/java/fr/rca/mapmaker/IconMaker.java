package fr.rca.mapmaker;

import fr.rca.mapmaker.editor.TileEditor;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.ColorPalette;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;

/**
 *
 * @author daeke
 */
public class IconMaker {
	
	public static void main(String[] args) {
		
		TileEditor.createEditorDialog(new TileLayer(16, 16), ColorPalette.getDefaultColorPalette(), null, new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent ae) {
				System.exit(0);
			}
		}).setVisible(true);
	}
}
