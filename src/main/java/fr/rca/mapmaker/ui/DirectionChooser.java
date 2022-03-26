package fr.rca.mapmaker.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.SystemColor;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class DirectionChooser extends JComponent {
	private static final int PADDING = 4;
	private static final int SQUARE_SIZE = 5;
	private static final int CENTER_SIZE = 3;
	
	private int numberOfDirections = 8;
	private double direction = 0.0;
	private boolean simplified = true;
	
	private final HashSet<Double> anglesWithValue = new HashSet<Double>();
	private final ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();

	public DirectionChooser() {
		addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				setDirection(angleAtPoint(e.getPoint()));
				repaint();
				
				fireActionPerformed();
			}
		});
	}
	
	public int getNumberOfDirections() {
		return numberOfDirections;
	}

	public void setNumberOfDirections(int numberOfDirections) {
		final int oldNumberOfDirections = this.numberOfDirections;
		this.numberOfDirections = numberOfDirections;
		
		firePropertyChange("numberOfDirections", oldNumberOfDirections, numberOfDirections);
	}

	public double getDirection() {
		return direction;
	}

	public void setDirection(double direction) {
		final double oldDirection = this.direction;
		
		if(simplified) {
			direction = simplifiedAngle(direction);
		}
		
		this.direction = direction;
		
		firePropertyChange("direction", oldDirection, direction);
	}
	
	private double simplifiedAngle(double angle) {
		return ((int)(angle * 100.0)) / 100.0;
	}
	
	public boolean isSimplified() {
		return simplified;
	}

	public void setSimplified(boolean simplified) {
		this.simplified = simplified;
	}
	
	public void setHasValue(double angle, boolean value) {
		if(value) {
			anglesWithValue.add(angle);
		} else {
			anglesWithValue.remove(angle);
		}
	}
	
	public void setAnglesWithValue(Collection<Double> angles) {
		anglesWithValue.clear();
		anglesWithValue.addAll(angles);
		repaint();
	}
	
	public void clearValues() {
		anglesWithValue.clear();
	}

	public double angleAtPoint(Point point) {
		final Point2D center = getCenter();
		double clickAngle = Math.atan2(point.getY() - center.getY(), point.getX() - center.getX());
		
		if(clickAngle < 0) {
			clickAngle += 2 * Math.PI;
		}
		
		double minimumAngle = 0.0;
		double difference = Double.MAX_VALUE;
		
		final double step = getStep();
		for(double angle = 0.0; angle < 2.0 * Math.PI; angle += step) {
			final double currentDifference = Math.abs(angle - clickAngle);
			if(currentDifference < difference) {
				minimumAngle = angle;
				difference = currentDifference;
			}
		}
		
		return minimumAngle;
	}
	
	private Point2D getCenter() {
		final Dimension size = getSize();
		return new Point.Double(size.getWidth() / 2.0, size.getHeight() / 2.0);
	}
	
	private double getRay() {
		final Dimension size = getSize();
		return size.getHeight() / 2.0 - PADDING;
	}
	
	private double getStep() {
		return 2.0 * Math.PI / (double)numberOfDirections;
	}

	@Override
	protected void paintComponent(Graphics g) {
		super.paintComponent(g);
		
		final double ray = getRay();
		final Point2D center = getCenter();
		
		final Point2D selection = new Point.Double(center.getX() + Math.cos(direction) * ray, center.getY() + Math.sin(direction) * ray);
		
		g.setColor(SystemColor.textHighlight);
		g.drawLine((int)center.getX(), (int)center.getY(), (int)selection.getX(), (int)selection.getY());
		
		g.setColor(Color.BLACK);
		g.fillRect((int)(center.getX() - CENTER_SIZE/2), (int)(center.getY() - CENTER_SIZE/2), CENTER_SIZE, CENTER_SIZE);
		
		final double step = getStep();
		for(double angle = 0.0; angle < 2.0 * Math.PI; angle += step) {
			final double x = center.getX() + Math.cos(angle) * ray;
			final double y = center.getY() + Math.sin(angle) * ray;
				
			if(anglesWithValue.contains(angle) || anglesWithValue.contains(simplifiedAngle(angle))) {
				g.setColor(SystemColor.textHighlight);
				g.fillRect((int)(x - SQUARE_SIZE/2), (int)(y - SQUARE_SIZE/2), SQUARE_SIZE, SQUARE_SIZE);
				
			} else {
				g.setColor(Color.BLACK);
				g.drawRect((int)x, (int)y, 1, 1);
				g.drawRect((int)(x - SQUARE_SIZE/2), (int)(y - SQUARE_SIZE/2), SQUARE_SIZE, SQUARE_SIZE);
			}
		}
		
		g.dispose();
	}
	
	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}
	
	protected void fireActionPerformed() {
		for(final ActionListener listener : actionListeners) {
			listener.actionPerformed(new ActionEvent(this, 0, "DIRECTION_CHANGED"));
		}
	}
}
