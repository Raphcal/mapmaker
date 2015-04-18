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
	private static final Color[] COLORS = new Color[] {
		Color.RED, Color.YELLOW, Color.GREEN
	};
	
	private final Map<Motion, Color> motions = new LinkedHashMap<Motion, Color>();
	private int refreshRate = 2;
	private float direction = 1.0f;
	private float jumpSpeedPercent = 1.0f;

	public MotionPanel() {
		add(Motion.getDefaultMotion(), Color.RED);
	}
	
	public final void add(Motion motion, Color color) {
		motions.put(motion, color);
		motion.setDirection(this.direction);
		
		motion.addPropertyChangeListener(new PropertyChangeListener() {

			@Override
			public void propertyChange(PropertyChangeEvent evt) {
				if(!Objects.equals(evt.getOldValue(), evt.getNewValue())) {
					repaint();
				}
			}
		});
		
		repaint();
	}

	public void setDirection(int direction) {
		this.direction = direction / 100.0f;
		for(final Motion motion : motions.keySet()) {
			motion.setDirection(this.direction);
		}
	}

	public void setJumpSpeedPercent(int jumpSpeedPercent) {
		this.jumpSpeedPercent = (float) jumpSpeedPercent / 100.0f;
		repaint();
	}

	public int getJumpSpeedPercent() {
		return (int) (jumpSpeedPercent * 100.0f);
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
		return (float) refreshRate / 1000.0f;
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
		final int y = (int) (getHeight() / 2.0f + 32 - ((int) getHeight() / 2.0f) % 32);
		
		Motion last = null;
		for(final Map.Entry<Motion, Color> entry : motions.entrySet()) {
			final Motion motion = entry.getKey();
			motion.reset();
			motion.setY(y);
			drawMotion(entry.getKey(), entry.getValue(), g);
			
			last = motion;
		}
		
		drawMotionSpeed(last, g);
		
		g.dispose();
	}

	private void drawMotion(Motion motion, Color color, Graphics g) {
		final float end = 5f;
		final float delta = getDelta();

		g.setColor(color);
		for(float elapsed = 0; elapsed < end; elapsed += delta) {
			if(!motion.isInAir() && motion.isAtSpeedPercentage(jumpSpeedPercent) && isAtAMultipleOf32(motion)) {
				motion.jump();
			}
			motion.update(delta);

			g.fillOval((int) motion.getX() - 1, (int) motion.getY() - 1, 3, 3);
		}
	}

	private void drawMotionSpeed(Motion motion, Graphics g) {
		final float end = 5f;
		final float delta = getDelta();

		motion.reset();
		final float direction = motion.getDirection();
		motion.setDirection(1.0f, false);
		
		for(float elapsed = 0; elapsed < end; elapsed += delta) {
			if(motion.getDirection() == 1.0f && motion.isAtSpeedPercentage(jumpSpeedPercent) && isAtAMultipleOf32(motion)) {
				motion.setDirection(0.0f, false);
			}
			motion.update(delta);
			
			final double speedPercent = (motion.getHorizontalSpeed() * COLORS.length) / motion.getMaximumSpeed();
			final int color = (int) speedPercent;
			
			if(color >= COLORS.length - 1) {
				g.setColor(COLORS[COLORS.length - 1]);
			} else {
				g.setColor(mix(COLORS[color], COLORS[color + 1], speedPercent - color));
			}

			g.fillOval((int) motion.getX() - 1, (int) motion.getY() - 1, 3, 3);
		}
		
		motion.setDirection(direction, false);
	}
	
	private Color mix(Color firstColor, Color secondColor, double percent) {
		return new Color((int) (secondColor.getRed() * percent + firstColor.getRed() * (1.0 - percent)),
				(int) (secondColor.getGreen()* percent + firstColor.getGreen() * (1.0 - percent)),
				(int) (secondColor.getBlue()* percent + firstColor.getBlue() * (1.0 - percent)));
	}
	
	private boolean isAtAMultipleOf32(Motion motion) {
		return ((int)motion.getX()) % 32 == 0;
	}
	
}
