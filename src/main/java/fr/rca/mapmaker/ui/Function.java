package fr.rca.mapmaker.ui;

import java.awt.Color;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Rectangle;
import java.util.ArrayDeque;
import java.util.Arrays;
import java.util.Deque;
import java.util.HashSet;
import java.util.Set;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class Function extends JComponent {
	
	private static final Set<Character> OPERATORS = new HashSet<Character>(Arrays.asList('+', '-', '/', '*'));
	private static final Color COLOR = new Color(255, 0, 0, 128);
	
	private String function;
	
	private int sourceWidth = 1;
	private int sourceHeight = 1;

	public Function() {
	}
	
	public void setFunction(String function) {
		this.function = function;
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

	private double apply(double x) {
		if(function == null) {
			return x;
		}
		
		final ArrayDeque<Double> stack = new ArrayDeque<Double>();
		char operator = '\0';
		
		Double value = null;
		boolean decimal = false;
		int count = 0;
		
		for(char c : function.toCharArray()) {
			if(c == '.') {
				decimal = true;
				count = 0;
				
			} else if(c >= '0' && c <= '9') {
				final int digit = c - '0';
				if(value == null) {
					value = 0.0;
				}
				if(!decimal) {
					value = value * 10 + digit;
				} else {
					value = value + digit / Math.pow(10.0, ++count);
				}
						
			} else {
				if(value != null) {
					stack.push(value);
					value = null;
					decimal = false;
					
					applyOperator(operator, stack);
					operator = '\0';
				}
				
				if(c == 'x') {
					stack.push((double)x);

					applyOperator(operator, stack);
					operator = '\0';

				} else if(OPERATORS.contains(c)) {
					operator = c;
				}
			}
		}
		
		if(value != null) {
			stack.push(value);
		}
		
		applyOperator(operator, stack);
		
		return stack.peek() != null ? stack.peek() : 0.0;
	}
	
	private void applyOperator(char operator, Deque<Double> stack) {
		if(operator == '\0') {
			return;
		}
		
		if(operator == '-' && stack.size() == 1) {
			stack.push(-stack.pop());
			return;
		}
		
		final double right = stack.pop();
		final double left = stack.pop();
		
		switch(operator) {
			case '+':
				stack.push(left + right);
				break;
				
			case '-':
				stack.push(left - right);
				break;
				
			case '*':
				stack.push(left * right);
				break;
				
			case '/':
				stack.push(left / right);
				break;
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		final Rectangle bounds = g.getClipBounds();
		final double w = (double) bounds.width / (double) sourceWidth;
		final double h = (double) bounds.height / (double) sourceHeight;
		
		g.setColor(COLOR);
		
		for(int x = 0; x < sourceWidth; x++) {
			final double y = apply((double)x);
			
			g.fillRect((int) Math.floor(x * w), (int) (h * (sourceHeight - y)), (int) Math.ceil(w), (int) (h * y));
		}
	}
	
	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		
		final Function function = new Function();
		function.setSourceWidth(32);
		function.setSourceHeight(32);
		function.setFunction("16");
		
		function.setPreferredSize(new Dimension(200, 200));
		
		frame.add(function);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
