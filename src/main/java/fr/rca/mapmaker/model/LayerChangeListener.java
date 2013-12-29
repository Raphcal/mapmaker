package fr.rca.mapmaker.model;

import fr.rca.mapmaker.model.map.TileLayer;
import java.awt.Rectangle;

public interface LayerChangeListener {
	
	void layerChanged(TileLayer layer, Rectangle dirtyRectangle);
}
