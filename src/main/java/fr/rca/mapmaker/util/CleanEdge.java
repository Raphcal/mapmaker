package fr.rca.mapmaker.util;

import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.operation.Operation;
import fr.rca.mapmaker.operation.OperationParser;
import fr.rca.mapmaker.ui.Grid;
import java.awt.Dimension;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.geom.Point2D;
import java.io.InputStream;
import javax.swing.JFrame;
import javax.swing.Timer;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.Value;

/**
 * Portage de l'algorithme
 * <a href="https://gist.github.com/torcado194/e2794f5a4b22049ac0a41f972d14c329">cleanEdge</a>.
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class CleanEdge {
	private static final Point CENTER = new Point(0.5, 0.5);
	private static final Color TRANSPARENT = new Color(0, 0, 0, 0);

	/**
	 * Gère des pentes au lieu de ne gérer que des angles à 45°. Obligatoire
	 * pour les rotations.
	 */
	private boolean slope;
	/**
	 * Nettoie les petites imperfections. Inutile pour les rotations.
	 */
	private boolean cleanUpSmallDetails;

	/**
	 * Couleur ayant la plus grande priorité.
	 */
	@Builder.Default
	private Color highestColor = new Color(1, 1, 1, 1);

	/**
	 * Détermine à quelle distance deux couleurs doivent être pour être
	 * considérées identiues. Peut créer des artéfactes. Doit être bas pour un
	 * meilleur résultat.
	 */
	@Builder.Default
	private double similarThreshold = 0.1;

	@Builder.Default
	private double lineWidth = 1.0;

	private Point scale;

	private Double scaleRate;

	private Dimension dimension;

	@Builder.Default
	private double rotation = 0.0;

	private String function;

	private Operation operation;

	private ColorPalette palette;

	public TileLayer shaded(DataLayer source) {
		final TileLayer layer = new TileLayer(source);
		shade(layer);
		return layer;
	}

	public void shade(DataLayer layer) {
		if (function != null && operation == null) {
			operation = OperationParser.parse(function);
		}
		if (scale == null && scaleRate != null) {
			scale = new Point(scaleRate, scaleRate);
		} else if (scale == null && dimension != null) {
			scale = new Point((double) dimension.width / layer.getWidth(), (double) dimension.height / layer.getHeight());
		} else if (scale == null) {
			scale = new Point(1.0, 1.0);
		}
		final int width = (int) Math.ceil(layer.getWidth() * scale.x);
		final int height = (int) Math.ceil(layer.getHeight() * scale.y);
		final int max = width * height;

		final int[] tiles = new int[max];
		for (int index = 0; index < max; index++) {
			Point point = new Point(index % width + scale.x / 2, index / width + scale.y / 2);

			if (operation != null) {
				point.y = point.y - operation.execute(point.x);
			}
			if (rotation != 0) {
				final double middleX = width / 2.0;
				final double middleY = height / 2.0;

				final double displacementX = point.x - middleX;
				final double displacementY = point.y - middleY;

				final double xPrime = displacementX * Math.cos(rotation) + displacementY * Math.sin(rotation);
				final double yPrime = -displacementX * Math.sin(rotation) + displacementY * Math.cos(rotation);

				point = new Point(xPrime + middleX, yPrime + middleY);
			}
			if (scale.x != 1 || scale.y != 1) {
				point = new Point(point.x / scale.x, point.y / scale.y);
			}

			tiles[index] = fragment(point, layer);
		}
		layer.restoreData(tiles, width, height);
	}

	private double distance(int lhs, int rhs) {
		return distance(getTileColor(lhs), getTileColor(rhs));
	}

	private static double distance(Color lhs, Color rhs) {
		return Math.sqrt(
				(lhs.getRed() - rhs.getRed()) * (lhs.getRed() - rhs.getRed())
				+ (lhs.getGreen() - rhs.getGreen()) * (lhs.getGreen() - rhs.getGreen())
				+ (lhs.getBlue() - rhs.getBlue()) * (lhs.getBlue() - rhs.getBlue())
				+ (lhs.getAlpha() - rhs.getAlpha()) * (lhs.getAlpha() - rhs.getAlpha())
		);
	}

	private static double rgbDistance(Color lhs, Color rhs) {
		return Math.sqrt(
				(lhs.getRed() - rhs.getRed()) * (lhs.getRed() - rhs.getRed())
				+ (lhs.getGreen() - rhs.getGreen()) * (lhs.getGreen() - rhs.getGreen())
				+ (lhs.getBlue() - rhs.getBlue()) * (lhs.getBlue() - rhs.getBlue())
		);
	}

	/**
	 * Indique si les deux couleurs données sont similaires.
	 *
	 * @param color1
	 * @param color2
	 * @return
	 */
	private boolean isSimilar(Color lhs, Color rhs) {
		return (lhs.getAlpha() == 0 && rhs.getAlpha() == 0) || (distance(lhs, rhs) <= similarThreshold);
	}

	private boolean isSimilar(int lhs, int rhs) {
		return isSimilar(getTileColor(lhs), getTileColor(rhs));
	}

	private boolean isSimilar(Color color1, Color color2, Color color3) {
		return isSimilar(color1, color2) && isSimilar(color2, color3);
	}

	private boolean isSimilar(int color1, int color2, int color3) {
		return isSimilar(getTileColor(color1), getTileColor(color2), getTileColor(color3));
	}

	private boolean isSimilar(Color color1, Color color2, Color color3, Color color4) {
		return isSimilar(color1, color2) && isSimilar(color2, color3) && isSimilar(color3, color4);
	}

	private boolean isSimilar(int color1, int color2, int color3, int color4) {
		return isSimilar(getTileColor(color1), getTileColor(color2), getTileColor(color3), getTileColor(color4));
	}

	private boolean isHigher(Color thisColor, Color otherColor) {
		if (isSimilar(thisColor, otherColor)) {
			return false;
		} else if (thisColor.getAlpha() == otherColor.getAlpha()) {
			return rgbDistance(thisColor, highestColor) < rgbDistance(otherColor, highestColor);
		} else {
			return thisColor.getAlpha() > otherColor.getAlpha();
		}
	}

	private boolean isHigher(int thisColor, int otherColor) {
		return isHigher(getTileColor(thisColor), getTileColor(otherColor));
	}

	private Integer sliceDist(Point point, Point mainDir, Point pointDir, int u, int uf, int uff, int b, int c, int f, int ff, int db, int d, int df, int dff, int ddb, int dd, int ddf) {
		final double minWidth, maxWidth;
		if (slope) {
			minWidth = 0.44;
			maxWidth = 1.142;
		} else {
			minWidth = 0.0;
			maxWidth = 1.4;
		}
		final double _lineWidth = Math.max(minWidth, Math.min(maxWidth, lineWidth));
		//flip point
		point = mainDir.multiply(point.substract(0.5)).add(0.5);

		//edge detection
		final double distAgainst = 4.0 * distance(f, d) + distance(uf, c) + distance(c, db) + distance(ff, df) + distance(df, dd);
		final double distTowards = 4.0 * distance(c, df) + distance(u, f) + distance(f, dff) + distance(b, d) + distance(d, ddf);
		boolean shouldSlice =
				(distAgainst < distTowards)
				|| (distAgainst < distTowards + 0.001) && !isHigher(c, f); //equivalent edges edge case
		if (isSimilar(f, d, b, u) && isSimilar(uf, df, db/*, ub*/) && !isSimilar(c, f)) { //checkerboard edge case
			shouldSlice = false;
		}
		if (!shouldSlice) {
			return null;
		}

		double dist;
		boolean flip = false;

		if (slope && isSimilar(f, d, db) && !isSimilar(f, d, b) && !isSimilar(uf, db)) { //lower shallow 2:1 slant
			//single pixel wide diagonal, dont flip
			final boolean singlePixelWideDiagonal = isSimilar(c, df) && isHigher(c, f);
			if (!singlePixelWideDiagonal) {
				//priority edge cases
				if (isHigher(c, f)) {
					flip = true;
				}
				if (isSimilar(u, f) && !isSimilar(c, df) && !isHigher(c, u)) {
					flip = true;
				}
			}

			if (flip) {
				//midpoints of neighbor two-pixel groupings
				dist = _lineWidth - point.distToLine(CENTER.add(pointDir.multiply(1.5, -1.0)), CENTER.add(pointDir.multiply(-0.5, 0.0)), pointDir.negative());
			} else {
				//midpoints of neighbor two-pixel groupings
				dist = point.distToLine(CENTER.add(pointDir.multiply(1.5, 0.0)), CENTER.add(pointDir.multiply(-0.5, 1.0)), pointDir);
			}

			//cleanup slant transitions
			if (cleanUpSmallDetails && !flip && isSimilar(c, uf) && !(isSimilar(c, uf, uff) && !isSimilar(c, uf, ff) && !isSimilar(d, uff))) { //shallow
				double dist2 = point.distToLine(CENTER.add(pointDir.multiply(2.0, -1.0)), CENTER.add(pointDir.multiply(-0.0, 1.0)), pointDir);
				dist = Math.min(dist, dist2);
			}

			dist -= (_lineWidth / 2.0);
			return dist <= 0.0 ? ((distance(c, f) <= distance(c, d)) ? f : d) : null;
		} else if (slope && isSimilar(uf, f, d) && !isSimilar(u, f, d) && !isSimilar(uf, db)) { //forward steep 2:1 slant
			//single pixel wide diagonal, dont flip
			final boolean singlePixelWideDiagonal = isSimilar(c, df) && isSimilar(c, d);
			if (!singlePixelWideDiagonal) {
				//priority edge cases
				if (isHigher(c, d)) {
					flip = true;
				}
				if (isSimilar(b, d) && !isSimilar(c, df) && !isHigher(c, d)) {
					flip = true;
				}
			}

			if (flip) {
				//midpoints of neighbor two-pixel groupings
				dist = _lineWidth - point.distToLine(CENTER.add(pointDir.multiply(0.0, -0.5)), CENTER.add(pointDir.multiply(-1.0, 1.5)), pointDir.negative());
			} else {
				//midpoints of neighbor two-pixel groupings
				dist = point.distToLine(CENTER.add(pointDir.multiply(1.0, -0.5)), CENTER.add(pointDir.multiply(0.0, 1.5)), pointDir);
			}

			//cleanup slant transitions
			if (cleanUpSmallDetails && !flip && isSimilar(c, db) && !(isSimilar(c, db, ddb) && !isSimilar(c, db, dd) && !isSimilar(f, ddb))) { //steep
				double dist2 = point.distToLine(CENTER.add(pointDir.multiply(1.0, 0.0)), CENTER.add(pointDir.multiply(-1.0, 2.0)), pointDir);
				dist = Math.min(dist, dist2);
			}

			dist -= (_lineWidth / 2.0);
			return dist <= 0.0 ? ((distance(c, f) <= distance(c, d)) ? f : d) : null;
		} else if (isSimilar(f, d)) { //45 diagonal
			if (isSimilar(c, df) && isHigher(c, f)) { //single pixel diagonal along neighbors, dont flip
				if (!isSimilar(c, dd) && !isSimilar(c, ff)) { //line against triple color stripe edge case
					flip = true;
				}
			} else {
				//priority edge cases
				if (isHigher(c, f)) {
					flip = true;
				}
				if (!isSimilar(c, b) && isSimilar(b, f, d, u)) {
					flip = true;
				}
			}
			//single pixel 2:1 slope, dont flip
			if ((((isSimilar(f, db) && isSimilar(u, f, df)) || (isSimilar(uf, d) && isSimilar(b, d, df))) && !isSimilar(c, df))) {
				flip = true;
			}

			if (flip) {
				//midpoints of own diagonal pixels
				dist = _lineWidth - point.distToLine(CENTER.add(pointDir.multiply(1.0, -1.0)), CENTER.add(pointDir.multiply(-1.0, 1.0)), pointDir.negative());
			} else {
				//midpoints of corner neighbor pixels
				dist = point.distToLine(CENTER.add(pointDir.multiply(1.0, 0.0)), CENTER.add(pointDir.multiply(0.0, 1.0)), pointDir);
			}

			//cleanup slant transitions
			if (slope && cleanUpSmallDetails) {
				if (!flip && isSimilar(c, uf, uff) && !isSimilar(c, uf, ff) && !isSimilar(d, uff)) { //shallow
					double dist2 = point.distToLine(CENTER.add(pointDir.multiply(1.5, 0.0)), CENTER.add(pointDir.multiply(-0.5, 1.0)), pointDir);
					dist = Math.max(dist, dist2);
				}

				if (!flip && isSimilar(ddb, db, c) && !isSimilar(dd, db, c) && !isSimilar(ddb, f)) { //steep
					double dist2 = point.distToLine(CENTER.add(pointDir.multiply(1.0, -0.5)), CENTER.add(pointDir.multiply(0.0, 1.5)), pointDir);
					dist = Math.max(dist, dist2);
				}
			}
			dist -= (_lineWidth / 2.0);
			return dist <= 0.0 ? ((distance(c, f) <= distance(c, d)) ? f : d) : null;
		} else if (slope && isSimilar(ff, df, d) && !isSimilar(ff, df, c) && !isSimilar(uff, d)) { //far corner of shallow slant 

			if (isSimilar(f, dff) && isHigher(f, ff)) { //single pixel wide diagonal, dont flip

			} else {
				//priority edge cases
				if (isHigher(f, ff)) {
					flip = true;
				}
				if (isSimilar(uf, ff) && !isSimilar(f, dff) && !isHigher(f, uf)) {
					flip = true;
				}
			}
			if (flip) {
				//midpoints of neighbor two-pixel groupings
				dist = _lineWidth - point.distToLine(CENTER.add(pointDir.multiply(1.5 + 1.0, -1.0)), CENTER.add(pointDir.multiply(-0.5 + 1.0, 0.0)), pointDir.negative());
			} else {
				//midpoints of neighbor two-pixel groupings
				dist = point.distToLine(CENTER.add(pointDir.multiply(1.5 + 1.0, 0.0)), CENTER.add(pointDir.multiply(-0.5 + 1.0, 1.0)), pointDir);
			}

			dist -= (_lineWidth / 2.0);
			return dist <= 0.0 ? ((distance(f, ff) <= distance(f, df)) ? ff : df) : null;
		} else if (slope && isSimilar(f, df, dd) && !isSimilar(c, df, dd) && !isSimilar(f, ddb)) { //far corner of steep slant
			//single pixel wide diagonal, dont flip
			final boolean singlePixelWideDiagonal = isSimilar(d, ddf) && isHigher(d, dd);
			if (!singlePixelWideDiagonal) {
				//priority edge cases
				if (isHigher(d, dd)) {
					flip = true;
				}
				if (isSimilar(db, dd) && !isSimilar(d, ddf) && !isHigher(d, dd)) {
					flip = true;
				}
			}

			if (flip) {
				//midpoints of neighbor two-pixel groupings
				dist = _lineWidth - point.distToLine(CENTER.add(pointDir.multiply(0.0, -0.5 + 1.0)), CENTER.add(pointDir.multiply(-1.0, 1.5 + 1.0)), pointDir.negative());
			} else {
				//midpoints of neighbor two-pixel groupings
				dist = point.distToLine(CENTER.add(pointDir.multiply(1.0, -0.5 + 1.0)), CENTER.add(pointDir.multiply(0.0, 1.5 + 1.0)), pointDir);
			}
			dist -= (_lineWidth / 2.0);
			return dist <= 0.0 ? ((distance(d, df) <= distance(d, dd)) ? df : dd) : null;
		}
		return null;
	}

	private int texture(DataLayer layer, Point point) {
		return layer.getTile((int) point.x, (int) point.y);
	}

	private Color getTileColor(int tile) {
		final java.awt.Color color = palette.getColor(tile);
		return color != null
				? new Color(color.getRed() / 255.0, color.getGreen() / 255.0, color.getBlue() / 255.0, color.getAlpha() / 255.0)
				: TRANSPARENT;
	}

	private int fragment(Point px, DataLayer layer) {
		px = px.add(0.0001);
		Point local = px.fractional();
		px = px.floor();

		Point pointDir = local.round().multiply(2).substract(1);

		//neighbor pixels
		//Up, Down, Forward, and Back
		//relative to quadrant of current location within pixel

		int uub = texture(layer, px.add(pointDir.multiply(-1.0, -2.0)));
		int uu  = texture(layer, px.add(pointDir.multiply( 0.0, -2.0)));
		int uuf = texture(layer, px.add(pointDir.multiply( 1.0, -2.0)));

		int ubb = texture(layer, px.add(pointDir.multiply(-2.0, -2.0)));
		int ub  = texture(layer, px.add(pointDir.multiply(-1.0, -1.0)));
		int u   = texture(layer, px.add(pointDir.multiply( 0.0, -1.0)));
		int uf  = texture(layer, px.add(pointDir.multiply( 1.0, -1.0)));
		int uff = texture(layer, px.add(pointDir.multiply( 2.0, -1.0)));

		int bb  = texture(layer, px.add(pointDir.multiply(-2.0, 0.0)));
		int b   = texture(layer, px.add(pointDir.multiply(-1.0, 0.0)));
		int c   = texture(layer, px.add(pointDir.multiply( 0.0, 0.0)));
		int f   = texture(layer, px.add(pointDir.multiply( 1.0, 0.0)));
		int ff  = texture(layer, px.add(pointDir.multiply( 2.0, 0.0)));

		int dbb = texture(layer, px.add(pointDir.multiply(-2.0, 1.0)));
		int db  = texture(layer, px.add(pointDir.multiply(-1.0, 1.0)));
		int d   = texture(layer, px.add(pointDir.multiply( 0.0, 1.0)));
		int df  = texture(layer, px.add(pointDir.multiply( 1.0, 1.0)));
		int dff = texture(layer, px.add(pointDir.multiply( 2.0, 1.0)));

		int ddb = texture(layer, px.add(pointDir.multiply(-1.0, 2.0)));
		int dd  = texture(layer, px.add(pointDir.multiply( 0.0, 2.0)));
		int ddf = texture(layer, px.add(pointDir.multiply( 1.0, 2.0)));

		int col = c;

		//c_orner, b_ack, and u_p slices
		// (slices from neighbor pixels will only ever reach these 3 quadrants
		Integer c_col = sliceDist(local, new Point( 1.0, 1.0), pointDir, u, uf, uff, b, c, f, ff, db, d, df, dff, ddb, dd, ddf);
		Integer b_col = sliceDist(local, new Point(-1.0, 1.0), pointDir, u, ub, ubb, f, c, b, bb, df, d, db, dbb, ddf, dd, ddb);
		Integer u_col = sliceDist(local, new Point( 1.0, -1.0), pointDir, d, df, dff, b, c, f, ff, ub, u, uf, uff, uub, uu, uuf);

		if (c_col != null) {
			col = c_col;
		}
		if (b_col != null) {
			col = b_col;
		}
		if (u_col != null) {
			col = u_col;
		}

		return col;
	}

	public static final class Point extends Point2D.Double {

		public Point(double x, double y) {
			super(x, y);
		}

		public Point add(Point2D other) {
			return add(other.getX(), other.getY());
		}

		public Point add(double value) {
			return add(value, value);
		}

		public Point add(double x, double y) {
			return new Point(this.x + x, this.y + y);
		}

		public Point substract(Point2D other) {
			return substract(other.getX(), other.getY());
		}

		public Point substract(double value) {
			return substract(value, value);
		}

		public Point substract(double x, double y) {
			return new Point(this.x - x, this.y - y);
		}

		public Point multiply(Point2D other) {
			return multiply(other.getX(), other.getY());
		}

		public Point multiply(double value) {
			return multiply(value, value);
		}

		public Point multiply(double x, double y) {
			return new Point(this.x * x, this.y * y);
		}

		public Point negative() {
			return multiply(-1);
		}

		public Point divide(Point2D other) {
			return divide(other.getX(), other.getY());
		}

		public Point divide(double value) {
			return divide(value, value);
		}

		public Point divide(double x, double y) {
			return new Point(this.x / x, this.y / y);
		}

		public Point square() {
			return new Point(x * x, y * y);
		}

		public Point sqrt() {
			return new Point(Math.sqrt(x), Math.sqrt(y));
		}

		public Point round() {
			return new Point(Math.round(x), Math.round(y));
		}

		public Point ceil() {
			return new Point(Math.ceil(x), Math.ceil(y));
		}

		public Point floor() {
			return new Point(Math.floor(x), Math.floor(y));
		}

		public double dotProduct(Point2D other) {
			return x * other.getX() + y * other.getY();
		}

		public double length() {
			return Math.sqrt(x * x + y * y);
		}

		/**
		 * Renvoi un point représentant le vecteur donné normalisé entre 0 et 1.
		 * @return Point normalisé.
		 */
		public Point normalized() {
			final double length = length();
			return new Point(x / length, y / length);
		}

		public Point fractional() {
			return new Point(fractionalOrZero(x), fractionalOrZero(y));
		}
		private double fractionalOrZero(double value) {
			return value < 0
					? value + Math.floor(-value)
					: value - Math.floor(value);
		}

		public double distToLine(Point pt1, Point pt2, Point dir) {
			final Point lineDir = pt2.substract(pt1);
			final Point perpDir = new Point(lineDir.getY(), -lineDir.getX());
			final Point dirToPt1 = pt1.substract(this);
			return (perpDir.dotProduct(dir) > 0.0 ? 1.0 : -1.0)
					* perpDir.normalized().dotProduct(dirToPt1);
		}
	}

	@Value
	@AllArgsConstructor
	public static final class Color {
		double red, green, blue, alpha;

		public Color() {
			this.red = 0;
			this.green = 0;
			this.blue = 0;
			this.alpha = 0;
		}

		public Color(double red, double green, double blue) {
			this(red, green, blue, 1);
		}
	}

	public static void main(String[] args) throws Exception {
		InternalFormat format = new InternalFormat();
		format.setVersion(InternalFormat.VERSION_15);
		InputStream inputStream = CleanEdge.class.getResourceAsStream("/test-rotate.mmk");
		format.readHeader(inputStream);
		Project project = format.getHandler(Project.class).read(inputStream);

		TileLayer source = project.getSprites().get(0).get(Animation.ANIMATION_NAMES.get(0)).getFrames(0.0).get(0);
		TileLayer base = new TileLayer(source);
		TileLayer rotated = new TileLayer(source);

		Timer timer = new Timer(500, new ActionListener() {
			private int rotation = 0;

			@Override
			public void actionPerformed(ActionEvent e) {
				rotation = (rotation + 10) % 360;
				rotated.restoreData(source);
				if (rotation % 90 == 0) {
					rotated.rotate90(rotation / 90);
				} else {
					CleanEdge.builder()
						.palette(AlphaColorPalette.getDefaultColorPalette())
						.rotation(Math.toRadians(rotation))
						.slope(true)
						.cleanUpSmallDetails(true)
						.build()
						.shade(rotated);
				}
			}
		});
		timer.start();

		Grid grid = new Grid();
		grid.setZoom(8);
		TileMap tileMap = new TileMap(base, AlphaColorPalette.getDefaultColorPalette());
		tileMap.add(rotated);
		grid.setTileMap(tileMap);

		JFrame frame = new JFrame();
		frame.getContentPane().add(grid);
		frame.pack();
		frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
		frame.setVisible(true);
	}
}
