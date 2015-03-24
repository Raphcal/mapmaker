package fr.rca.mapmaker.motion;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.beans.PropertyChangeEvent;
import java.beans.PropertyChangeListener;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Objects;
import javax.swing.JPanel;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class MotionPanel extends JPanel {
	
	private final Map<Motion, Color> motions = new LinkedHashMap<Motion, Color>();
	private int refreshRate = 200;

	public MotionPanel() {
		add(Motion.getDefaultMotion(), Color.RED);
	}
	
	public final void add(Motion motion, Color color) {
		motions.put(motion, color);
		
		motion.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
					repaint();
				}
			}
		});
	}

	public void setDirection(int direction) {
		final float realDirection = direction / 100.0f;
		for(final Motion motion : motions.keySet()) {
			motion.setDirection(realDirection);
		}
	}
	
	public void setRefreshRate(int refreshRate) {
		final int old = getRefreshRate();
		final float oldDelta = getDelta();
		
		this.refreshRate = refreshRate;
		
		firePropertyChange("refreshRate", old, refreshRate);
		firePropertyChange("delta", oldDelta, getDelta());
	}
	
	public int getRefreshRate() {
		return this.refreshRate;
	}

	public float getDelta() {
		return (float) refreshRate / 10000.0f;
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final int size = 32;
		
		// Cadrillage
		final Rectangle bounds = g.getClipBounds();
		final int left = bounds.x + size - bounds.x % size;
		final int top = bounds.y + size - bounds.y % size;
		
		g.setColor(Color.LIGHT_GRAY);
		
		for(int y = 0; y < bounds.height; y += size) {
			g.drawLine(bounds.x, y + top, bounds.x + bounds.width, y + top);
		}
		
		for(int x = 0; x < bounds.width; x += size) {
			g.drawLine(x + left, bounds.y, x + left, bounds.y + bounds.height);
		}
		
		// Affichage des mouvements
		for(final Map.Entry<Motion, Color> entry : motions.entrySet()) {
			final Motion motion = entry.getKey();
			motion.reset();
			motion.setY(getHeight() / 2.0f);
			drawMotion(entry.getKey(), entry.getValue(), g);
		}

		g.dispose();
	}

	private void drawMotion(Motion motion, Color color, Graphics g) {
		final float end = 4f;
		final float delta = getDelta();

		g.setColor(color);
		for(float elapsed = 0; elapsed < end; elapsed += delta) {
			if(!motion.isInAir() && elapsed >= end / 2.0f) {
				motion.jump();
			}
			motion.update(delta);

			g.fillOval((int) motion.getX() - 1, (int) motion.getY() - 1, 3, 3);
		}
	}
	
}
