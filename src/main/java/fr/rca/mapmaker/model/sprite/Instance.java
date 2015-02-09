package fr.rca.mapmaker.model.sprite;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.Dimension;
import java.awt.Graphics;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import javax.swing.JComponent;
import javax.swing.JFrame;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class Instance extends JComponent {
	private static final int TILE_SIZE = 1;
	
	private Sprite sprite;
	private BufferedImage image;

	public Instance() {
	}

	public Instance(Sprite sprite, Point location) {
		setSprite(sprite);
		setBounds(location.x, location.y, sprite.getSize(), sprite.getSize());
	}
	
	public Sprite getSprite() {
		return sprite;
	}

	private void setSprite(Sprite sprite) {
		this.sprite = sprite;
		setPreferredSize(new Dimension(sprite.getSize(), sprite.getSize()));
		
		final ImageRenderer renderer = new ImageRenderer();
		
		final TileLayer defaultLayer = sprite.getDefaultLayer();
		if(defaultLayer != null) {
			image = renderer.renderImage(defaultLayer, sprite.getPalette(), TILE_SIZE);
		} else {
			image = renderer.renderImage(sprite.getSize(), sprite.getSize(), TILE_SIZE);
		}
	}

	@Override
	protected void paintComponent(Graphics g) {
		g.drawImage(image, 0, 0, null);
	}
	
	
	public static void main(String[] args) {
		final JFrame frame = new JFrame();
		
		final Map<String, List<TileLayer>> animations = new HashMap<String, List<TileLayer>>();
		final List<TileLayer> walk0 = new ArrayList<TileLayer>();
		animations.put("walk-0.0", walk0);
		
		final TileLayer base = new TileLayer(6, 6);
		base.restoreData(new int[] {
		   -1, 0, 0, 0,-1,-1,
		   -1, 0,-1, 0,-1,-1,
		   -1, 0, 0,-1,-1,-1,
		   -1, 0,-1, 0,-1,-1,
		   -1, 0,-1, 0,-1,-1,
			0, 0,-1, 0, 0, 0,
		}, null);
		walk0.add(base);
		
		final Sprite sprite = new Sprite(6, animations);
		
		final Grid rootPanel = new Grid();
//		final JPanel rootPanel = new JPanel((LayoutManager)null);
		rootPanel.add(new Instance(sprite, new Point(16, 16)));
		rootPanel.setPreferredSize(new Dimension(160, 160));
		frame.setContentPane(rootPanel);
		
		rootPanel.addMouseListener(new MouseAdapter() {

			@Override
			public void mouseClicked(MouseEvent e) {
				rootPanel.add(new Instance(sprite, new Point(e.getX(), e.getY())));
				rootPanel.repaint(e.getX(), e.getY(), 6, 6);
			}
			
		});
		
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.pack();
		frame.setVisible(true);
	}
}
