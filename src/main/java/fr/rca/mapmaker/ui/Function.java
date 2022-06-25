package fr.rca.mapmaker.ui;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.operation.Operation;
import fr.rca.mapmaker.operation.OperationParser;
import java.awt.Color;
import java.awt.Graphics;
import java.awt.Rectangle;
import javax.swing.JComponent;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class Function extends JComponent {

	private static final Color X_COLOR = new Color(255, 0, 0, 128);
	private static final Color Y_COLOR = new Color(0, 255, 0, 128);

	private String xFunction;
	private Operation xOperation;
	private String yFunction;
	private Operation yOperation;

	private int sourceWidth = 1;
	private int sourceHeight = 1;

	public Function() {
	}

	public void setXFunction(String function) {
		this.xFunction = function;
		this.xOperation = OperationParser.parse(function);
		repaint();
	}

	public String getXFunction() {
		return xFunction;
	}

	public void setYFunction(String function) {
		this.yFunction = function;
		this.yOperation = OperationParser.parse(function);
		repaint();
	}

	public String getYFunction() {
		return yFunction;
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
		final boolean hasXFunction = xFunction != null && !xFunction.isEmpty();
		final boolean hasYFunction = yFunction != null && !yFunction.isEmpty();
		if (!hasXFunction && !hasYFunction) {
			return;
		}

		final Rectangle bounds = g.getClipBounds();
		final double w = (double) bounds.width / (double) sourceWidth;
		final double h = (double) bounds.height / (double) sourceHeight;

		final double zoom = Math.max(w, h);

		if (hasXFunction) {
			g.setColor(X_COLOR);
			for (int x = 0; x < sourceWidth; x++) {
				final double y = xOperation.execute((double) x);
				g.fillRect((int) Math.floor(x * zoom), (int) Math.floor(y * zoom), (int) zoom, (int) zoom);
			}
		}
		if (hasYFunction) {
			g.setColor(Y_COLOR);
			for (int y = 0; y < sourceHeight; y++) {
				final double x = yOperation.execute((double) y);
				g.fillRect((int) Math.floor(x * zoom), (int) Math.floor(y * zoom), (int) zoom, (int) zoom);
			}
		}
	}

	public static TileLayer asTileLayer(String function, int width, int height) {
		final TileLayer tileLayer = new TileLayer(width, height);
		final Operation operation = OperationParser.parse(function);

		final AlphaColorPalette alphaColorPalette = AlphaColorPalette.getDefaultColorPalette();
		alphaColorPalette.setSelectedAlpha(3);
		alphaColorPalette.setSelectedTile(1);
		final int tile = alphaColorPalette.getSelectedTile();

		for (int x = 0; x < width; x++) {
			tileLayer.setTile(x, (int) operation.execute((double) x), tile);
		}

		return tileLayer;
	}
}
