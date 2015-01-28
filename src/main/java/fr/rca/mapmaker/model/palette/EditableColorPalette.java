package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.SizeChangeListener;
import java.awt.Color;
import java.awt.Dimension;
import java.util.ArrayList;
import java.util.ResourceBundle;
import javax.swing.JColorChooser;
import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class EditableColorPalette extends ColorPalette implements EditablePalette, HasSizeChangeListeners {

	private static final ResourceBundle language = ResourceBundle.getBundle("resources/language");
	private String name;
	
	private ArrayList<SizeChangeListener> sizeChangeListeners = new ArrayList<SizeChangeListener>();

	private final static int MAX_LENGTH = 256;
	private final static int COLUMN_LENGTH = 4;
	
	private int length;
	private int tileSize;
	
	public EditableColorPalette() {
		this(16, COLUMN_LENGTH);
	}
	
	public EditableColorPalette(int length) {
		this(16, length);
	}
	
	public EditableColorPalette(int tileSize, int length) {
		super(MAX_LENGTH);
		this.tileSize = tileSize;
		this.length = length;
	}
	
	@Override
	public void setName(String name) {
		this.name = name;
	}

	public void setTileSize(int tileSize) {
		this.tileSize = tileSize;
	}
	
	@Override
	public boolean isEditable() {
		return true;
	}

	@Override
	public void editTile(int index, JFrame parent) {
		final Color newColor = JColorChooser.showDialog(parent, language.getString("palette.color.edit"), getColor(index));
	
		if(newColor != null) {
			setColor(index, newColor);
			
			if(index >= length - COLUMN_LENGTH) {
				final Dimension oldSize = new Dimension(COLUMN_LENGTH, length / COLUMN_LENGTH);

				length += COLUMN_LENGTH;
				final Dimension newSize = new Dimension(COLUMN_LENGTH, length / COLUMN_LENGTH);

				fireSizeChanged(oldSize, newSize);
			}
		}
	}

	@Override
	public String toString() {
		if(name != null)
			return name;
		else
			return super.toString();
	}

	@Override
	public int getTileSize() {
		return tileSize;
	}

	@Override
	public void setColor(int index, Color color) {
		
		if(index >= length && index < MAX_LENGTH)
			length = index + 1;
		
		super.setColor(index, color);
	}
	
	@Override
	public int size() {
		return length;
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
		
		for(final SizeChangeListener listener : sizeChangeListeners)
			listener.sizeChanged(this, oldSize, newSize);
	}
}
