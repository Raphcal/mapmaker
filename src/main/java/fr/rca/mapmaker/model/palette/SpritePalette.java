package fr.rca.mapmaker.model.palette;

import fr.rca.mapmaker.editor.SpriteEditor;
import fr.rca.mapmaker.event.Event;
import fr.rca.mapmaker.event.EventBus;
import fr.rca.mapmaker.model.HasSizeChangeListeners;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.ui.Paints;
import fr.rca.mapmaker.util.CleanEdge;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.image.BufferedImage;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
public class SpritePalette extends AbstractEditablePalette<Sprite> implements EditablePalette, HasSizeChangeListeners {

	private final List<String> animationNames;

	public SpritePalette() {
		this.animationNames = Arrays.stream(Animation.getDefaultAnimations())
				.map(Animation::getName)
				.collect(Collectors.toList());
		this.columns = 4;
		refreshSource(columns - 1);
	}

	public SpritePalette(List<Sprite> sprites, List<String> animationNames) {
		this.sources = sprites;
		this.animationNames = animationNames;
		this.columns = 4;

		for (int index = 0; index < sprites.size(); index++) {
			renderTile(index);
		}
		refreshSource(columns - 1);
	}

	@Override
	public void paintTile(Graphics g, int tile, int refX, int refY, int size) {
		if (tile >= 0 && tile < tiles.size()) {
			g.drawImage(tiles.get(tile), refX, refY, size, size, null);
		}
	}

	@Override
	public String toString() {
		return name;
	}

	@Override
	public int getTileSize() {
		return 32;
	}

	@Override
	public int getTileSize(int tile) {
		final Sprite sprite = sources.get(tile);
		// TODO: Faire mieux, peut-être fixer la valeur et dessiner en centré.
		return sprite.getWidth();
	}

	public Sprite getSelectedSprite() {
		final int selectedTile = getSelectedTile();
		if (selectedTile >= 0 && selectedTile < sources.size()) {
			return sources.get(selectedTile);
		} else {
			return null;
		}
	}

	@Override
	public void editTile(final int index, java.awt.Frame parent) {
		final SpriteEditor editor = new SpriteEditor(parent);
		editor.setSprite(sources.get(index), animationNames);
		editor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				refreshSource(index);
				EventBus.INSTANCE.fireEvent(Event.SPRITE_CHANGED, sources);
			}
		});
		editor.setVisible(true);
	}

	@Override
	public void refresh() {
		refreshSource(columns - 1);
	}

	@Override
	protected BufferedImage render(Sprite sprite) {
		final ColorPalette palette = sprite.getPalette();

		final int size = getTileSize();
		final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();

		// Fond
		graphics.setPaint(Paints.TRANSPARENT_PAINT);
		graphics.fillRect(0, 0, size, size);

		// Sprite
		final TileLayer defaultLayer = sprite.getDefaultLayer(animationNames);

		if (defaultLayer != null) {
			TileLayer layerToPaint;
			if (defaultLayer.getWidth() != size && defaultLayer.getHeight() != size) {
				layerToPaint = new TileLayer(defaultLayer);
				double scale = Math.max((double)size / defaultLayer.getWidth(), (double)size / defaultLayer.getHeight());
				CleanEdge.builder()
						.palette(palette)
						.scale(new CleanEdge.Point(scale, scale))
						.slope(true)
						.cleanUpSmallDetails(true)
						.build()
						.shade(layerToPaint);
			} else {
				layerToPaint = defaultLayer;
			}

			final int tileSize = 1;
			final int layerWidth = Math.min(layerToPaint.getWidth(), size);
			final int layerHeight = Math.min(layerToPaint.getHeight(), size);

			final int left = layerWidth < size ? (size - layerWidth) / 2 : 0;
			final int top = layerHeight < size ? (size - layerHeight) / 2 / 2: 0;

			for (int y = 0; y < layerHeight; y++) {
				for (int x = 0; x < layerWidth; x++) {
					palette.paintTile(graphics, layerToPaint.getTile(x, y), x * tileSize + left, y * tileSize + top, tileSize);
				}
			}
		}

		graphics.dispose();
		return image;
	}

	@Override
	protected Sprite createEmptySource() {
		return new Sprite();
	}
}
