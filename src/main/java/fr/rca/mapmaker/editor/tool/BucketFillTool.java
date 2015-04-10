package fr.rca.mapmaker.editor.tool;

import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.model.map.TileLayer;
import java.util.ArrayDeque;
import javax.swing.SwingWorker;

public class BucketFillTool extends MouseAdapter implements Tool {

	private final static int ORIGIN_NONE = 0;
	private final static int ORIGIN_RIGHT = 1;
	private final static int ORIGIN_DOWN = 2;
	private final static int ORIGIN_LEFT = 3;
	private final static int ORIGIN_UP = 4;
	
	private interface PaintAction {
		void paint(ArrayDeque<PaintAction> paintStack);
	}
	
	private final Grid grid;
	
	public BucketFillTool(Grid grid) {
		this.grid = grid;
	}
	
	@Override
	public void mouseClicked(MouseEvent e) {
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();
		final TileLayer previewLayer = grid.getOverlay();
		
		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		
		if(point.x >=0 && point.x < drawingLayer.getWidth() &&
				point.y >= 0 && point.y < drawingLayer.getHeight()) {
		
			final int source = drawingLayer.getTile(point.x, point.y);
			final int target;

			if(e.getButton() == MouseEvent.BUTTON1) {
				final int selectedTile = grid.getTileMap().getPalette().getSelectedTile();
				if(selectedTile == -1) {
					target = -2;
				} else {
					target = selectedTile;
				}

			} else {
				target = -2;
			}

			final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
				@Override
				protected Void doInBackground() throws Exception {
					final int[] data = grid.getOverlay().copyData();
					
					final ArrayDeque<PaintAction> paintStack = new ArrayDeque<PaintAction>();
					paintStack.addLast(createPaintAction(point.x, point.y, data, source, target, ORIGIN_NONE));

					while(!paintStack.isEmpty()) {
						paintStack.removeFirst().paint(paintStack);
					}
					
					grid.getOverlay().restoreData(data, null);
					
					return null;
				}

				@Override
				protected void done() {
					drawingLayer.merge(previewLayer);
					previewLayer.clear();
				}
			};
			
			worker.execute();
		}
	}
	
	private int getTile(int[] tiles, int x, int y) {
		return tiles[y * grid.getOverlay().getWidth() + x];
	}
	
	private void setTile(int[] tiles, int x, int y, int tile) {
		tiles[y * grid.getOverlay().getWidth() + x] = tile;
	}
	
	private PaintAction createPaintAction(final int x, final int y, final int[] tiles, final int source, final int target, final int origin) {
		return new PaintAction() {

			@Override
			public void paint(ArrayDeque<PaintAction> paintStack) {
				setTile(tiles, x, y, target);
				
				if(origin != ORIGIN_LEFT && canPaint(x - 1, y, source, tiles)) {
					paintStack.addFirst(createPaintAction(x - 1, y, tiles, source, target, ORIGIN_RIGHT));
				}
				
				if(origin != ORIGIN_UP && canPaint(x, y - 1, source, tiles)) {
					paintStack.addFirst(createPaintAction(x, y - 1, tiles, source, target, ORIGIN_DOWN));
				}

				if(origin != ORIGIN_RIGHT && canPaint(x + 1, y, source, tiles)) {
					paintStack.addFirst(createPaintAction(x + 1, y, tiles, source, target, ORIGIN_LEFT));
				}

				if(origin != ORIGIN_DOWN && canPaint(x, y + 1, source, tiles)) {
					paintStack.addFirst(createPaintAction(x, y + 1, tiles, source, target, ORIGIN_UP));
				}
			}
		};
	}
	
	private boolean canPaint(int x, int y, int source, int[] tiles) {
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();
		
		return x >= 0 && x < drawingLayer.getWidth() &&
			   y >= 0 && y < drawingLayer.getHeight() &&
			   drawingLayer.getTile(x, y) == source &&
			   getTile(tiles, x, y) == -1;
	}
	
	@Override
	public void reset() {
	}
}
