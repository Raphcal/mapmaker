package fr.rca.mapmaker.model.palette;

import java.awt.Color;
import java.awt.Graphics;

public class ColorPalette implements Palette {

	private Color[] colors;
	private Color[] inverses;
	private int selectedTile = -1;

	public ColorPalette() {
		this(getDefaultColors());
	}
	
	public ColorPalette(int length) {
		this.colors = new Color[length];
		this.inverses = new Color[length];
	}
	
	public ColorPalette(Color... colors) {
		setColorsAndInverses(colors);
	}
	
	@Override
	public boolean isEditable() {
		return false;
	}
	
	private void inverse(int index) {
		inverses[index] = new Color(~colors[index].getRGB(), false);
	}
	
	public Color getColor(int index) {
		return colors[index];
	}
	
	public void setColor(int index, Color color) {
		colors[index] = color;
		inverse(index);
	}
	
	public void setColors(int index, Color... colors) {
		System.arraycopy(colors, 0, this.colors, index, colors.length);
		
		final int length = index + colors.length;
		for(int i = index; i < length; i++) {
			inverse(i);
		}
	}
	
	public void setColors(Color... colors) {
		setColorsAndInverses(colors);
	}
	
	private void setColorsAndInverses(Color[] colors) {
		this.colors = colors;
		this.inverses = new Color[colors.length];
		
		for(int index = 0; index < colors.length; index++) {
			inverse(index);
		}
	}
	
	@Override
	public void paintTile(Graphics g, int tile, int x, int y, int size) {
		
		if(tile >= 0 && tile < colors.length && colors[tile] != null) {
			g.setColor(colors[tile]);
			g.fillRect(x, y, size, size);
		}
	}

	@Override
	public int getTileSize() {
		return 1;
	}

	@Override
	public int getTileSize(int tile) {
		return getTileSize();
	}
	
	@Override
	public int size() {
		return colors.length;
	}
	
	@Override
	public void setSelectedTile(int tile) {
		selectedTile = tile;
	}

	@Override
	public int getSelectedTile() {
		return selectedTile;
	}
	
	public Color getInverseColor() {
		
		if(selectedTile >= 0 && selectedTile < colors.length)
			return inverses[selectedTile];
		else
			return Color.WHITE;
	}

	public Color[] getColors() {
		return colors;
	}

	@Override
	public void refresh() {
	}
	
	protected static Color[] getDefaultColors() {
		return new Color[] {
			new Color(0, 0, 6),
			new Color(232, 250, 255),
			new Color(235, 233, 248),
			new Color(255, 238, 238),
			new Color(253, 255, 214),
			new Color(196, 255, 217),
			new Color(0, 246, 245),
			new Color(229, 254, 216),
			new Color(210, 0, 0),
			new Color(205, 245, 255),
			new Color(219, 214, 245),
			new Color(255, 207, 208),
			new Color(250, 255, 127),
			new Color(153, 248, 181),
			new Color(0, 230, 228),
			new Color(217, 254, 216),
			new Color(0, 179, 0),
			new Color(185, 240, 255),
			new Color(202, 195, 235),
			new Color(255, 172, 172),
			new Color(248, 255, 0),
			new Color(83, 238, 150),
			new Color(0, 211, 210),
			new Color(196, 255, 217),
			new Color(161, 175, 0),
			new Color(153, 234, 255),
			new Color(189, 181, 231),
			new Color(255, 139, 141),
			new Color(251, 255, 0),
			new Color(0, 229, 112),
			new Color(0, 192, 190),
			new Color(196, 254, 231),
			new Color(45, 0, 183),
			new Color(127, 228, 255),
			new Color(173, 159, 228),
			new Color(255, 83, 88),
			new Color(242, 250, 0),
			new Color(0, 218, 70),
			new Color(0, 172, 170),
			new Color(196, 253, 239),
			new Color(212, 0, 179),
			new Color(0, 219, 254),
			new Color(156, 142, 216),
			new Color(255, 0, 0),
			new Color(235, 233, 0),
			new Color(0, 213, 0),
			new Color(0, 149, 148),
			new Color(185, 253, 246),
			new Color(0, 172, 170),
			new Color(0, 198, 251),
			new Color(143, 126, 211),
			new Color(255, 0, 0),
			new Color(231, 212, 0),
			new Color(0, 200, 0),
			new Color(0, 124, 123),
			new Color(192, 246, 253),
			new Color(217, 217, 217),
			new Color(0, 185, 234),
			new Color(129, 105, 198),
			new Color(255, 0, 0),
			new Color(233, 192, 0),
			new Color(0, 186, 0),
			new Color(0, 96, 93),
			new Color(205, 233, 248),
			new Color(202, 237, 214),
			new Color(0, 173, 215),
			new Color(113, 83, 194),
			new Color(255, 0, 0),
			new Color(225, 171, 0),
			new Color(0, 172, 0),
			new Color(255, 246, 216),
			new Color(210, 227, 250),
			new Color(169, 221, 255),
			new Color(0, 158, 203),
			new Color(103, 70, 187),
			new Color(255, 0, 0),
			new Color(215, 155, 0),
			new Color(0, 155, 0),
			new Color(255, 242, 201),
			new Color(215, 221, 244),
			new Color(154, 255, 218),
			new Color(0, 145, 182),
			new Color(88, 64, 164),
			new Color(233, 0, 0),
			new Color(202, 139, 0),
			new Color(0, 139, 0),
			new Color(255, 242, 193),
			new Color(255, 250, 154),
			new Color(98, 252, 213),
			new Color(0, 118, 162),
			new Color(72, 60, 138),
			new Color(210, 0, 0),
			new Color(191, 120, 0),
			new Color(0, 129, 0),
			new Color(255, 237, 178),
			new Color(253, 246, 135),
			new Color(31, 248, 207),
			new Color(0, 102, 134),
			new Color(67, 44, 120),
			new Color(183, 0, 0),
			new Color(176, 102, 0),
			new Color(0, 112, 0),
			new Color(255, 231, 169),
			new Color(255, 239, 124),
			new Color(0, 235, 210),
			new Color(0, 79, 117),
			new Color(42, 0, 89),
			new Color(153, 0, 0),
			new Color(163, 80, 0),
			new Color(0, 87, 0),
			new Color(255, 225, 162),
			new Color(255, 234, 96),
			new Color(0, 229, 212),
			new Color(0, 54, 87),
			new Color(8, 0, 66),
			new Color(117, 0, 0),
			new Color(143, 67, 0),
			new Color(0, 63, 0),
			new Color(255, 219, 145),
			new Color(255, 229, 59),
			new Color(0, 223, 214),
			new Color(0, 23, 43),
			new Color(5, 0, 26),
			new Color(74, 0, 0),
			new Color(133, 56, 0),
			new Color(0, 46, 0),
			new Color(255, 212, 134),
			new Color(255, 223, 0),
			new Color(255, 255, 255),
			new Color(230, 238, 255),
			new Color(255, 229, 255),
			new Color(255, 235, 202),
			new Color(225, 255, 229),
			new Color(209, 255, 116),
			new Color(255, 206, 124),
			new Color(255, 216, 0),
			new Color(245, 245, 245),
			new Color(211, 227, 255),
			new Color(255, 202, 251),
			new Color(255, 219, 163),
			new Color(200, 250, 202),
			new Color(183, 255, 75),
			new Color(255, 194, 105),
			new Color(255, 204, 0),
			new Color(233, 233, 233),
			new Color(187, 214, 255),
			new Color(255, 182, 247),
			new Color(255, 200, 114),
			new Color(174, 240, 182),
			new Color(169, 245, 0),
			new Color(255, 171, 100),
			new Color(255, 189, 0),
			new Color(223, 223, 223),
			new Color(162, 201, 255),
			new Color(254, 158, 237),
			new Color(255, 180, 0),
			new Color(142, 230, 160),
			new Color(147, 241, 0),
			new Color(255, 165, 89),
			new Color(255, 181, 0),
			new Color(211, 211, 211),
			new Color(132, 188, 255),
			new Color(243, 143, 226),
			new Color(255, 156, 0),
			new Color(113, 226, 131),
			new Color(107, 230, 0),
			new Color(251, 148, 83),
			new Color(255, 166, 0),
			new Color(193, 193, 193),
			new Color(116, 171, 255),
			new Color(243, 115, 215),
			new Color(255, 127, 0),
			new Color(54, 215, 115),
			new Color(83, 218, 0),
			new Color(239, 133, 76),
			new Color(255, 147, 0),
			new Color(180, 180, 180),
			new Color(72, 157, 255),
			new Color(232, 91, 210),
			new Color(255, 129, 0),
			new Color(0, 204, 92),
			new Color(49, 205, 0),
			new Color(227, 114, 65),
			new Color(255, 255, 249),
			new Color(167, 167, 167),
			new Color(55, 137, 255),
			new Color(220, 66, 197),
			new Color(255, 120, 0),
			new Color(0, 191, 68),
			new Color(0, 199, 0),
			new Color(213, 105, 52),
			new Color(193, 193, 193),
			new Color(152, 152, 152),
			new Color(0, 133, 239),
			new Color(215, 1, 184),
			new Color(255, 112, 0),
			new Color(0, 178, 31),
			new Color(0, 255, 0),
			new Color(209, 81, 42),
			new Color(167, 167, 167),
			new Color(137, 137, 137),
			new Color(1, 117, 220),
			new Color(199, 0, 171),
			new Color(238, 104, 0),
			new Color(0, 163, 0),
			new Color(0, 244, 0),
			new Color(192, 72, 18),
			new Color(255, 0, 0),
			new Color(120, 120, 120),
			new Color(0, 112, 192),
			new Color(184, 0, 163),
			new Color(225, 79, 0),
			new Color(0, 147, 0),
			new Color(0, 233, 0),
			new Color(177, 60, 0),
			new Color(0, 255, 0),
			new Color(103, 103, 103),
			new Color(0, 96, 170),
			new Color(175, 0, 147),
			new Color(205, 54, 0),
			new Color(0, 138, 0),
			new Color(0, 219, 0),
			new Color(160, 15, 0),
			new Color(255, 255, 0),
			new Color(81, 81, 81),
			new Color(0, 71, 147),
			new Color(156, 0, 129),
			new Color(190, 0, 0),
			new Color(0, 120, 0),
			new Color(0, 207, 0),
			new Color(141, 0, 0),
			new Color(0, 0, 255),
			new Color(43, 43, 43),
			new Color(0, 65, 120),
			new Color(131, 0, 120),
			new Color(162, 0, 0),
			new Color(0, 101, 0),
			new Color(0, 193, 0),
			new Color(130, 0, 0),
			new Color(255, 0, 255),
			new Color(2, 2, 2),
			new Color(0, 34, 89),
			new Color(119, 0, 99),
			new Color(141, 0, 0),
			new Color(0, 78, 0),
			new Color(0, 180, 0),
			new Color(104, 0, 0),
			new Color(0, 255, 255),
			new Color(0, 0, 6),
			new Color(5, 0, 48),
			new Color(92, 0, 74),
			new Color(117, 0, 0),
			new Color(0, 46, 0),
			new Color(0, 163, 0),
			new Color(74, 0, 0),
			new Color(255, 255, 255)
		};
	}
	
	public static ColorPalette getDefaultColorPalette() {
		return new ColorPalette(getDefaultColors());
	}
}
