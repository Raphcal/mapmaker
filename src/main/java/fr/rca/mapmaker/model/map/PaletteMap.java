package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.HasSelectionListeners;
import fr.rca.mapmaker.model.SelectionListener;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Point;
import java.util.ArrayList;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PaletteMap extends TileMap implements HasSelectionListeners {

	private int width;
	private Point selectedPoint;
	private final ArrayList<SelectionListener> listeners;

	public PaletteMap() {
		this.width = 1;
		this.listeners = null;
	}
	
	public PaletteMap(Palette palette, int width) {
		this.width = width;
		this.selectedPoint = new Point();
		setPalette(palette);
		
		this.listeners = new ArrayList<SelectionListener>();
	}

	public void setPaletteWidth(int width) {
		this.width = width;
		
		if(!getLayers().isEmpty()) {
			final PaletteLayer paletteLayer = (PaletteLayer) getLayers().get(0);
			paletteLayer.setWidth(width);
		}
	}
	
	@Override
	public final void setPalette(Palette palette) {
		clear();
		add(new PaletteLayer(palette, width));
		
		super.setPalette(palette);
		
		// TODO: Vérifier que la tuile existe dans la palette
		palette.setSelectedTile(getSelectedTile());
	}

	@Override
	public void addSelectionListener(SelectionListener listener) {
		listeners.add(listener);
	}

	@Override
	public void removeSelectionListener(SelectionListener listener) {
		listeners.remove(listener);
	}

	@Override
	public Point getSelection() {
		return selectedPoint;
	}
	
	public int getSelectedTile() {
		return getTileFromPoint(selectedPoint);
	}
	
	public void setSelection(Point selectedPoint) {
		normalizePoint(selectedPoint);
		
		final int tile = getTileFromPoint(selectedPoint);
		final int lastTile = getPalette().size() - 1;
		
		if(tile > lastTile)
			selectedPoint = getPointFromTile(lastTile);
		
		if(!this.selectedPoint.equals(selectedPoint)) {
			final Point oldSelection = this.selectedPoint;
			this.selectedPoint = selectedPoint;
			
			getPalette().setSelectedTile(getSelectedTile());
			fireSelectionChanged(oldSelection, selectedPoint);
		}
	}

	private int getTileFromPoint(Point point) {
		if(point != null) {
			return point.y * width + point.x;
		} else {
			return 0;
		}
	}
	
	private Point getPointFromTile(int tile) {
		return new Point(tile % width, tile / width);
	}

	private void normalizePoint(Point p) {
		if(p.x < 0) {
			p.x = 0;
		
		} else if (p.x >= width) {
			p.x = width - 1;
		}
		
		if(p.y < 0) {
			p.y = 0;
		}
	}
	
	protected void fireSelectionChanged(Point oldSelection, Point newSelection) {
		for(final SelectionListener listener : listeners)
			listener.selectionChanged(oldSelection, newSelection);
	}
}
