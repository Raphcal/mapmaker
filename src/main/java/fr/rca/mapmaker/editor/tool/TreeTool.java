package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import javax.swing.JOptionPane;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class TreeTool extends MouseAdapter implements Tool {
	
	private Grid grid;
	private String lastInput;
	
	public TreeTool() {
	}

	public TreeTool(Grid grid) {
		this.grid = grid;
	}
	
	public void setGrid(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void reset() {
		// Rien à remettre à zéro.
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		final String input = JOptionPane.showInputDialog("Infos de l'arbre ? (feuilles, angle d'orientation, angle de largeur, pourcentage branches, taille du tronc, itérations)", lastInput);
		lastInput = input;
		
		final String[] values = input.split(",");
		if(values.length != 6) {
			return;
		}
		
		final int leafCount = Integer.parseInt(values[0]);
		final double tilting = Math.toRadians(Double.parseDouble(values[1]));
		final double wide = Math.toRadians(Double.parseDouble(values[2]));
		final double childLength = Double.parseDouble(values[3]);
		final double trunkHeight = Double.parseDouble(values[4]);
		final int iterations = Integer.parseInt(values[5]);
		
		final DataLayer layer = (DataLayer) grid.getActiveLayer();
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		final FractalPainter painter = new FractalPainter(layer, leafCount, tilting, wide, childLength);
		painter.drawLine(point, trunkHeight, -Math.PI / 2.0, iterations);
		
		layer.restoreData(painter.getTiles(), null);
	}
	
	private static class FractalPainter {
		private final DataLayer layer;
		private final int[] tiles;
		private final int leafCount;
		private final double tilting;
		private final double wide;
		private final double childLenth;

		public FractalPainter(DataLayer layer, int leafCount, double tilting, double wide, double childLenth) {
			this.layer = layer;
			this.tiles = layer.copyData();
			this.leafCount = leafCount;
			this.tilting = tilting;
			this.wide = wide;
			this.childLenth = childLenth;
		}
		
		private void drawLine(Point2D root, double length, double angle, int remainingIterations) {
			for(int i = 0; i < (int) length; i++) {
				final double x = root.getX() + Math.cos(angle) * i;
				final double y = root.getY() + Math.sin(angle) * i;
				
				draw((int) x, (int) y, 0);
			}
			
			final Point2D nextRoot = new Point2D.Double(root.getX() + Math.cos(angle) * length, root.getY() + Math.sin(angle) * length);
			
			if(remainingIterations > 0) {
				double leafAngle = angle + tilting - wide / 2.0;
				for(int leaf = 0; leaf < leafCount; leaf++) {
					drawLine(nextRoot, length * childLenth, leafAngle, remainingIterations - 1);

					leafAngle += wide / (double) leafCount;
				}
			}
		}
		
		private void draw(int x, int y, int tile) {
			if(x >= 0 && x < layer.getWidth() &&
				y >= 0 && y < layer.getHeight()) {
				tiles[layer.getHeight() * y + x] = tile;
			}
		}

		public int[] getTiles() {
			return tiles;
		}
	}
}
