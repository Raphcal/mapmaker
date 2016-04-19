package fr.rca.mapmaker.ui;

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
public class AnimatedGrid<L extends Layer> extends JComponent {
	
	private static final double ONE_SECOND = 1000.0;
    private static final double MAX_SIZE = 256.0;
	
	private int index;
	private List<L> frames = Collections.<L>emptyList();
	private Palette palette = AlphaColorPalette.getDefaultColorPalette();
	
	private Timer timer;
	private int frequency = 24;
	private boolean looping;
	private boolean scroll;
	
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
		if(timer == null) {
			timer = new Timer(getDelay(), new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					nextFrame();
				}
			});
			timer.start();
		}
	}
    
    public void restart() {
        stop();
        index = 0;
        repaint();
        start();
    }
	
	public void stop() {
		if(timer != null) {
			timer.stop();
			timer = null;
		}
	}
	
	public void setFrequency(int frequency) {
		this.frequency = frequency;
		
		if(timer != null) {
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
	
	private int getDelay() {
		return (int) (ONE_SECOND / (double)frequency);
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
		setZoom((double)zoom / 100.0);
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
        while (zoom > 1.0 && frameWidth * zoom > MAX_SIZE || frameHeight * zoom > MAX_SIZE) {
            zoom = Math.max(zoom - 1, 1);
        }
        
		final Dimension dimension = new Dimension((int) (frameWidth * zoom), (int) (frameHeight * zoom));
		setPreferredSize(dimension);
		setSize(dimension);
	}
	
	public void nextFrame() {
		if (!frames.isEmpty()) {
            if (looping) {
                index = (index + 1) % frameCount();
                repaint();
            } else {
                index = Math.min(index + 1, frameCount() - 1);
                repaint();
            }
		}
	}
    
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		final Rectangle clipBounds = g.getClipBounds();

		// Fond
		((Graphics2D)g).setPaint(Paints.TRANSPARENT_PAINT);
		g.fillRect(clipBounds.x, clipBounds.y, clipBounds.width, clipBounds.height);
		
		// Étape d'animation
		if(index >= 0 && index < frameCount()) {
			if (!scroll) {
				final Layer layer = frames.get(index);

				for(int y = 0; y < layer.getHeight(); y++) {
					for(int x = 0; x < layer.getWidth(); x++) {
						palette.paintTile(g, layer.getTile(x, y), (int) (x * zoom), (int) (y * zoom), (int) zoom);
					}
				}
			} else {
				final Layer layer = frames.get(0);
				
				for(int y = 0; y < frameHeight; y++) {
					final int frameY = frames.get(0).getHeight() - frameHeight - index + y;
					for(int x = 0; x < layer.getWidth(); x++) {
						palette.paintTile(g, layer.getTile(x, frameY), (int) (x * zoom), (int) (y * zoom), (int) zoom);
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
