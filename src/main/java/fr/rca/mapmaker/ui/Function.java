package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.operation.Operation;
import fr.rca.mapmaker.operation.OperationParser;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class Function extends JComponent {
	
	private static final Color COLOR = new Color(255, 0, 0, 128);
	
	private String function;
	private Operation operation;
	
	private int sourceWidth = 1;
	private int sourceHeight = 1;

	public Function() {
	}
	
	public void setFunction(String function) {
		this.function = function;
		this.operation = OperationParser.parse(function);
		repaint();
	}

	public String getFunction() {
		return function;
	}

	public int getSourceWidth() {
		return sourceWidth;
	}

	public void setSourceWidth(int sourceWidth) {
		this.sourceWidth = sourceWidth;
	}

	public int getSourceHeight() {
		return sourceHeight;
	}

	public void setSourceHeight(int sourceHeight) {
		this.sourceHeight = sourceHeight;
	}

	@Override
	protected void paintComponent(Graphics g) {
		if(function == null || function.isEmpty()) {
			return;
		}
		
		final Rectangle bounds = g.getClipBounds();
		final double w = (double) bounds.width / (double) sourceWidth;
		final double h = (double) bounds.height / (double) sourceHeight;
		
		g.setColor(COLOR);
		
		for(int x = 0; x < sourceWidth; x++) {
			final double y = operation.execute((double)x);
			
			g.fillRect((int) Math.round(x * w), (int) (h * y), (int) Math.round(w), (int) h);
		}
	}
	
	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		
		final Function function = new Function();
		function.setSourceWidth(32);
		function.setSourceHeight(32);
		function.setFunction("-x + 31");
		
		function.setPreferredSize(new Dimension(200, 200));
		
		frame.add(function);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
