package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JOptionPane;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class TreeTool extends MouseAdapter implements Tool {
	
	private Grid grid;
	private String lastInput;
	
	private FractalPainter painter;
	
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
		final String input = JOptionPane.showInputDialog("Infos de l'arbre ? (feuilles, tilt, angle de largeur, pourcentage branches, taille du tronc)", lastInput);
		lastInput = input;
		
		final String[] values = input.split(",");
		if(values.length != 5) {
			return;
		}
		
		final int leafCount = Integer.parseInt(values[0]);
		final double tilting = Math.toRadians(Double.parseDouble(values[1]));
		final double wide = Math.toRadians(Double.parseDouble(values[2]));
		final double childLength = Double.parseDouble(values[3]);
		final double trunkHeight = Double.parseDouble(values[4]);
		
		final DataLayer layer = (DataLayer) grid.getActiveLayer();
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		painter = new FractalPainter(layer, leafCount, tilting, wide, childLength);
		painter.firstStep(getTile(), point, trunkHeight, -Math.PI / 2.0);
		
		layer.restoreData(painter.getTiles(), null);
	}
	
	public void nextStep() {
		if(painter != null) {
			final DataLayer layer = (DataLayer) grid.getActiveLayer();
			
			painter.setTiles(layer.copyData());
			painter.nextStep(getTile());
			
			layer.restoreData(painter.getTiles(), null);
		}
	}
	
	private int getTile() {
		return grid.getTileMap().getPalette().getSelectedTile();
	}
	
	private static class FractalPainter {
		private final DataLayer layer;
		private int[] tiles;
		private final int leafCount;
		private final double tilting;
		private final double wide;
		private final double childLenth;

		private List<State> states;
		
		public FractalPainter(DataLayer layer, int leafCount, double tilting, double wide, double childLenth) {
			this.layer = layer;
			this.tiles = layer.copyData();
			this.leafCount = leafCount;
			this.tilting = tilting;
			this.wide = wide;
			this.childLenth = childLenth;
		}
		
		public void drawLine(int tile, Point2D root, double length, double angle, int remainingIterations) {
			draw(root, (int) length, angle, tile);
			
			final Point2D nextRoot = nextRoot(root, angle, length);
			
			if(remainingIterations > 0) {
				double leafAngle = angle + tilting - wide / 2.0;
				for(int leaf = 0; leaf < leafCount; leaf++) {
					drawLine(tile, nextRoot, length * childLenth, leafAngle, remainingIterations - 1);

					leafAngle += wide / (double) (leafCount - 1);
				}
			}
		}

		public void firstStep(int tile, Point2D root, double length, double angle) {
			states = Collections.singletonList(new State(root, length, angle));
			nextStep(tile);
			nextStep(tile);
		}
		
		public void nextStep(int tile) {
			final List<State> nextStates = new ArrayList<State>();
			
			for(final State state : states) {
				draw(state, tile);
				
				final Point2D nextRoot = nextRoot(state);
				
				double leafAngle = state.getAngle() + tilting - wide / 2.0;
				for(int leaf = 0; leaf < leafCount; leaf++) {
					nextStates.add(new State(nextRoot, state.getLength() * childLenth, leafAngle));
					leafAngle += wide / (double) (leafCount - 1);
				}
			}
			
			states = nextStates;
		}
		
		private static Point2D.Double nextRoot(State state) {
			return nextRoot(state.getRoot(), state.getAngle(), state.getLength());
		}
		private static Point2D.Double nextRoot(Point2D root, double angle, double length) {
			return new Point2D.Double(root.getX() + Math.cos(angle) * length, root.getY() + Math.sin(angle) * length);
		}
		
		private void draw(State state, int tile) {
			draw(state.getRoot(), (int) state.getLength(), state.getAngle(), tile);
		}
		
		private void draw(Point2D origin, int length, double angle, int tile) {
			for(int i = 0; i < length; i++) {
				final double x = origin.getX() + Math.cos(angle) * i;
				final double y = origin.getY() + Math.sin(angle) * i;
				
				draw((int) x, (int) y, tile);
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

		public void setTiles(int[] tiles) {
			this.tiles = tiles;
		}
		
	}
	
	private static class State {
		private final Point2D root;
		private final double length;
		private final double angle;

		public State(Point2D root, double length, double angle) {
			this.root = root;
			this.length = length;
			this.angle = angle;
		}

		public Point2D getRoot() {
			return root;
		}

		public double getLength() {
			return length;
		}

		public double getAngle() {
			return angle;
		}
	}
}
