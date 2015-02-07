package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.palette.ColorPalette;
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
 */
public class AnimatedGrid<L extends Layer> extends JComponent {
	
	private int index;
	private List<L> frames = Collections.<L>emptyList();
	private Palette palette = ColorPalette.getDefaultColorPalette();
	
	private Timer timer;
	private int frequency = 24;

	public AnimatedGrid() {
		setSize(32, 32);
		setPreferredSize(new Dimension(32, 32));
	}
	
	public void start() {
		if(timer == null) {
			timer = new Timer((int) (1000.0 / (double)frequency), new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					nextFrame();
				}
			});
			timer.start();
		}
	}
	
	public void stop() {
		if(timer != null) {
			timer.stop();
			timer = null;
		}
	}

	public void setFrequency(int frequency) {
		this.frequency = frequency;
		
		timer.setDelay((int) (1000.0 / (double)frequency));
	}

	public int getFrequency() {
		return frequency;
	}
	
	public void setFrames(List<L> frames) {
		this.frames = frames;
		
		if(frames != null && !frames.isEmpty()) {
			final Layer layer = frames.get(0);
			setSize(new Dimension(layer.getWidth(), layer.getHeight()));
		}
		
		repaint();
	}

	public void setPalette(Palette palette) {
		this.palette = palette;
	}
	
	public void nextFrame() {
		if(!frames.isEmpty()) {
			index = (index + 1) % frames.size();
			repaint();
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
		if(index >= 0 && index < frames.size()) {
			final Layer layer = frames.get(index);

			for(int y = 0; y < layer.getHeight(); y++) {
				for(int x = 0; x < layer.getWidth(); x++) {
					palette.paintTile(g, layer.getTile(x, y), x, y, 1);
				}
			}
		}
		
		g.dispose();
	}
	
}
