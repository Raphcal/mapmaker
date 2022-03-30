package fr.rca.mapmaker.model.map;

import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Dimension;
import java.awt.Point;
import java.util.ArrayList;
import java.util.List;

/**
 * Couche d'une grille dessinée à partir d'une palette.
 * 
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PaletteLayer implements Layer, HasSizeChangeListeners {

	private final Palette palette;
	private int width;
	
	private final List<SizeChangeListener> sizeChangeListeners = new ArrayList<SizeChangeListener>();

	public PaletteLayer(Palette palette, int width) {
		this.palette = palette;
		setWidth(width);
		
		if(palette instanceof HasSizeChangeListeners) {
			((HasSizeChangeListeners)palette).addSizeChangeListener(new SizeChangeListener() {

				@Override
				public void sizeChanged(Object source, Dimension oldSize, Dimension newSize) {
					fireSizeChanged(oldSize, newSize);
				}
			});
		}
	}
	
	@Override
	public int getWidth() {
		return width;
	}

	public final void setWidth(int width) {
		this.width = width;
	}

	@Override
	public int getHeight() {
		return (int) Math.ceil(((double)palette.size()) / width);
	}

	@Override
	public ScrollRate getScrollRate() {
		return ScrollRate.IDENTITY;
	}

	@Override
	public boolean isSolid() {
		return false;
	}

	@Override
	public boolean isVisible() {
		return true;
	}

	@Override
	public int getTile(int x, int y) {
		final int tile = y * width + x;
		
		if(tile < 0 || tile >= palette.size()) {
			return -1;
		} else {
			return tile;
		}
	}

	@Override
	public int getTile(Point p) {
		return getTile(p.x, p.y);
	}

	@Override
	public void addSizeChangeListener(SizeChangeListener listener) {
		sizeChangeListeners.add(listener);
	}
	
	@Override
	public void removeSizeChangeListener(SizeChangeListener listener) {
		sizeChangeListeners.remove(listener);
	}
	
	protected void fireSizeChanged(Dimension oldSize, Dimension newSize) {
		
		for(final SizeChangeListener listener : sizeChangeListeners) {
			listener.sizeChanged(this, oldSize, newSize);
		}
	}
}
