package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.Collections;
import java.util.List;
import javax.swing.JComponent;
import javax.swing.Timer;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 * @param <L> Type des étapes d'animation.
 */
public class AnimatedGrid<L extends DataLayer> extends JComponent {

	private static final double ONE_SECOND = 1000.0;
	private static final double MAX_SIZE = 400.0;

	private int index;
	private List<L> frames = Collections.<L>emptyList();
	private Palette palette = AlphaColorPalette.getDefaultColorPalette();

	private Timer timer;
	private int frequency = 24;
	private double time;
	private boolean looping;
	private boolean scroll;
	private boolean easing;

	private int frameWidth = 32;
	private int frameHeight = 32;

	/**
	 * Niveau de zoom des carreaux.
	 */
	private double zoom = 1.0;

	public AnimatedGrid() {
		updateSize();
	}

	public void start() {
		if (timer != null) {
			return;
		}
		timer = new Timer(getDelay(), new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				nextFrame();
			}
		});
		timer.start();
	}

	public void restart() {
		stop();
		index = 0;
		time = 0.0;
		repaint();
		start();
	}

	public void stop() {
		if (timer != null) {
			timer.stop();
			timer = null;
		}
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;

		if (timer != null) {
			timer.setDelay(getDelay());
		}
	}

	public int getFrequency() {
		return frequency;
	}

	public boolean isLooping() {
		return looping;
	}

	public void setLooping(boolean looping) {
		this.looping = looping;
	}

	public boolean isEasing() {
		return easing;
	}

	public void setEasing(boolean easing) {
		final boolean oldEasing = this.easing;
		this.easing = easing;

		if (oldEasing != easing) {
			setFrequency(frequency);
		}
	}

	private int getDelay() {
		if (!easing) {
			return (int) (ONE_SECOND / (double) frequency);
		} else {
			return (int) (ONE_SECOND / 60.0);
		}
	}

	private double getDuration() {
		return frames.size() * (ONE_SECOND / (double) frequency);
	}

	public void setFrames(List<L> frames) {
		this.frames = frames;
		repaint();
	}

	public void setPalette(Palette palette) {
		this.palette = palette;
	}

	public double getZoom() {
		return zoom;
	}

	public void setZoom(double zoom) {
		this.zoom = zoom;
		updateSize();
	}

	public void setZoomAsInteger(int zoom) {
		setZoom((double) zoom / 100.0);
	}

	public int getZoomAsInteger() {
		return (int) (zoom * 100.0);
	}

	public int getFrameWidth() {
		return frameWidth;
	}

	public void setFrameWidth(int frameWidth) {
		this.frameWidth = frameWidth;
		updateSize();
	}

	public int getFrameHeight() {
		return frameHeight;
	}

	public void setFrameHeight(int frameHeight) {
		this.frameHeight = frameHeight;
		updateSize();
	}

	public void setScroll(boolean scroll) {
		this.scroll = scroll;
	}

	public boolean isScroll() {
		return scroll;
	}

	private void updateSize() {
		if (frameWidth * zoom > MAX_SIZE || frameHeight * zoom > MAX_SIZE) {
			final int oldZoom = getZoomAsInteger();
			zoom = ((int) (((double) MAX_SIZE / Math.max(frameWidth, frameHeight)) * 100)) / 100.0;
			firePropertyChange("zoomAsInteger", oldZoom, getZoomAsInteger());
		}

		final Dimension dimension = new Dimension((int) (frameWidth * zoom), (int) (frameHeight * zoom));
		setPreferredSize(dimension);
		setSize(dimension);
	}

	public void nextFrame() {
		if (frames.isEmpty()) {
			return;
		}
		if (easing) {
			final int oldIndex = index;
			final double duration = getDuration();
			time += getDelay();
			if (looping && time >= duration) {
				time -= duration;
			}

			index = (int) (easeInOut(0, duration, time) * frames.size());
			if (!looping) {
				index = Math.min(index, frames.size() - 1);
			}

			if (oldIndex != index) {
				repaint();
			}
			return;
		}
		if (looping) {
			index = (index + 1) % frameCount();
			repaint();
		} else {
			index = Math.min(index + 1, frameCount() - 1);
			repaint();
		}
	}

	private double easeInOut(double from, double to, double value) {
		final double progress = Math.max(Math.min((value - from) / (to - from), 1.0), 0.0);
		final double sin = Math.sin(Math.PI / 2.0 * progress);
		return sin * sin;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Rectangle clipBounds = g.getClipBounds();

		// Fond
		((Graphics2D) g).setPaint(Paints.TRANSPARENT_PAINT);
		g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);

		// Étape d'animation
		if (index >= 0 && index < frameCount()) {
			if (!scroll) {
				// TODO: Calculer le zoom à partir de la taille de la frame
				DataLayer layer = frames.get(index);
				Dimension dimension = getPreferredSize();
				for (int y = 0; y < dimension.height; y++) {
					for (int x = 0; x < dimension.width; x++) {
						palette.paintTile(g, layer.getTile((int) (x / zoom), (int) (y / zoom)), x, y, 1);
					}
				}
			} else {
				final Layer layer = frames.get(0);

				for (int y = 0; y < frameHeight; y++) {
					final int frameY = frames.get(0).getHeight() - frameHeight - index + y;
					for (int x = 0; x < layer.getWidth(); x++) {
						palette.paintTile(g, layer.getTile(x, frameY), (int) (x * zoom), (int) (y * zoom), (int) Math.max(zoom, 1));
					}
				}
			}
		}

		g.dispose();
	}

	private int frameCount() {
		if (scroll && !frames.isEmpty()) {
			// TODO: Gérer le scrolling horizontal aussi.
			return Math.max(frames.get(0).getHeight() - frameHeight, 1);
		} else {
			return frames.size();
		}
	}

}
