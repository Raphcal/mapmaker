package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.geom.Point2D;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;
import javax.swing.JOptionPane;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class TreeTool extends MouseAdapter implements Tool {
	private static final int TILTING_SIMPLE = 0;
	private static final int TILTING_ALTERNATE = 1;
	private static final int TILTING_RANDOM = 2;
	
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
	public void setup() {
		// Pas d'action.
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
		if(values.length != 5 && values.length != 6) {
			JOptionPane.showMessageDialog(null, "Taille fournie : " + values.length);
			return;
		}
		
		int tiltingMode = TILTING_SIMPLE;
		String tiltingString = values[1];
		
		if(tiltingString.length() > 0) {
			if(tiltingString.charAt(0) == 'r') {
				tiltingString = tiltingString.substring(1);
				tiltingMode = TILTING_RANDOM;
			} else if(tiltingString.charAt(0) == 'a') {
				tiltingString = tiltingString.substring(1);
				tiltingMode = TILTING_ALTERNATE;
			}
		}
		
		final int leafCount = Integer.parseInt(values[0]);
		final double tilting = Math.toRadians(Double.parseDouble(tiltingString));
		final double wide = Math.toRadians(Double.parseDouble(values[2]));
		final double childLength = Double.parseDouble(values[3]);
		final double trunkHeight = Double.parseDouble(values[4]);
		final int trunkWidth = values.length == 6 ? Integer.parseInt(values[5]) : 1;
		
		final DataLayer layer = (DataLayer) grid.getActiveLayer();
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		painter = new FractalPainter(layer, leafCount, tilting, tiltingMode, wide, childLength);
		painter.firstStep(getTile(), point, trunkHeight, trunkWidth, -Math.PI / 2.0);
		
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
		private double tilting;
		private final int tiltMode;
		private final double wide;
		private final double childLenth;

		private List<State> states;
		
		private final Random random;
		
		public FractalPainter(DataLayer layer, int leafCount, double tilting, int tiltMode, double wide, double childLenth) {
			this.layer = layer;
			this.tiles = layer.copyData();
			this.leafCount = leafCount;
			this.tilting = tilting;
			this.tiltMode = tiltMode;
			this.wide = wide;
			this.childLenth = childLenth;
			this.random = new Random();
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

		public void firstStep(int tile, Point2D root, double length, int width, double angle) {
			states = Collections.singletonList(new State(root, length, width, angle));
			nextStep(tile);
			nextStep(tile);
		}
		
		public void nextStep(int tile) {
			final List<State> nextStates = new ArrayList<State>();
			
			for(final State state : states) {
				draw(state, tile);
				
				final Point2D nextRoot = nextRoot(state);
				final int width = state.getWidth() > 1 ? state.getWidth() - 1 : 1;
				
				final double nextTilt;
				
				switch(tiltMode) {
					case TILTING_ALTERNATE:
						nextTilt = tilting;
						tilting = -tilting;
						break;
					case TILTING_RANDOM:
						nextTilt = tilting * 2.0 * random.nextDouble() - tilting;
						break;
					default:
						nextTilt = tilting;
						break;
				}
				
				double leafAngle = state.getAngle() + nextTilt - wide / 2.0;
				for(int leaf = 0; leaf < leafCount; leaf++) {
					nextStates.add(new State(nextRoot, state.getLength() * childLenth, width, leafAngle));
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
			if(state.getWidth() > 1) {
				draw(state.getRoot(), state.getWidth(), (int) state.getLength(), state.getAngle(), tile);
			} else {
				draw(state.getRoot(), (int) state.getLength(), state.getAngle(), tile);
			}
		}
		
		private void draw(Point2D origin, int length, double angle, int tile) {
			for(int i = 0; i < length; i++) {
				final double x = origin.getX() + Math.cos(angle) * i;
				final double y = origin.getY() + Math.sin(angle) * i;
				
				draw((int) x, (int) y, tile);
			}
		}
		
		private void draw(Point2D origin, int width, int length, double angle, int tile) {
			for(int i = 0; i < length; i++) {
				final double x = origin.getX() + Math.cos(angle) * i;
				final double y = origin.getY() + Math.sin(angle) * i;
				
				draw(new Line(new Point2D.Double(x, y), (double) width, angle), tile);
			}
		}
		
		private void draw(Line line, int tile) {
			final TileLayer tileLayer = new TileLayer(layer.getWidth(), layer.getHeight());
			tileLayer.restoreData(tiles, null);
			tileLayer.setTiles(line.getPointA(), line.getPointB(), tile);
			tiles = tileLayer.copyData();
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
	
	private static class Line {
		private final Point2D a, b;
		private final double width;

		public Line(Point2D origin, double width, double angle) {
			this.width = width;
			
			final double aAngle = angle - Math.PI / 2.0f;
			final double bAngle = angle + Math.PI / 2.0f;
			
			this.a = new Point2D.Double(Math.cos(aAngle) * width / 2.0 + origin.getX(),
					Math.sin(aAngle) * width + origin.getY());
			this.b = new Point2D.Double(Math.cos(bAngle) * width / 2.0 + origin.getX(),
					Math.sin(bAngle) * width + origin.getY());
		}

		public double getWidth() {
			return width;
		}

		public Point2D getA() {
			return a;
		}

		public Point2D getB() {
			return b;
		}
		
		public Point getPointA() {
			return toPoint(a);
		}
		
		public Point getPointB() {
			return toPoint(b);
		}
		
		private Point toPoint(Point2D p) {
			return new Point((int) p.getX(), (int) p.getY());
		}
	}
	
	private static class State {
		private final Point2D root;
		private final double length;
		private final int width;
		private final double angle;

		public State(Point2D root, double length, int width, double angle) {
			this.root = root;
			this.length = length;
			this.width = width;
			this.angle = angle;
		}

		public Point2D getRoot() {
			return root;
		}

		public double getLength() {
			return length;
		}

		public int getWidth() {
			return width;
		}
		
		public double getAngle() {
			return angle;
		}
	}
}
