package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayDeque;
import java.util.Deque;
import javax.swing.SwingWorker;
import lombok.AllArgsConstructor;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
@AllArgsConstructor
public class CoatTool extends MouseAdapter implements Tool {

	private final static int ORIGIN_NONE = 0;
	private final static int ORIGIN_RIGHT = 1;
	private final static int ORIGIN_DOWN = 2;
	private final static int ORIGIN_LEFT = 3;
	private final static int ORIGIN_UP = 4;

	private interface PaintAction {

		void paint(Deque<PaintAction> paintStack);
	}

	private final Grid grid;

	@Override
	public void mouseClicked(MouseEvent e) {
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();
		final TileLayer previewLayer = grid.getOverlay();
		final boolean[] done = new boolean[drawingLayer.getWidth() * drawingLayer.getHeight()];

		final Point point = grid.getLayerLocation(e.getX(), e.getY());

		if (point.x >= 0 && point.x < drawingLayer.getWidth()
				&& point.y >= 0 && point.y < drawingLayer.getHeight()) {

			final int source = drawingLayer.getTile(point.x, point.y);
			final int target;

			if (e.getButton() == MouseEvent.BUTTON1) {
				final int selectedTile = grid.getTileMap().getPalette().getSelectedTile();
				if (selectedTile == -1) {
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

					final Deque<PaintAction> paintStack = new ArrayDeque<>();
					paintStack.addLast(createPaintAction(point.x, point.y, data, done, source, target, ORIGIN_NONE));

					while (!paintStack.isEmpty()) {
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

	private PaintAction createPaintAction(final int x, final int y, final int[] tiles, final boolean[] done, final int source, final int target, final int origin) {
		return new PaintAction() {

			@Override
			public void paint(Deque<PaintAction> paintStack) {
				done[y * ((TileLayer) grid.getActiveLayer()).getWidth() + x] = true;

				if (origin != ORIGIN_LEFT && canGo(x - 1, y, source, done)) {
					paintStack.addFirst(createPaintAction(x - 1, y, tiles, done, source, target, ORIGIN_RIGHT));
				} else if (origin != ORIGIN_LEFT && canPaint(x - 1, y, source, done)) {
					setTile(tiles, x, y, target);
				}

				if (origin != ORIGIN_UP && canGo(x, y - 1, source, done)) {
					paintStack.addFirst(createPaintAction(x, y - 1, tiles, done, source, target, ORIGIN_DOWN));
				} else if (origin != ORIGIN_UP && canPaint(x, y - 1, source, done)) {
					setTile(tiles, x, y, target);
				}

				if (origin != ORIGIN_RIGHT && canGo(x + 1, y, source, done)) {
					paintStack.addFirst(createPaintAction(x + 1, y, tiles, done, source, target, ORIGIN_LEFT));
				} else if (origin != ORIGIN_RIGHT && canPaint(x + 1, y, source, done)) {
					setTile(tiles, x, y, target);
				}

				if (origin != ORIGIN_DOWN && canGo(x, y + 1, source, done)) {
					paintStack.addFirst(createPaintAction(x, y + 1, tiles, done, source, target, ORIGIN_UP));
				} else if (origin != ORIGIN_DOWN && canPaint(x, y + 1, source, done)) {
					setTile(tiles, x, y, target);
				}
			}
		};
	}

	private boolean canGo(int x, int y, int source, boolean[] done) {
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();

		return x >= 0 && x < drawingLayer.getWidth()
				&& y >= 0 && y < drawingLayer.getHeight()
				&& drawingLayer.getTile(x, y) == source
				&& !done[y * drawingLayer.getWidth() + x];
	}

	private boolean canPaint(int x, int y, int source, boolean[] done) {
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();

		return x >= 0 && x < drawingLayer.getWidth()
				&& y >= 0 && y < drawingLayer.getHeight()
				&& drawingLayer.getTile(x, y) != source;
	}

	@Override
	public void setup() {
		// Vide.
	}

	@Override
	public void reset() {
		// Vide.
	}
}
