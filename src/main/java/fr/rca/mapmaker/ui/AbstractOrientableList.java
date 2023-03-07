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
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.imageio.ImageIO;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public abstract class AbstractOrientableList<E> extends JComponent implements Orientable {

	public static final String ADD_COMMAND = "add";
	public static final String EDIT_COMMAND = "edit";

	private static final int MAXIMUM_SIZE = 200;

	protected Orientation orientation = Orientation.VERTICAL;
	private int width = 72;
	private int height = 72;
	private int padding = 4;
	protected List<E> elements;

	protected LayerChangeListener listener;
	protected final Map<TileLayer, Integer> indexes = new HashMap<TileLayer, Integer>();

	private BufferedImage addImage;

	private boolean editable = true;

	private final List<Integer> selection = new ArrayList<Integer>();

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

				if (index != null) {
					repaintElement(index);
				}
			}
		};

		setElements(elements);

		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				switch (e.getClickCount()) {
					case 1:
						final int element = orientation.indexOfElementAtPoint(AbstractOrientableList.this, e.getPoint());

						if (!selection.isEmpty() && ((e.getModifiersEx() & MouseEvent.SHIFT_DOWN_MASK) != 0)) {
							expandSelection(element);

						} else if ((e.getModifiersEx() & (MouseEvent.CTRL_DOWN_MASK | MouseEvent.META_DOWN_MASK)) != 0) {
							addToSelection(element);

						} else {
							select(element);
						}
						repaint();
						break;

					case 2:
						if (selection.size() == 1) {
							final int selected = selection.get(0);
							fireActionPerformed(selected == AbstractOrientableList.this.elements.size() ? ADD_COMMAND : EDIT_COMMAND);
						}
						break;
				}
			}
		});

		addFocusListener(new FocusAdapter() {

			@Override
			public void focusLost(FocusEvent e) {
				if (!selection.isEmpty()) {
					repaint();
				}
			}
		});
	}

	private void select(final int element) {
		if (element >= 0 && element <= AbstractOrientableList.this.elements.size()) {
			selection.clear();
			selection.add(element);

			requestFocusInWindow();
		} else {
			selection.clear();
		}
	}

	private void addToSelection(final int element) {
		if (element >= 0 && element < AbstractOrientableList.this.elements.size()) {
			selection.add(element);
		}
	}

	private void expandSelection(final int element) {
		if (element >= 0 && element < AbstractOrientableList.this.elements.size()) {
			Collections.sort(selection, new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					return o1.compareTo(o2);
				}
			});
			final int start;
			final int end;

			if (element > selection.get(0)) {
				start = selection.get(0);
				end = element;
			} else if (element < selection.get(0)) {
				end = selection.get(0);
				start = element;
			} else {
				start = element;
				end = element;
			}

			selection.clear();
			for (int i = start; i <= end; i++) {
				selection.add(i);
			}
		}
	}

	public final void setElements(List<E> elements) {
		if (elements == null) {
			elements = new ArrayList<E>();
		}

		if (this.elements != null) {
			for (final TileLayer layer : indexes.keySet()) {
				layer.removeLayerChangeListener(listener);
			}
			indexes.clear();
		}

		this.elements = elements;

		for (int index = 0; index < elements.size(); index++) {
			final E element = elements.get(index);

			if (element != null) {
				elementAdded(index, element);
			}
		}

		updateSize();

		if (getParent() != null) {
			getParent().validate();
		}

		repaint();
	}

	public List<E> getElements() {
		return elements;
	}

	public int getElementCount() {
		return elements.size();
	}

	protected abstract void elementAdded(int index, E element);

	@Override
	public int getPadding() {
		return padding;
	}

	@Override
	public int getElementWidth() {
		if (width <= MAXIMUM_SIZE && height <= MAXIMUM_SIZE) {
			return width;
		}
		
		final double ratio = (double)width / height;
		if (ratio > 1.0) {
			return MAXIMUM_SIZE;
		} else {
			return (int) Math.round(MAXIMUM_SIZE * ratio);
		}
	}

	public void setElementWidth(int width) {
		this.width = width;
		updateSize();
	}

	@Override
	public int getElementHeight() {
		if (width <= MAXIMUM_SIZE && height <= MAXIMUM_SIZE) {
			return height;
		}
		
		final double ratio = (double)height / width;
		if (ratio > 1.0) {
			return MAXIMUM_SIZE;
		} else {
			return (int) Math.round(MAXIMUM_SIZE * ratio);
		}
	}

	// Appelé quand la taille d'un sprite change.
	public void setElementHeight(int height) {
		this.height = height;
		updateSize();
	}

	private void updateSize() {
		final Dimension newSize = orientation.getDimension(this);
		setPreferredSize(newSize);
		setSize(newSize);

		invalidate();

		final Component parent = getParent();
		if (parent != null) {
			parent.validate();
		}
	}

	@Override
	public int getNumberOfElements() {
		int count = elements.size();
		if (editable) {
			count++;
		}
		return count;
	}

	public void add(E element) {
		this.elements.add(element);
		repaint();
		updateSize();
	}
	public void addAll(Collection<E> elements) {
		this.elements.addAll(elements);
		repaint();
		updateSize();
	}

	public void insert(int index, E element) {
		this.elements.add(index, element);
		repaint();
		updateSize();
	}
	public void insertAll(int index, Collection<E> elements) {
		this.elements.addAll(index, elements);
		repaint();
		updateSize();
	}

	public void removeSelectedElement() {
		if (!selection.isEmpty()) {
			Collections.sort(selection, new Comparator<Integer>() {

				@Override
				public int compare(Integer o1, Integer o2) {
					return o2.compareTo(o1);
				}
			});

			for (final int selected : selection) {
				this.elements.remove(selected);
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

	public int[] getSelectedIndexes() {
		final int[] selectedIndexes = new int[selection.size()];

		for (int index = 0; index < selection.size(); index++) {
			selectedIndexes[index] = selection.get(index);
		}

		return selectedIndexes;
	}

	public List<E> getSelection() {
		final List<E> selectedElements = new ArrayList<E>();

		Collections.sort(selection, new Comparator<Integer>() {

			@Override
			public int compare(Integer o1, Integer o2) {
				return o1.compareTo(o2);
			}
		});

		for (int index : selection) {
			selectedElements.add(elements.get(index));
		}

		return selectedElements;
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle clipBounds = g.getClipBounds();

		if (isOpaque()) {
			g.setColor(getBackground());
			g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		}

		// Maps
		final int firstMap = Math.max((orientation.getStart(clipBounds) - padding) / (orientation.getSize(this) + padding), 0);
		final int lastMap = Math.min(
				firstMap + orientation.getLength(clipBounds) / (orientation.getSize(this) + padding),
				elements.size() - 1);

		final int width = getElementWidth();
		final int height = getElementHeight();

		for (int index = firstMap; index <= lastMap; index++) {
			// Selection
			if (selection.contains(index)) {
				g.setColor(SystemColor.textHighlight);
				if (hasFocus()) {
					g.fillRect(orientation.getX(this, index), orientation.getY(this, index), width, height);
				} else {
					g.drawRect(orientation.getX(this, index), orientation.getY(this, index), width, height);
				}
			}

			paintElement(index, g);
		}

		// Plus
		if (editable) {
			g.drawImage(addImage,
					orientation.getX(this, elements.size()) + width / 2 - addImage.getWidth() / 2,
					orientation.getY(this, elements.size()) + height / 2 - addImage.getHeight() / 2, null);
		}

		g.dispose();
	}

	protected abstract void paintElement(int index, Graphics g);

	private void repaintElement(int index) {
		final int width = getElementWidth();
		final int height = getElementHeight();
		repaint(new Rectangle(
				// X
				orientation.getX(this, index) - padding,
				// Y
				orientation.getY(this, index) - padding,
				// Width, Height
				width + padding + padding, height + padding + padding));
	}

	public void addActionListener(ActionListener listener) {
		this.actionListeners.add(listener);
	}

	public void removeActionListener(ActionListener listener) {
		this.actionListeners.remove(listener);
	}

	protected void fireActionPerformed(String command) {
		for (final ActionListener listener : actionListeners) {
			listener.actionPerformed(new ActionEvent(this, selection.get(0), command));
		}
	}
}
