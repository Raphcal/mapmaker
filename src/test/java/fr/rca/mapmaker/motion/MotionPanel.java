package fr.rca.mapmaker.motion;

import java.awt.Color;
import java.awt.Graphics;
import java.util.LinkedHashMap;
import java.util.Map;
import javax.swing.JPanel;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class MotionPanel extends JPanel {
	
	private final Map<Motion, Color> motions = new LinkedHashMap<Motion, Color>();

	public void add(Motion motion, Color color) {
		motions.put(motion, color);
	}

	public void setDirection(float direction) {
		for(final Motion motion : motions.keySet()) {
			motion.setDirection(direction);
		}
	}
	
	@Override
	protected void paintComponent(Graphics g) {
		for(final Map.Entry<Motion, Color> entry : motions.entrySet()) {
			final Motion motion = entry.getKey();
			motion.reset();
			motion.setY(getPreferredSize().height / 2.0f);
			drawMotion(entry.getKey(), entry.getValue(), g);
		}

		g.dispose();
	}

	private void drawMotion(Motion motion, Color color, Graphics g) {
		final float end = 4f;
		final float delta = 0.02f;

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
