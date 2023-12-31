package fr.rca.mapmaker.editor.tool;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Point;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Arrays;
import javax.swing.AbstractButton;
import javax.swing.Icon;
import javax.swing.SwingWorker;
import lombok.AllArgsConstructor;
import lombok.Data;

/**
 * Rempli avec des lignes. Le taux de transparence défini
 * l'espacement horizontal entre les lignes.
 *
 * Clique droit pour changer l'orientation des lignes.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class FillDiagonalLines extends MouseAdapter implements Tool {

	private final Grid grid;
	private final AbstractButton button;
	private LineOrientation orientation = LineOrientation.DIAGONAL_ASCENDING;

	public FillDiagonalLines(Grid grid, AbstractButton button) {
		this.grid = grid;
		this.button = button;
		this.button.setIcon(orientation.getIcon());
	}

	public LineOrientation getOrientation() {
		return orientation;
	}

	@Override
	public void mouseClicked(MouseEvent e) {
		if (e.getButton() != MouseEvent.BUTTON1) {
			// Changement d'orientation.
			// TODO: Changer l'icône en fonction de l'orientation.
			this.orientation = LineOrientation.values()[(orientation.ordinal() + 1) % LineOrientation.values().length];
			this.button.setIcon(orientation.getIcon());
			return;
		}
		final boolean bold = (e.getModifiersEx() & MouseEvent.ALT_DOWN_MASK) != 0;
		final boolean twice = (e.getModifiersEx() & MouseEvent.CTRL_DOWN_MASK) != 0;

		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();
		final TileLayer previewLayer = grid.getOverlay();

		final Point point = grid.getLayerLocation(e.getX(), e.getY());
		if (point.x < 0 || point.x >= drawingLayer.getWidth()
				|| point.y < 0 || point.y >= drawingLayer.getHeight()) {
			// En dehors du canvas, pas de traitement.
			return;
		}

		final int source = drawingLayer.getTile(point.x, point.y);

		final Palette palette = grid.getTileMap().getPalette();
		final int selectedTile = palette.getSelectedTile();
		final int target, old, spacing;

		if (selectedTile == TileLayer.EMPTY_TILE) {
			target = TileLayer.ERASE_TILE;
			spacing = 2;
		} else if (palette instanceof AlphaColorPalette) {
			target = AlphaColorPalette.getTileFromTile(selectedTile);
			spacing = AlphaColorPalette.getAlphaFromTile(selectedTile) + 1;
		} else {
			target = selectedTile;
			spacing = 2;
		}
		old = source == TileLayer.EMPTY_TILE ? TileLayer.ERASE_TILE : source;

		final SwingWorker<Void, Void> worker = new SwingWorker<Void, Void>() {
			@Override
			protected Void doInBackground() throws Exception {
				final int[] data = grid.getOverlay().copyData();

				// NOTE: L'algorithme est légèrement différent de celui de
				// BucketFillTool car je pensais réussir à faire quelque
				// chose de plus clair et de plus performant mais les
				// performances ne paraissent pas vraiment meilleures.
				// Comme j'aime bien cette implémentation, je l'utilise ici.
				final ArrayList<PaintLocation> locations = new ArrayList<>();
				locations.add(new PaintLocation(point.x, point.y, PaintOrigin.NONE));

				while (!locations.isEmpty()) {
					final PaintLocation location = locations.remove(locations.size() - 1);
					setTile(data, location, orientation.shouldPaintAtLocation(location, spacing, bold, twice) ? target : old);

					for (PaintOrigin origin : location.origin.getOthers()) {
						final PaintLocation nextLocation = origin.translate(location);
						if (canPaint(nextLocation, source, data)) {
							locations.add(nextLocation);
						}
					}
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

	private int getTile(int[] tiles, PaintLocation location) {
		return tiles[location.y * grid.getOverlay().getWidth() + location.x];
	}

	private void setTile(int[] tiles, PaintLocation location, int tile) {
		tiles[location.y * grid.getOverlay().getWidth() + location.x] = tile;
	}

	private boolean canPaint(PaintLocation location, int source, int[] tiles) {
		final TileLayer drawingLayer = (TileLayer) grid.getActiveLayer();

		return location.x >= 0 && location.x < drawingLayer.getWidth()
				&& location.y >= 0 && location.y < drawingLayer.getHeight()
				&& drawingLayer.getTile(location.x, location.y) == source
				&& getTile(tiles, location) == TileLayer.EMPTY_TILE;
	}

	@Override
	public void setup() {
		// Pas d'action.
	}

	@Override
	public void reset() {
		// Pas d'action.
	}

	@AllArgsConstructor
	public static enum LineOrientation {
		DIAGONAL_ASCENDING ("tool_diagonal_fill.png") {
			@Override
			boolean shouldPaintAtLocation(PaintLocation location, int spacing) {
				return (location.x + location.y) % (spacing + 1) == 0;
			}
		},
		HORIZONTAL ("tool_horizontal_fill.png", PaintOrigin.DOWN) {
			@Override
			boolean shouldPaintAtLocation(PaintLocation location, int spacing) {
				return location.y % (spacing + 1) == 0;
			}
		},
		DIAGONAL_DESCENDING ("tool_diagonal2_fill.png") {
			@Override
			boolean shouldPaintAtLocation(PaintLocation location, int spacing) {
				final int modulo = spacing + 1;
				return (location.x - location.y + modulo) % modulo == 0;
			}
		},
		VERTICAL ("tool_vertical_fill.png") {
			@Override
			boolean shouldPaintAtLocation(PaintLocation location, int spacing) {
				return location.x % (spacing + 1) == 0;
			}
		},
		GRID ("tool_grid_fill.png") {
			@Override
			boolean shouldPaintAtLocation(PaintLocation location, int spacing) {
				return location.x % (spacing + 1) == 0 && location.y % (spacing + 1) == 0;
			}
		};

		private final String iconFileName;
		private final PaintOrigin boldDirection;

		private LineOrientation(String iconFileName) {
			this.iconFileName = iconFileName;
			this.boldDirection = PaintOrigin.LEFT;
		}

		abstract boolean shouldPaintAtLocation(PaintLocation location, int spacing);

		boolean shouldPaintAtLocation(PaintLocation location, int spacing, boolean bold, boolean twice) {
			if (twice) {
				location = new PaintLocation(location.x / 2, location.y / 2, location.origin);
			}
			return shouldPaintAtLocation(location, spacing) || (bold && shouldPaintAtLocation(boldDirection.translate(location), spacing));
		}

		Icon getIcon() {
			return new javax.swing.ImageIcon(getClass().getResource("/resources/" + iconFileName));
		}
	}

	@Data
	@AllArgsConstructor
	private static class PaintLocation {
		private int x;
		private int y;
		private PaintOrigin origin;
	}

	private static enum PaintOrigin {
		NONE,
		RIGHT {
			@Override
			PaintLocation translate(PaintLocation source) {
				return new PaintLocation(source.x + 1, source.y, PaintOrigin.LEFT);
			}
		},
		DOWN {
			@Override
			PaintLocation translate(PaintLocation source) {
				return new PaintLocation(source.x, source.y + 1, PaintOrigin.UP);
			}
		},
		LEFT {
			@Override
			PaintLocation translate(PaintLocation source) {
				return new PaintLocation(source.x - 1, source.y, PaintOrigin.RIGHT);
			}
		},
		UP {
			@Override
			PaintLocation translate(PaintLocation source) {
				return new PaintLocation(source.x, source.y - 1, PaintOrigin.DOWN);
			}
		};

		private PaintOrigin[] others;

		PaintOrigin[] getOthers() {
			if (others == null) {
				others = Arrays.stream(values())
						.filter(value -> this != value && value != PaintOrigin.NONE)
						.toArray(PaintOrigin[]::new);
			}
			return others;
		}

		PaintLocation translate(PaintLocation source) {
			return source;
		}
	}
}
