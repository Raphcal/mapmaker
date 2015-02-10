package fr.rca.mapmaker;

import fr.rca.mapmaker.ui.LayerLayout;
import java.awt.Color;
import java.awt.Dimension;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import javax.swing.JFrame;
import javax.swing.JPanel;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class LayoutPreview {
	public static void main(String[] args) {
		final JFrame frame = new JFrame();

		final JPanel red = new JPanel();
		red.setPreferredSize(new Dimension(50, 50));
//		red.setOpaque(false);
		red.setBackground(new Color(1.0f, 0.0f, 0.0f, 0.5f));

		final JPanel blue = new JPanel();
		blue.setPreferredSize(new Dimension(50, 50));
//		green.setOpaque(false);
		blue.setBackground(new Color(0.0f, 0.0f, 1.0f, 0.5f));
		
		blue.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("blue");
			}
		});
		
		red.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				System.out.println("red");
			}
		});
		
		final JPanel gridBag = new JPanel(new LayerLayout(LayerLayout.Disposition.TOP_LEFT));
		gridBag.setOpaque(true);
		gridBag.setPreferredSize(new Dimension(150, 150));
		gridBag.add(red);
		gridBag.add(blue);
		
		frame.setContentPane(gridBag);
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
