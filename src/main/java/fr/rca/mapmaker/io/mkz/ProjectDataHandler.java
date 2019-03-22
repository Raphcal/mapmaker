package fr.rca.mapmaker.io.mkz;

import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.DataHandler;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.common.Streams;
import fr.rca.mapmaker.model.map.MapAndInstances;
import fr.rca.mapmaker.model.map.Packer;
import fr.rca.mapmaker.model.map.PackerFactory;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.image.BufferedImage;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class ProjectDataHandler implements DataHandler<Project> {
	
	public static final String IMAGE_FILE_NAME = "atlas.png";
	public static final String DATA_FILE_NAME = "atlas.sprites";
	public static final int PALETTE_PADDING = 4;
	
	private final Format format;

	public ProjectDataHandler(Format format) {
		this.format = format;
	}

	@Override
	public void write(Project t, OutputStream outputStream) throws IOException {
		final ZipOutputStream zipOutputStream = (ZipOutputStream)outputStream;
		
		// Palettes
		final List<Palette> palettes = t.getPalettes();
		
		final DataHandler<Palette> paletteDataHandler = format.getHandler(Palette.class);
		final DataHandler<BufferedImage> imageHandler = format.getHandler(BufferedImage.class);
		
		for(int index = 0; index < palettes.size(); index++) {
			final Palette palette = palettes.get(index);
			
			zipOutputStream.putNextEntry(new ZipEntry("palette" + index + ".pal"));
			paletteDataHandler.write(palette, outputStream);
			zipOutputStream.closeEntry();
			
			// Écriture des images.
			final BufferedImage image = renderPalette(palette, palette.getTileSize());

			zipOutputStream.putNextEntry(new ZipEntry(palette.toString() + '-' + palette.getTileSize() + ".png"));
			imageHandler.write(image, outputStream);
			zipOutputStream.closeEntry();
		}
		
		// Cartes
		final List<MapAndInstances> maps = t.getMaps();
		
		final DataHandler<TileMap> tileMapHandler = format.getHandler(TileMap.class);
		final DataHandler<Instance> instanceHandler = format.getHandler(Instance.class);
		
		for(int index = 0; index < maps.size(); index++) {
            final MapAndInstances mapAndInstances = maps.get(index);
			final TileMap map = mapAndInstances.getTileMap();
			final List<Instance> instances = new ArrayList<Instance>(mapAndInstances.getSpriteInstances());
			Collections.sort(instances, new Comparator<Instance>() {

				@Override
				public int compare(Instance o1, Instance o2) {
					int order = Integer.valueOf(o1.getX()).compareTo(o2.getX());
					if(order == 0) {
						return Integer.valueOf(o1.getY()).compareTo(o2.getY());
					} else {
						return order;
					}
				}
			});
			
			
			// Écriture de la carte
			final ZipEntry mapEntry = new ZipEntry("map" + index + ".map");
			zipOutputStream.putNextEntry(mapEntry);
			tileMapHandler.write(map, outputStream);
			zipOutputStream.closeEntry();
			
			// Écriture des instances
			final ZipEntry instancesEntry = new ZipEntry("map" + index + ".sprites");
			zipOutputStream.putNextEntry(instancesEntry);
			
			Streams.write(instances.size(), outputStream);
			for(final Instance instance : instances) {
				instanceHandler.write(instance, outputStream);
			}
			zipOutputStream.closeEntry();
		}
		
		// Sprites
		final List<Sprite> sprites = t.getSprites();
		
		// Feuille contenant tous les sprites
        final Packer spritePackMap = PackerFactory.createPacker();
        spritePackMap.addAll(null, sprites, null);
		
        zipOutputStream.putNextEntry(new ZipEntry(IMAGE_FILE_NAME));
        imageHandler.write(spritePackMap.renderImage(), zipOutputStream);
        zipOutputStream.closeEntry();

        final DataHandler<Packer> packMapHandler = format.getHandler(Packer.class);
        zipOutputStream.putNextEntry(new ZipEntry(DATA_FILE_NAME));
        packMapHandler.write(spritePackMap, zipOutputStream);
        zipOutputStream.closeEntry();
		
		// Feuilles de sprites individuelles
		final DataHandler<Sprite> spriteHandler = format.getHandler(Sprite.class);
		
		for(int index = 0; index < sprites.size(); index++) {
			final Sprite sprite = sprites.get(index);
			
			if(!sprite.isEmpty()) {
				final ZipEntry entry = new ZipEntry("sprite" + index + ".png");
				zipOutputStream.putNextEntry(entry);
				spriteHandler.write(sprite, outputStream);
				zipOutputStream.closeEntry();
			}
		}
	}
	
	@Override
	public Project read(InputStream inputStream) throws IOException {
		throw new UnsupportedOperationException("NIY");
	}
	
	public static BufferedImage renderPalette(Palette p, int tileSize) {
		final int space = PALETTE_PADDING;
		// Calcul de l'espace total
		// TODO: il manque la bordure droite et la bordure basse, ajouter "space * space * p.size()" ?
		final long neededSurface = (tileSize + space) * (tileSize + space) * p.size();
		final int size = Surfaces.getNearestUpperPowerOfTwoForSurface(neededSurface);
		// final int lineTileCount = (size / (tileSize + 2));
		// final int space = getNearestLowerPowerOfTwo((size - (lineTileCount * tileSize)) / lineTileCount);
		
		final BufferedImage image = new BufferedImage(size, size, BufferedImage.TYPE_INT_ARGB);
		final Graphics2D graphics = image.createGraphics();
		graphics.setBackground(new Color(0, 0, 0, 0));
		
		int x = space;
		int y = space;
		for(int index = 0; index < p.size(); index++) {
			p.paintTile(graphics, index, x, y - 1, tileSize);
			graphics.clearRect(x, y, tileSize, tileSize);
			p.paintTile(graphics, index, x, y + 1, tileSize);
			graphics.clearRect(x, y, tileSize, tileSize);
			p.paintTile(graphics, index, x - 1, y, tileSize);
			graphics.clearRect(x, y, tileSize, tileSize);
			p.paintTile(graphics, index, x + 1, y, tileSize);
			graphics.clearRect(x, y, tileSize, tileSize);
			p.paintTile(graphics, index, x, y, tileSize);

			// Remarque : pour être propre il faudrait ajouter 2 * l'espace mais
			// vu que l'espace minimum est de 2 pixels, c'est suffisant.
			x += tileSize + space;
			if(x + tileSize + space > size) {
				x = space;
				y += tileSize + space;
			}
		}
		
		graphics.dispose();
		
		return image;
	}
}
