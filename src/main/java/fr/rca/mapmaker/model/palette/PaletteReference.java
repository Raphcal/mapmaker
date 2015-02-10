package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.SizeChangeListener;
import fr.rca.mapmaker.model.project.Project;
import java.awt.Graphics;
import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class PaletteReference implements Palette, EditablePalette, HasSizeChangeListeners {
	
	private Project project;
	private int paletteIndex;

	public PaletteReference() {
	}

	public PaletteReference(Project project, int paletteIndex) {
		this.project = project;
		this.paletteIndex = paletteIndex;
	}

	public void setProject(Project project) {
		this.project = project;
	}
	
	private Palette getPalette() {
		return project.getPalette(paletteIndex);
	}
	
	private EditablePalette getEditablePalette() {
		if(getPalette() instanceof EditablePalette)
			return (EditablePalette) getPalette();
		else
			throw new UnsupportedOperationException(getPalette().getClass().getSimpleName() + " n'est pas éditable.");
	}
	
	private HasSizeChangeListeners getHasSizeChangeListeners() {
		
		if(getPalette() instanceof HasSizeChangeListeners)
			return (HasSizeChangeListeners) getPalette();
		else
			return null;
	}

	@Override
	public boolean equals(Object obj) {
		
		if(obj instanceof PaletteReference) {
			return paletteIndex == ((PaletteReference)obj).paletteIndex;
			
		} else if(obj instanceof Palette) {
			return getPalette().equals(obj);
			
		} else
			return false;
	}

	@Override
	public int hashCode() {
		return getPalette().hashCode();
	}
	
	public int getPaletteIndex() {
		return paletteIndex;
	}
	
	@Override
	public void paintTile(Graphics g, int tile, int x, int y, int size) {
		getPalette().paintTile(g, tile, x, y, size);
	}

	@Override
	public int getTileSize() {
		return getPalette().getTileSize();
	}

	@Override
	public int getTileSize(int tile) {
		return getPalette().getTileSize(tile);
	}

	@Override
	public int size() {
		return getPalette().size();
	}

	@Override
	public void setSelectedTile(int tile) {
		getPalette().setSelectedTile(tile);
	}

	@Override
	public int getSelectedTile() {
		return getPalette().getSelectedTile();
	}

	@Override
	public boolean isEditable() {
		return getPalette().isEditable();
	}

	@Override
	public void setName(String name) {
		getEditablePalette().setName(name);
	}

	@Override
	public void editTile(int index, JFrame parent) {
		getEditablePalette().editTile(index, parent);
	}

	@Override
	public void refresh() {
		getPalette().refresh();
	}
	
	@Override
	public void addSizeChangeListener(SizeChangeListener listener) {
		
		final HasSizeChangeListeners hasSizeChangeListeners = getHasSizeChangeListeners();
		
		if(hasSizeChangeListeners != null)
			hasSizeChangeListeners.addSizeChangeListener(listener);
	}

	@Override
	public void removeSizeChangeListener(SizeChangeListener listener) {
		
		final HasSizeChangeListeners hasSizeChangeListeners = getHasSizeChangeListeners();
		
		if(hasSizeChangeListeners != null)
			hasSizeChangeListeners.removeSizeChangeListener(listener);
	}
}
