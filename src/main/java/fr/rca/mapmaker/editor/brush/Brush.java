package fr.rca.mapmaker.editor.brush;

import fr.rca.mapmaker.model.map.DataLayer;
import java.awt.Point;

/**
 * Type de pinceau pour peindre avec l'outil <code>BrushTool</code>.
 * 
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public interface Brush {
	
	DataLayer get();
	
	Point translate(Point point);
	
}
