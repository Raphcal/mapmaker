package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.model.LayerChangeListener;
import fr.rca.mapmaker.model.map.TileLayer;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.FocusAdapter;
import java.awt.event.FocusEvent;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class AbstractOrientableList<E> extends JComponent implements Orientable {
	public static final String ADD_COMMAND = "add";
	public static final String EDIT_COMMAND = "edit";
	
	protected Orientation orientation = Orientation.VERTICAL;
	private int gridSize = 72;
	private int padding = 4;
	protected List<E> elements;
	
	protected LayerChangeListener listener;
	protected final HashMap<TileLayer, Integer> indexes = new HashMap<TileLayer, Integer>();
	
	private BufferedImage addImage;
	
	private boolean editable = true;
	
	private Integer selection;
	
	private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();

	public AbstractOrientableList() {
		this(null);
	}

	public AbstractOrientableList(List<E> elements) {
		setOpaque(true);
		setFocusable(true);
		setEnabled(editable);
		
		try {
			addImage = ImageIO.read(AbstractOrientableList.class.getResourceAsStream("/resources/add.png"));
		} catch (IOException ex) {
			Exceptions.showStackTrace(ex, null);
		}
		
		listener = new LayerChangeListener() {
			@Override
			public void layerChanged(TileLayer layer, Rectangle dirtyRectangle) {
				final Integer index = indexes.get(layer);
				
				if(index != null) {
					repaintElement(index);
				}
			}
		};
		
		setElements(elements);
		
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				switch(e.getClickCount()) {
					case 1:
						int element = orientation.indexOfElementAtPoint(AbstractOrientableList.this, e.getPoint());
						if(element >= 0 && element <= AbstractOrientableList.this.elements.size()) {
							selection = element;
							requestFocusInWindow();
						} else {
							selection = null;
						}
						repaint();
						break;
						
					case 2:
						if(selection != null) {
							fireActionPerformed(selection == AbstractOrientableList.this.elements.size() ? ADD_COMMAND : EDIT_COMMAND);
						}
						break;
				}
			}
		});
		
		addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				if(selection != null) {
					repaintElement(selection);
				}
			}
		});
	}

	public final void setElements(List<E> elements) {
		if(elements == null) {
			elements = new ArrayList<E>();
		}
		
		if(this.elements != null) {
			for(final TileLayer layer : indexes.keySet()) {
				layer.removeLayerChangeListener(listener);
			}
			indexes.clear();
		}
		
		this.elements = elements;
		
		for(int index = 0; index < elements.size(); index++) {
			final E element = elements.get(index);
			
			if(element != null) {
				elementAdded(index, element);
			}
		}

		updateSize();
		
		if(getParent() != null) {
			getParent().validate();
		}
		
		repaint();
	}
	
	public List<E> getElements() {
		return elements;
	}
	
	protected abstract void elementAdded(int index, E element);
	
	@Override
	public int getPadding() {
		return padding;
	}

	@Override
	public int getGridSize() {
		return gridSize;
	}
	
	public void setGridSize(int gridSize) {
		this.gridSize = gridSize;
		updateSize();
	}

	private void updateSize() {
		final Dimension newSize = orientation.getDimension(this);
		setPreferredSize(newSize);
		setSize(newSize);
		
		invalidate();
		
		final Component parent = getParent();
		if(parent != null) {
			parent.validate();
		}
	}
	
	@Override
	public int getNumberOfElements() {
		int count = elements.size();
		if(editable) {
			count++;
		}
		return count;
	}

	public void add(E element) {
		this.elements.add(element);
		repaint();
		updateSize();
	}
	
	public void removeSelectedElement() {
		if(selection != null) {
			this.elements.remove(selection.intValue());
			if(selection >= this.elements.size()) {
				selection--;
				
				if(selection < 0) {
					selection = null;
				}
			}
			repaint();
			updateSize();
		}
	}
	
	public void updateElement(int index) {
		repaintElement(index);
		updateSize();
	}
	
	public void setOrientation(Orientation orientation) {
		this.orientation = orientation != null ? orientation : Orientation.VERTICAL;
		updateSize();
	}

	public void setEditable(boolean editable) {
		this.editable = editable;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle clipBounds = g.getClipBounds();
		
		if(isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		}
		
		// Selection
		if(selection != null) {
			g.setColor(SystemColor.textHighlight);
			if(hasFocus()) {
				g.fillRect(orientation.getX(this, selection), orientation.getY(this, selection), gridSize, gridSize);
			} else {
				g.drawRect(orientation.getX(this, selection), orientation.getY(this, selection), gridSize, gridSize);
			}
		}
		
		// Maps
		final int firstMap = Math.max((orientation.getStart(clipBounds) - padding) / (orientation.getSize(this) + padding), 0);
		final int lastMap = Math.min(
			firstMap + orientation.getLength(clipBounds) / (orientation.getSize(this) + padding),
			elements.size() - 1);
		
		for(int index = firstMap; index <= lastMap; index++) {
			paintElement(index, g);
		}

		// Plus
		if(editable) {
			g.drawImage(addImage, 
				orientation.getX(this, elements.size()) + gridSize / 2 - addImage.getWidth() / 2, 
				orientation.getY(this, elements.size()) + gridSize / 2 - addImage.getHeight()/ 2, null);
		}
		
		g.dispose();
	}
	
	protected abstract void paintElement(int index, Graphics g);
	
	private void repaintElement(int index) {
		repaint(new Rectangle(
			// X
			orientation.getX(this, index) - padding, 
			// Y
			orientation.getY(this, index) - padding,
			// Width, Height
			gridSize + padding + padding, gridSize + padding + padding));
	}
	
	public void addActionListener(ActionListener listener) {
		this.actionListeners.add(listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		this.actionListeners.remove(listener);
	}
	
	protected void fireActionPerformed(String command) {
		for(final ActionListener listener : actionListeners) {
			listener.actionPerformed(new ActionEvent(this, selection, command));
		}
	}
}
