package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.editor.tool.BucketFillTool;
import fr.rca.mapmaker.editor.tool.CircleStrokeTool;
import fr.rca.mapmaker.editor.tool.ColorPickerTool;
import fr.rca.mapmaker.editor.tool.EllipseFillTool;
import fr.rca.mapmaker.editor.tool.EllipseStrokeTool;
import fr.rca.mapmaker.editor.tool.LineTool;
import fr.rca.mapmaker.editor.tool.MagicWandSelectionTool;
import fr.rca.mapmaker.editor.tool.MagnifierTool;
import fr.rca.mapmaker.editor.tool.PasteSelectionTool;
import fr.rca.mapmaker.editor.tool.PenTool;
import fr.rca.mapmaker.editor.tool.RectangleFillTool;
import fr.rca.mapmaker.editor.tool.RectangleStrokeTool;
import fr.rca.mapmaker.editor.tool.SelectionTool;
import fr.rca.mapmaker.editor.tool.Tool;
import fr.rca.mapmaker.editor.undo.LayerMemento;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.FormatFileFilter;
import fr.rca.mapmaker.io.Formats;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.selection.SmallSelectionStyle;
import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.ui.ImageRenderer;
import java.awt.BorderLayout;
import java.awt.Color;
import java.awt.Component;
import java.awt.Dimension;
import java.awt.FlowLayout;
import java.awt.GridBagLayout;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.Arrays;
import javax.imageio.ImageIO;
import javax.swing.Box;
import javax.swing.BoxLayout;
import javax.swing.ButtonGroup;
import javax.swing.ImageIcon;
import javax.swing.JButton;
import javax.swing.JComponent;
import javax.swing.JDialog;
import javax.swing.JFileChooser;
import javax.swing.JFrame;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileFilter;

@Deprecated
public class TileEditor {
    
	private static final int PALETTE_WIDTH = 8;
	private static final int BUTTON_WIDTH = 28;
	
	private static int[] clipboardData;
	private static Rectangle clipboardSurface;
	
	public static JDialog createEditorDialog(final TileLayer sourceLayer, final ColorPalette palette,
			JFrame owner, ActionListener callback) {
		
		// Copie de la source
		final TileLayer drawingLayer = new TileLayer(sourceLayer.getWidth(), sourceLayer.getHeight());
		drawingLayer.restoreData(sourceLayer.copyData(), new Rectangle(sourceLayer.getWidth(), sourceLayer.getHeight()));
		
		final JDialog dialog = new JDialog(owner, true);
		dialog.setTitle("Éditeur");
		dialog.setSize(720, 440);
		
		// Côté droit
		final JPanel rightPanel = new JPanel(null);
		final BoxLayout boxLayout = new BoxLayout(rightPanel, BoxLayout.Y_AXIS);
		rightPanel.setLayout(boxLayout);
		rightPanel.setPreferredSize(new Dimension(64, 0));
		
		// Palette
		final Grid paletteGrid = new Grid();
		final PaletteMap paletteMap = new PaletteMap(palette, PALETTE_WIDTH);
		paletteGrid.setTileMap(paletteMap);
		paletteGrid.setCustomTileSize(8);
		paletteGrid.setSelectionStyle(new SmallSelectionStyle());
		paletteMap.setSelection(new Point());
		
		// Sélection des couleurs
		paletteGrid.addMouseListener(new MouseAdapter() {
			@Override
			public void mouseClicked(MouseEvent e) {
				final Point point = paletteGrid.getLayerLocation(e.getX(), e.getY());
				paletteMap.setSelection(point);
			}
		});
		paletteGrid.addMouseMotionListener(new MouseAdapter() {

			@Override
			public void mouseDragged(MouseEvent e) {
				final Point point = paletteGrid.getLayerLocation(e.getX(), e.getY());
				paletteMap.setSelection(point);
			}
		});
		
		// Palette de transparence
		final Grid alphaPaletteGrid;
		if(palette instanceof AlphaColorPalette) {
			final AlphaColorPalette alphaColorPalette = (AlphaColorPalette)palette;
			alphaColorPalette.setSelectedAlpha(0);
			
			final ColorPalette alphaPalette = AlphaColorPalette.getAlphaPalette();
			alphaPaletteGrid = new Grid();
			final PaletteMap alphaPaletteMap = new PaletteMap(alphaPalette, PALETTE_WIDTH);
			alphaPaletteGrid.setTileMap(alphaPaletteMap);
			alphaPaletteGrid.setCustomTileSize(8);
			alphaPaletteGrid.setSelectionStyle(new SmallSelectionStyle());
			alphaPaletteMap.setSelection(new Point());

			// Sélection des couleurs
			alphaPaletteGrid.addMouseListener(new MouseAdapter() {
				@Override
				public void mouseClicked(MouseEvent e) {
					final Point point = alphaPaletteGrid.getLayerLocation(e.getX(), e.getY());
					alphaPaletteMap.setSelection(point);
					alphaColorPalette.setSelectedAlpha(alphaPalette.getSelectedTile());
				}
			});
			alphaPaletteGrid.addMouseMotionListener(new MouseAdapter() {

				@Override
				public void mouseDragged(MouseEvent e) {
					final Point point = alphaPaletteGrid.getLayerLocation(e.getX(), e.getY());
					alphaPaletteMap.setSelection(point);
					alphaColorPalette.setSelectedAlpha(alphaPalette.getSelectedTile());
				}
			});
		} else {
			alphaPaletteGrid = null;
		}

		// Aperçu du dessin
		final TileMap previewMap = new TileMap();
		previewMap.add(drawingLayer);
		previewMap.setPalette(palette);
		
		final Grid previewGrid = new Grid();
		previewGrid.setTileMap(previewMap);
		
		// Bouton OK
		final JButton okButton = new JButton("OK");
		okButton.setPreferredSize(new Dimension(72, 24));
		okButton.addActionListener(callback);
		okButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
				sourceLayer.restoreData(drawingLayer.copyData(), new Rectangle(sourceLayer.getWidth(), sourceLayer.getHeight()));
			}
		});
		
		// Bouton Annuler
		final JButton cancelButton = new JButton("Annu.");
		cancelButton.setPreferredSize(new Dimension(72, 24));
		cancelButton.addActionListener(callback);
		cancelButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				dialog.setVisible(false);
			}
		});
		
		rightPanel.add(paletteGrid);
		if(alphaPaletteGrid != null) {
			rightPanel.add(alphaPaletteGrid);
		}
		rightPanel.add(previewGrid);
		rightPanel.add(okButton);
		rightPanel.add(cancelButton);
		
		// Zone de dessin
		final JPanel panel = new JPanel(new GridBagLayout());
		
		final TileMap drawingMap = new TileMap();
		drawingMap.setBackgroundColor(new Color(0, 128, 128));
		drawingMap.add(drawingLayer);
		drawingMap.setPalette(palette);
		
		final Grid drawingGrid = new Grid();
		drawingGrid.setTileMap(drawingMap);
		drawingGrid.setCustomTileSize(12);
		drawingGrid.setActiveLayer(drawingLayer);
//		drawingGrid.setBackground(new Color(0, 128, 128));
		
		panel.add(drawingGrid, null);
                
		final JScrollPane scrollPane = new JScrollPane(panel);
		
		// Palette d'outils
		final JComponent toolPalette = createToolPalette(paletteMap, drawingGrid,
				drawingLayer, drawingGrid.getOverlay(), scrollPane);
		
		// Ajout des morceaux à la fenêtre
		dialog.getContentPane().add(toolPalette, BorderLayout.WEST);
		dialog.getContentPane().add(rightPanel, BorderLayout.EAST);
		dialog.getContentPane().add(scrollPane, BorderLayout.CENTER);
		
		return dialog;
	}
	
	private static Component createMargin(int width, int height) {
		return Box.createRigidArea(new Dimension(width, height));
	}
	
	/**
	 * Créé la palette d'outils.
	 * 
	 * @param drawingGrid La surface de dessin.
	 * @param drawingLayer La couche de dessin.
	 * @param previewLayer La couche d'aperçu.
	 * @return Un composant Swing contenant la palette d'outils.
	 */
	private static JComponent createToolPalette(final PaletteMap paletteMap, 
			final Grid drawingGrid,	final TileLayer drawingLayer, final TileLayer previewLayer,
			final JScrollPane scrollPane) {
		
		final FlowLayout layout = new FlowLayout(FlowLayout.LEADING, 0, 0);
		final JPanel toolPalette = new JPanel(layout);
		toolPalette.setPreferredSize(new Dimension(BUTTON_WIDTH + BUTTON_WIDTH, 100));

		final ButtonGroup group = new ButtonGroup();
		
		final JToggleButton selectionButton = createToggleButton("tool_selection.png", "Sélection", group, new SelectionTool(drawingGrid), drawingGrid);
		toolPalette.add(selectionButton);
		final JToggleButton magicWandButton = createToggleButton("tool_magic_wand.png", "Baguette magique", group, new MagicWandSelectionTool(drawingGrid), drawingGrid);
		toolPalette.add(magicWandButton);
		
		toolPalette.add(createMargin(BUTTON_WIDTH + BUTTON_WIDTH, 8));
		
		final JToggleButton penButton = createToggleButton("tool_pen.png", "Stylo", group, new PenTool(drawingGrid), drawingGrid);
		toolPalette.add(penButton);
		final JToggleButton bucketFillButton = createToggleButton("tool_bucket_fill.png", "Pot de peinture", group, new BucketFillTool(drawingGrid), drawingGrid);
		toolPalette.add(bucketFillButton);
		
		final JToggleButton lineButton = createToggleButton("tool_line.png", "Trait", group, new LineTool(drawingGrid, drawingLayer, previewLayer), drawingGrid);
		toolPalette.add(lineButton);
		final JToggleButton circleButton = createToggleButton("tool_circle.png", "Cercle", group, new CircleStrokeTool(drawingGrid, drawingLayer, previewLayer), drawingGrid);
		toolPalette.add(circleButton);
		
		final JToggleButton rectangleStrokeButton = createToggleButton("tool_rectangle.png", "Rectangle", group, new RectangleStrokeTool(drawingGrid), drawingGrid);
		toolPalette.add(rectangleStrokeButton);
		final JToggleButton rectangleFillButton = createToggleButton("tool_rectangle_fill.png", "Rectangle plein", group, new RectangleFillTool(drawingGrid), drawingGrid);
		toolPalette.add(rectangleFillButton);
		
		final JToggleButton ellipseStrokeButton = createToggleButton("tool_ellipse.png", "Ellipse", group, new EllipseStrokeTool(drawingGrid), drawingGrid);
		toolPalette.add(ellipseStrokeButton);
		final JToggleButton ellipseFillButton = createToggleButton("tool_ellipse_fill.png", "Ellipse pleine", group, new EllipseFillTool(drawingGrid), drawingGrid);
		toolPalette.add(ellipseFillButton);
		
		toolPalette.add(createMargin(BUTTON_WIDTH + BUTTON_WIDTH, 8));
		
		final JToggleButton colorPickerButton = createToggleButton("tool_color_picker.png", "Pipette", group, new ColorPickerTool(paletteMap, drawingGrid, drawingLayer), drawingGrid);
		toolPalette.add(colorPickerButton);
		final JToggleButton zoomButton = createToggleButton("tool_magnifier.png", "Loupe", group, new MagnifierTool(drawingGrid, scrollPane), drawingGrid);
		toolPalette.add(zoomButton);

		// Miroirs
		final JButton horizontalMirrorButton = createButton("tool_horizontal_mirror.png", "Inverser horizontalement");
		toolPalette.add(horizontalMirrorButton);
		final JButton verticalMirrorButton = createButton("tool_vertical_mirror.png", "Inverser verticalement");
		toolPalette.add(verticalMirrorButton);
		
		horizontalMirrorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingLayer.flipHorizontally();
			}
		});
		
		verticalMirrorButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				drawingLayer.flipVertically();
			}
		});
		
		toolPalette.add(createMargin(BUTTON_WIDTH + BUTTON_WIDTH, 8));
		
		// Copier / coller
		final JButton copyButton = createButton("copy.png", "Copier");
		toolPalette.add(copyButton);
		final JButton pasteButton = createButton("paste.png", "Coller");
		toolPalette.add(pasteButton);
		final PasteSelectionTool pasteSelectionTool = new PasteSelectionTool(drawingGrid);
		
		copyButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				clipboardData = drawingLayer.copyData();
				clipboardSurface = new Rectangle(drawingLayer.getWidth(), drawingLayer.getHeight());
			}
		});
		
		pasteButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				group.clearSelection();
				pasteSelectionTool.setSelection(clipboardData, clipboardSurface);
				
				drawingGrid.addMouseListener(pasteSelectionTool);
				drawingGrid.addMouseMotionListener(pasteSelectionTool);
			}
		});
		
		
		// Annuler / refaire
		final LayerMemento memento = new LayerMemento(drawingLayer);
		
		final JButton undoButton = createButton("tool_undo.png", "Annuler");
		undoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				memento.undo();
			}
		});
		toolPalette.add(undoButton);
		
		final JButton redoButton = createButton("tool_redo.png", "Refaire");
		redoButton.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				memento.redo();
			}
		});
		toolPalette.add(redoButton);
		
		toolPalette.add(createMargin(BUTTON_WIDTH + BUTTON_WIDTH, 8));
		
		final JButton saveButton = new JButton("Enr.");
		saveButton.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_WIDTH));
		
		final JFileChooser fileChooser = new JFileChooser();
		configureFileChooser(fileChooser);
		saveButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final int action = fileChooser.showSaveDialog(null);
				
				if(action == JFileChooser.APPROVE_OPTION) {
					final Format format = ((FormatFileFilter)fileChooser.getFileFilter()).getFormat();
					final File destination = format.normalizeFile(fileChooser.getSelectedFile());

					final ImageRenderer renderer = new ImageRenderer();
					final BufferedImage image = renderer.renderImage(drawingLayer, paletteMap.getPalette(), 1);
					try {
						ImageIO.write(image, "png", destination);

					} catch (IOException ex) {
						Exceptions.showStackTrace(ex, null);
					}
				}
			}
		});
		toolPalette.add(saveButton);
		
		final JButton clearButton = new JButton("Eff.");
		clearButton.setPreferredSize(new Dimension(BUTTON_WIDTH * 2, BUTTON_WIDTH));
		clearButton.addActionListener(new ActionListener() {
			
			@Override
			public void actionPerformed(ActionEvent e) {
				final int[] tiles = new int[drawingLayer.getWidth() * drawingLayer.getHeight()];
				Arrays.fill(tiles, -1);
				drawingLayer.restoreData(tiles, new Rectangle(drawingLayer.getWidth(), drawingLayer.getHeight()));
			}
		});
		toolPalette.add(clearButton);
		
		penButton.setSelected(true);
		
		return toolPalette;
	}
	
	private static ImageIcon createImageIcon(String name) {
		return new ImageIcon(TileEditor.class.getResource("/resources/" + name));
	}
	
	private static JToggleButton createToggleButton(final String imageName, final String toolTip,
			final ButtonGroup group, final Tool tool, final Grid drawingGrid) {
		
		final JToggleButton button = new JToggleButton(createImageIcon(imageName));
		button.setToolTipText(toolTip);
		button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_WIDTH));
		
		button.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					drawingGrid.addMouseListener(tool);
					drawingGrid.addMouseMotionListener(tool);
					
				} else {
					drawingGrid.removeMouseListener(tool);
					drawingGrid.removeMouseMotionListener(tool);
					
					tool.reset();
				}
			}
		});
			
		group.add(button);
		
		return button;
	}
	
	private static void configureFileChooser(JFileChooser fileChooser) {
		for(final FileFilter fileFilter : fileChooser.getChoosableFileFilters())
			fileChooser.removeChoosableFileFilter(fileFilter);
			
		final Format format = Formats.getFormat(".png");
		fileChooser.addChoosableFileFilter(format.getFileFilter());
		fileChooser.setFileFilter(format.getFileFilter());
	}
	
	private static JButton createButton(final String imageName, final String toolTip) {
		
		final JButton button = new JButton(createImageIcon(imageName));
		button.setToolTipText(toolTip);
		button.setPreferredSize(new Dimension(BUTTON_WIDTH, BUTTON_WIDTH));
		
		return button;
	}
}
