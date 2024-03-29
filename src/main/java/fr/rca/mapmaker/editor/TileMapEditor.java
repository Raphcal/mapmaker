package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.editor.tool.AbstractHitboxTool;
import fr.rca.mapmaker.editor.tool.ApplyFunctionTool;
import fr.rca.mapmaker.editor.tool.BucketFillTool;
import fr.rca.mapmaker.editor.tool.CircleStrokeTool;
import fr.rca.mapmaker.editor.tool.CoatTool;
import fr.rca.mapmaker.editor.tool.ColorPickerTool;
import fr.rca.mapmaker.editor.tool.DitherRectangleTool;
import fr.rca.mapmaker.editor.tool.EllipseFillTool;
import fr.rca.mapmaker.editor.tool.EllipseStrokeTool;
import fr.rca.mapmaker.editor.tool.FillDiagonalLines;
import fr.rca.mapmaker.editor.tool.HitboxTool;
import fr.rca.mapmaker.editor.tool.LineTool;
import fr.rca.mapmaker.editor.tool.MagicWandSelectionTool;
import fr.rca.mapmaker.editor.tool.PasteSelectionTool;
import fr.rca.mapmaker.editor.tool.PenTool;
import fr.rca.mapmaker.editor.tool.RectangleFillTool;
import fr.rca.mapmaker.editor.tool.RectangleStrokeTool;
import fr.rca.mapmaker.editor.tool.ReplaceColorTool;
import fr.rca.mapmaker.editor.tool.SecondaryHitboxTool;
import fr.rca.mapmaker.editor.tool.SelectionTool;
import fr.rca.mapmaker.editor.tool.Tool;
import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.FunctionLayerPlugin;
import fr.rca.mapmaker.model.map.HitboxLayerPlugin;
import fr.rca.mapmaker.model.map.LayerPlugin;
import fr.rca.mapmaker.model.map.LayerPlugins;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.SecondaryHitboxLayerPlugin;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.ui.Function;
import fr.rca.mapmaker.util.CleanEdge;
import java.awt.Color;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import javax.imageio.ImageIO;
import javax.swing.JFileChooser;
import javax.swing.JOptionPane;
import javax.swing.JToggleButton;
import javax.swing.filechooser.FileNameExtensionFilter;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileMapEditor extends javax.swing.JDialog {

	private static final int PALETTE_WIDTH = 8;

	private static final JFileChooser importFileChooser = new JFileChooser();
	private static TileLayer clipboardData;
	private static LayerPlugin pluginClipboardData;

	private final PasteSelectionTool pasteSelectionTool;

	private final List<ActionListener> listeners;
	private final List<DataLayer> layers;
	private TileLayer[] tiles;
	private int layerIndex;

	private DataLayer editedLayer;

	private HitboxTool hitboxTool;
	private SecondaryHitboxTool secondaryHitboxTool;
	private HitboxLayerPlugin currentPlugin;

	/**
	 * Créé un nouvel éditeur de dessin.
	 *
	 * @param parent Fenêtre parente.
	 */
	public TileMapEditor(java.awt.Frame parent) {
		super(parent, true);
		initComponents();

		treeToggleButton.setVisible(false);
		nextStepTreeButton.setVisible(false);

		listeners = new ArrayList<ActionListener>();
		layers = new ArrayList<DataLayer>();

		paletteGrid.setTileMap(colorPaletteMap);

		pasteSelectionTool = new PasteSelectionTool(drawGrid);

		hitboxTool.setOtherHitboxes(Collections.singleton(secondaryHitboxTool.getHitboxLayer()));
		secondaryHitboxTool.setOtherHitboxes(Collections.singleton(hitboxTool.getHitboxLayer()));
	}

	public void setLayerAndPalette(DataLayer layer, ColorPalette palette) {
		if (layer.getWidth() > 200 || layer.getHeight() > 200) {
			previewGrid.setZoom(200.0 / Math.max(layer.getWidth(), layer.getHeight()));
		} else {
			previewGrid.setZoom(1.0);
		}

		editedLayer = layer;
		drawLayer.restoreData(layer);
		memento.clear();
		layers.clear();

		setPalette(palette);
		widthTextField.setText(Integer.toString(layer.getWidth()));
		heightTextField.setText(Integer.toString(layer.getHeight()));
		nameTextField.setText(layer.getName());
		nameTextField.setVisible(false);

		previousLayerButton.setVisible(false);
		nextLayerButton.setVisible(false);
		hitboxToggleButton.setVisible(isHitboxAvailable());
		attackHitboxToggleButton.setVisible(isHitboxAvailable());

		final FunctionLayerPlugin plugin = drawLayer.getPlugin(FunctionLayerPlugin.class);
		if (plugin != null) {
			final String function = plugin.getFunction();
			if (function != null) {
				drawMap.add(Function.asTileLayer(function, drawLayer.getWidth(), drawLayer.getHeight()));
			}
		}

		pack();
	}

	public void setLayers(List<TileLayer> layers, int index, ColorPalette palette) {
		this.layers.clear();
		this.layers.addAll(layers);
		this.editedLayer = null;

		tiles = new TileLayer[layers.size()];

		setPalette(palette);
		setLayerIndex(index);

		previousLayerButton.setVisible(true);
		nextLayerButton.setVisible(true);

		pack();
	}

	public void setPalette(ColorPalette palette) {
		drawMap.setPalette(palette);
		previewMap.setPalette(palette);
		colorPaletteMap.setPalette(palette);

		alphaPaletteGrid.setVisible(palette instanceof AlphaColorPalette);
	}

	public ColorPalette getPalette() {
		return (ColorPalette)colorPaletteMap.getPalette();
	}

	public void setLayerIndex(int layerIndex) {
		copyDrawLayerDataToCurrentTile();

		this.layerIndex = layerIndex;

		if (tiles[layerIndex] == null) {
			final DataLayer source = layers.get(layerIndex);
			tiles[layerIndex] = new TileLayer(source);
		}

		final DataLayer layer = tiles[layerIndex];

		if (layer.getWidth() > 200 || layer.getHeight() > 200) {
			previewGrid.setZoom(200.0 / Math.max(layer.getWidth(), layer.getHeight()));
		} else {
			previewGrid.setZoom(1.0);
		}

		drawLayer.restoreData(layer);
		memento.clear();

		widthTextField.setText(Integer.toString(layer.getWidth()));
		heightTextField.setText(Integer.toString(layer.getHeight()));
		nameTextField.setText(layer.getName());
		nameTextField.setVisible(true);

		hitboxToggleButton.setVisible(isHitboxAvailable());
		attackHitboxToggleButton.setVisible(isHitboxAvailable());

		firePropertyChange("previousAvailable", null, isPreviousAvailable());
		firePropertyChange("nextAvailable", null, isNextAvailable());
	}

	public boolean isList() {
		return editedLayer == null;
	}

	public boolean isPreviousAvailable() {
		return layers != null && layerIndex > 0;
	}

	public boolean isNextAvailable() {
		return layers != null && layerIndex < layers.size() - 1;
	}

	public boolean isClipboardFull() {
		if (!isEditingPlugin()) {
			return clipboardData != null;
		} else {
			return pluginClipboardData != null;
		}
	}

	public boolean isHitboxAvailable() {
		return drawLayer.getPlugin(HitboxLayerPlugin.class) != null;
	}

	public boolean isEditingPlugin() {
		return hitboxToggleButton.isSelected() || attackHitboxToggleButton.isSelected();
	}

	public void addActionListener(ActionListener listener) {
		listeners.add(listener);
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        toolButtonGroup = new javax.swing.ButtonGroup();
        drawLayer = new TileLayer(32, 32);
        drawMap = new fr.rca.mapmaker.model.map.TileMap();
        drawMap.add(drawLayer);
        colorPalette = new fr.rca.mapmaker.model.palette.ColorPalette();
        alphaPalette = AlphaColorPalette.getAlphaPalette();
        colorPaletteMap = new PaletteMap(colorPalette, PALETTE_WIDTH);
        alphaPaletteMap = new PaletteMap(alphaPalette, PALETTE_WIDTH);
        previewMap = new fr.rca.mapmaker.model.map.TileMap();
        previewMap.add(drawLayer);
        selectionStyle = new fr.rca.mapmaker.model.selection.SmallSelectionStyle();
        memento = new fr.rca.mapmaker.editor.undo.LayerMemento();
        treeTool = new fr.rca.mapmaker.editor.tool.TreeTool();
        gridScrollPane = new javax.swing.JScrollPane();
        centerPanel = new javax.swing.JPanel(new fr.rca.mapmaker.ui.LayerLayout(fr.rca.mapmaker.ui.LayerLayout.Disposition.CENTER));
        drawGrid = new fr.rca.mapmaker.ui.Grid();
        paletteGrid = new fr.rca.mapmaker.ui.Grid();
        paletteGrid.setTileMap(colorPaletteMap);
        alphaPaletteGrid = new fr.rca.mapmaker.ui.Grid();
        alphaPaletteGrid.setTileMap(alphaPaletteMap);
        previewGrid = new fr.rca.mapmaker.ui.Grid();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        selectionToggleButton = new javax.swing.JToggleButton();
        magicWandToggleButton = new javax.swing.JToggleButton();
        penToggleButton = new javax.swing.JToggleButton();
        bucketFillToggleButton = new javax.swing.JToggleButton();
        lineToggleButton = new javax.swing.JToggleButton();
        circleToggleButton = new javax.swing.JToggleButton();
        rectangleToggleButton = new javax.swing.JToggleButton();
        rectangleFillToggleButton = new javax.swing.JToggleButton();
        ellipseToggleButton = new javax.swing.JToggleButton();
        ellipseFillToggleButton = new javax.swing.JToggleButton();
        colorPickerToggleButton = new javax.swing.JToggleButton();
        horizontalMirrorButton = new javax.swing.JButton();
        verticalMirrorButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        pasteButton = new javax.swing.JButton();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        zoomTextField = new javax.swing.JTextField();
        zoomPercentLabel = new javax.swing.JLabel();
        previousLayerButton = new javax.swing.JButton();
        nextLayerButton = new javax.swing.JButton();
        rotateButton = new javax.swing.JButton();
        ditherToggleButton = new javax.swing.JToggleButton();
        replaceColorToggleButton = new javax.swing.JToggleButton();
        treeToggleButton = new javax.swing.JToggleButton();
        nextStepTreeButton = new javax.swing.JButton();
        widthTextField = new javax.swing.JTextField();
        heightTextField = new javax.swing.JTextField();
        hitboxToggleButton = new javax.swing.JToggleButton();
        applyFunctionButton = new javax.swing.JButton();
        coatToggleButton = new javax.swing.JToggleButton();
        resizeButton = new javax.swing.JButton();
        attackHitboxToggleButton = new javax.swing.JToggleButton();
        nameTextField = new javax.swing.JTextField();
        diagonalFillToggleButton = new javax.swing.JToggleButton();
        importButton = new javax.swing.JButton();

        drawMap.setBackgroundColor(new java.awt.Color(0, 153, 153));
        drawMap.setHeight(32);
        drawMap.setPalette(colorPalette);
        drawMap.setWidth(32);

        colorPaletteMap.setPalette(colorPalette);

        alphaPaletteMap.setPalette(alphaPalette);

        previewMap.setHeight(32);
        previewMap.setPalette(colorPalette);
        previewMap.setWidth(32);

        memento.setLayers(Collections.singletonList(drawLayer));
        treeTool.setGrid(drawGrid);

        setTitle("Éditeur");

        centerPanel.setBackground(new java.awt.Color(128, 128, 128));

        drawGrid.setTileMap(drawMap);
        drawGrid.setZoom(8.0);
        drawGrid.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                drawGridComponentResized(evt);
            }
        });
        centerPanel.add(drawGrid);

        gridScrollPane.setViewportView(centerPanel);

        paletteGrid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        paletteGrid.setCustomTileSize(new java.lang.Integer(8));
        paletteGrid.setSelectionStyle(selectionStyle);
        paletteGrid.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                paletteGridMouseDragged(evt);
            }
        });
        paletteGrid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                paletteGridMouseClicked(evt);
            }
        });

        alphaPaletteGrid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        alphaPaletteGrid.setCustomTileSize(new java.lang.Integer(8));
        alphaPaletteGrid.setSelectionStyle(selectionStyle);
        alphaPaletteGrid.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                alphaPaletteGridMouseDragged(evt);
            }
        });
        alphaPaletteGrid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                alphaPaletteGridMouseClicked(evt);
            }
        });

        previewGrid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        previewGrid.setTileMap(previewMap);

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText("Annuler");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        toolButtonGroup.add(selectionToggleButton);
        selectionToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_selection.png"))); // NOI18N
        selectionToggleButton.setToolTipText("Sélection");
        selectionToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(selectionToggleButton, new SelectionTool(drawGrid));

        toolButtonGroup.add(magicWandToggleButton);
        magicWandToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_magic_wand.png"))); // NOI18N
        magicWandToggleButton.setToolTipText("Baguette magique");
        magicWandToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(magicWandToggleButton, new MagicWandSelectionTool(drawGrid));

        toolButtonGroup.add(penToggleButton);
        penToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_pen.png"))); // NOI18N
        penToggleButton.setToolTipText("Stylo");
        penToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        final PenTool penTool = new PenTool(drawGrid, memento);
        penTool.setPaletteMaps(alphaPaletteMap, colorPaletteMap);
        wireTool(penToggleButton, penTool);
        penToggleButton.setSelected(true);

        toolButtonGroup.add(bucketFillToggleButton);
        bucketFillToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_bucket_fill.png"))); // NOI18N
        bucketFillToggleButton.setToolTipText("Pot de peinture");
        bucketFillToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(bucketFillToggleButton, new BucketFillTool(drawGrid));

        toolButtonGroup.add(lineToggleButton);
        lineToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_line.png"))); // NOI18N
        lineToggleButton.setToolTipText("Trait");
        lineToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(lineToggleButton, new LineTool(drawGrid, drawLayer, drawGrid.getOverlay()));

        toolButtonGroup.add(circleToggleButton);
        circleToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_circle.png"))); // NOI18N
        circleToggleButton.setToolTipText("Cercle");
        circleToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(circleToggleButton, new CircleStrokeTool(drawGrid, drawLayer, drawGrid.getOverlay()));

        toolButtonGroup.add(rectangleToggleButton);
        rectangleToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_rectangle.png"))); // NOI18N
        rectangleToggleButton.setToolTipText("Rectangle");
        rectangleToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(rectangleToggleButton, new RectangleStrokeTool(drawGrid));

        toolButtonGroup.add(rectangleFillToggleButton);
        rectangleFillToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_rectangle_fill.png"))); // NOI18N
        rectangleFillToggleButton.setToolTipText("Rectangle plein");
        rectangleFillToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(rectangleFillToggleButton, new RectangleFillTool(drawGrid));

        toolButtonGroup.add(ellipseToggleButton);
        ellipseToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_ellipse.png"))); // NOI18N
        ellipseToggleButton.setToolTipText("Ellipse");
        ellipseToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(ellipseToggleButton, new EllipseStrokeTool(drawGrid));

        toolButtonGroup.add(ellipseFillToggleButton);
        ellipseFillToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_ellipse_fill.png"))); // NOI18N
        ellipseFillToggleButton.setToolTipText("Ellipse pleine");
        ellipseFillToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(ellipseFillToggleButton, new EllipseFillTool(drawGrid));

        toolButtonGroup.add(colorPickerToggleButton);
        colorPickerToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_color_picker.png"))); // NOI18N
        colorPickerToggleButton.setToolTipText("Pipette");
        colorPickerToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(colorPickerToggleButton, new ColorPickerTool(alphaPaletteMap, colorPaletteMap, drawGrid));

        horizontalMirrorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_horizontal_mirror.png"))); // NOI18N
        horizontalMirrorButton.setToolTipText("Inverser horizontalement");
        horizontalMirrorButton.setPreferredSize(new java.awt.Dimension(32, 32));
        horizontalMirrorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                horizontalMirrorButtonActionPerformed(evt);
            }
        });

        verticalMirrorButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_vertical_mirror.png"))); // NOI18N
        verticalMirrorButton.setToolTipText("Inverser verticalement");
        verticalMirrorButton.setPreferredSize(new java.awt.Dimension(32, 32));
        verticalMirrorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                verticalMirrorButtonActionPerformed(evt);
            }
        });

        copyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/copy.png"))); // NOI18N
        copyButton.setToolTipText("Copier");
        copyButton.setPreferredSize(new java.awt.Dimension(32, 32));
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        pasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/paste.png"))); // NOI18N
        pasteButton.setToolTipText("Coller");
        pasteButton.setPreferredSize(new java.awt.Dimension(32, 32));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${clipboardFull}"), pasteButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        pasteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteButtonActionPerformed(evt);
            }
        });

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_undo.png"))); // NOI18N
        undoButton.setToolTipText("Annuler");
        undoButton.setPreferredSize(new java.awt.Dimension(32, 32));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, memento, org.jdesktop.beansbinding.ELProperty.create("${undoable}"), undoButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_redo.png"))); // NOI18N
        redoButton.setToolTipText("Refaire");
        redoButton.setPreferredSize(new java.awt.Dimension(32, 32));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, memento, org.jdesktop.beansbinding.ELProperty.create("${redoable}"), redoButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });

        zoomTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        zoomTextField.setPreferredSize(new java.awt.Dimension(36, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, drawGrid, org.jdesktop.beansbinding.ELProperty.create("${zoomAsInteger}"), zoomTextField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "grid[${zoomAsInteger}]");
        bindingGroup.addBinding(binding);

        zoomPercentLabel.setText("%");

        previousLayerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/arrow_left.png"))); // NOI18N
        previousLayerButton.setPreferredSize(new java.awt.Dimension(32, 32));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${previousAvailable}"), previousLayerButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        previousLayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                previousLayerButtonActionPerformed(evt);
            }
        });

        nextLayerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/arrow_right.png"))); // NOI18N
        nextLayerButton.setPreferredSize(new java.awt.Dimension(32, 32));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${nextAvailable}"), nextLayerButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        nextLayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextLayerButtonActionPerformed(evt);
            }
        });

        rotateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_rotate.png"))); // NOI18N
        rotateButton.setToolTipText("Pivoter");
        rotateButton.setPreferredSize(new java.awt.Dimension(32, 32));
        rotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                rotateButtonActionPerformed(evt);
            }
        });

        toolButtonGroup.add(ditherToggleButton);
        ditherToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_dither.png"))); // NOI18N
        ditherToggleButton.setToolTipText("Tramage");
        ditherToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(ditherToggleButton, new DitherRectangleTool(drawGrid));

        toolButtonGroup.add(replaceColorToggleButton);
        replaceColorToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_replace_color.png"))); // NOI18N
        replaceColorToggleButton.setToolTipText("Remplacement de couleur");
        replaceColorToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(replaceColorToggleButton, new ReplaceColorTool(drawGrid));

        toolButtonGroup.add(treeToggleButton);
        treeToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_tree.png"))); // NOI18N
        treeToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(treeToggleButton, treeTool);

        nextStepTreeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/arrow_right.png"))); // NOI18N
        nextStepTreeButton.setPreferredSize(new java.awt.Dimension(32, 32));
        nextStepTreeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                nextStepTreeButtonActionPerformed(evt);
            }
        });

        widthTextField.setText("32");
        widthTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeLayer(evt);
            }
        });

        heightTextField.setText("32");
        heightTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeLayer(evt);
            }
        });

        toolButtonGroup.add(hitboxToggleButton);
        hitboxToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_hitbox.png"))); // NOI18N
        hitboxToggleButton.setToolTipText("Hitbox");
        hitboxToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        hitboxTool = new HitboxTool(drawGrid);
        wireTool(hitboxToggleButton, hitboxTool);
        listenToPluginChanges(hitboxToggleButton, hitboxTool);

        applyFunctionButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_function.png"))); // NOI18N
        applyFunctionButton.setToolTipText("Appliquer une fonction");
        applyFunctionButton.setPreferredSize(new java.awt.Dimension(32, 32));
        applyFunctionButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                applyFunctionButtonActionPerformed(evt);
            }
        });

        toolButtonGroup.add(coatToggleButton);
        coatToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_coat.png"))); // NOI18N
        coatToggleButton.setToolTipText("Couvrir");
        coatToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(coatToggleButton, new CoatTool(drawGrid));

        resizeButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_resize.png"))); // NOI18N
        resizeButton.setToolTipText("Redimensionner");
        resizeButton.setPreferredSize(new java.awt.Dimension(32, 32));
        resizeButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                resizeButtonActionPerformed(evt);
            }
        });

        toolButtonGroup.add(attackHitboxToggleButton);
        attackHitboxToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_hitbox2.png"))); // NOI18N
        attackHitboxToggleButton.setToolTipText("Hitbox d'attaque");
        attackHitboxToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        secondaryHitboxTool = new SecondaryHitboxTool(drawGrid);
        wireTool(attackHitboxToggleButton, secondaryHitboxTool);
        listenToPluginChanges(attackHitboxToggleButton, secondaryHitboxTool);

        nameTextField.setToolTipText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("resources/language").getString("tilemap.editor.name"), new Object[] {})); // NOI18N

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, drawLayer, org.jdesktop.beansbinding.ELProperty.create("${name}"), nameTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        toolButtonGroup.add(diagonalFillToggleButton);
        diagonalFillToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_diagonal_fill.png"))); // NOI18N
        diagonalFillToggleButton.setToolTipText("Remplissage par lignes");
        diagonalFillToggleButton.setPreferredSize(new java.awt.Dimension(32, 32));
        wireTool(diagonalFillToggleButton, new FillDiagonalLines(drawGrid, diagonalFillToggleButton));

        importButton.setText("Import");
        importButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addComponent(nameTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 67, Short.MAX_VALUE)
                    .addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 66, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 65, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(zoomTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(zoomPercentLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectionToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(penToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(lineToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(circleToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bucketFillToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(magicWandToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rectangleToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(rectangleFillToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ellipseToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(ellipseFillToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(colorPickerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(coatToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rotateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(diagonalFillToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(horizontalMirrorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(verticalMirrorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(undoButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(redoButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pasteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ditherToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(replaceColorToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(hitboxToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(attackHitboxToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(applyFunctionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(resizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(treeToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(nextStepTreeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(previousLayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(nextLayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addComponent(importButton, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 476, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(alphaPaletteGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(paletteGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(previewGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton, javax.swing.GroupLayout.PREFERRED_SIZE, 69, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton))
                .addGap(8, 8, 8))
        );

        layout.linkSize(javax.swing.SwingConstants.HORIZONTAL, new java.awt.Component[] {cancelButton, okButton});

        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gridScrollPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(paletteGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(alphaPaletteGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(previewGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton)
                        .addGap(0, 0, 0)
                        .addComponent(cancelButton)
                        .addGap(8, 8, 8))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(magicWandToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(selectionToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(penToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(bucketFillToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lineToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(circleToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rectangleToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(rectangleFillToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ellipseFillToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(ellipseToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ditherToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(replaceColorToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(colorPickerToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(coatToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(rotateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addGap(0, 0, 0)
                                .addComponent(diagonalFillToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(hitboxToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(attackHitboxToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(applyFunctionButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(resizeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(horizontalMirrorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(verticalMirrorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pasteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(undoButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(redoButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(zoomTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(zoomPercentLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(previousLayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nextLayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addComponent(treeToggleButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nextStepTreeButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(importButton)
                        .addGap(0, 0, Short.MAX_VALUE))))
        );

        bindingGroup.bind();
    }// </editor-fold>//GEN-END:initComponents

    private void paletteGridMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paletteGridMouseClicked
		selectColor(evt);
    }//GEN-LAST:event_paletteGridMouseClicked

    private void paletteGridMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paletteGridMouseDragged
		selectColor(evt);
    }//GEN-LAST:event_paletteGridMouseDragged

    private void alphaPaletteGridMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_alphaPaletteGridMouseClicked
		selectAlpha(evt);
    }//GEN-LAST:event_alphaPaletteGridMouseClicked

    private void alphaPaletteGridMouseDragged(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_alphaPaletteGridMouseDragged
		selectAlpha(evt);
    }//GEN-LAST:event_alphaPaletteGridMouseDragged

    private void drawGridComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_drawGridComponentResized
		gridScrollPane.revalidate();
    }//GEN-LAST:event_drawGridComponentResized

    private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
		memento.redo();
    }//GEN-LAST:event_redoButtonActionPerformed

    private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
		memento.undo();
    }//GEN-LAST:event_undoButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
		if (editedLayer != null) {
			editedLayer.restoreData(drawLayer);
		}
		if (!layers.isEmpty()) {
			copyDrawLayerDataToCurrentTile();

			for (int index = 0; index < layers.size(); index++) {
				if (tiles[index] != null) {
					final TileLayer layer = tiles[index];
					layers.get(index).restoreData(layer);
				}
			}
		}
		setVisible(false);
		for (final ActionListener listener : listeners) {
			listener.actionPerformed(evt);
		}
    }//GEN-LAST:event_okButtonActionPerformed

    private void previousLayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousLayerButtonActionPerformed
		setLayerIndex(layerIndex - 1);
    }//GEN-LAST:event_previousLayerButtonActionPerformed

    private void nextLayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextLayerButtonActionPerformed
		setLayerIndex(layerIndex + 1);
    }//GEN-LAST:event_nextLayerButtonActionPerformed

	public static void copy(TileLayer source) {
		clipboardData = new TileLayer(source);
	}

	public static void copy(LayerPlugin source) {
		pluginClipboardData = LayerPlugins.copyOf(source);
	}

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
		final boolean oldClipboardFull = isClipboardFull();

		if (!isEditingPlugin()) {
			final TileLayer source;
			if (drawGrid.getOverlay().isEmpty()) {
				source = drawLayer;
			} else {
				source = drawGrid.getOverlay();
			}
			copy(source);
		} else {
			// Copie la hitbox
			copy(drawLayer.getPlugin(currentPlugin.getClass()));
		}

		firePropertyChange("clipboardFull", oldClipboardFull, isClipboardFull());
    }//GEN-LAST:event_copyButtonActionPerformed

    private void pasteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteButtonActionPerformed
		if (!isEditingPlugin()) {
			toolButtonGroup.clearSelection();
			pasteSelectionTool.setSelection(clipboardData);

			drawGrid.addMouseListener(pasteSelectionTool);
			drawGrid.addMouseMotionListener(pasteSelectionTool);
		} else {
			// Colle la hitbox
			drawLayer.setPlugin(LayerPlugins.copyOf(pluginClipboardData));
		}
    }//GEN-LAST:event_pasteButtonActionPerformed

    private void horizontalMirrorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_horizontalMirrorButtonActionPerformed
		drawLayer.flipHorizontally();
    }//GEN-LAST:event_horizontalMirrorButtonActionPerformed

    private void verticalMirrorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verticalMirrorButtonActionPerformed
		drawLayer.flipVertically();
    }//GEN-LAST:event_verticalMirrorButtonActionPerformed

    private void rotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_rotateButtonActionPerformed
		final String value = JOptionPane.showInputDialog("Angle de la rotation (0 à 360) :");
		if (value != null) {
			try {
				final int degree = Integer.parseInt(value);

				if (drawLayer.getWidth() == drawLayer.getHeight() && degree % 90 == 0) {
					drawLayer.rotate90(degree / 90);
				} else if (drawMap.getPalette() instanceof ColorPalette) {
					CleanEdge.builder()
							.palette((ColorPalette) drawMap.getPalette())
							.rotation(Math.toRadians(degree))
							.slope(true)
							.build()
							.shade(drawLayer);
				} else {
					// Rotation de l'angle converti en radian.
					drawLayer.rotate(degree * Math.PI / 180.0);
				}

			} catch (NumberFormatException e) {
				// Ignoré.
			}
		}
    }//GEN-LAST:event_rotateButtonActionPerformed

    private void nextStepTreeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextStepTreeButtonActionPerformed
		treeTool.nextStep();
    }//GEN-LAST:event_nextStepTreeButtonActionPerformed

    private void resizeLayer(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeLayer
		final int width = Integer.parseInt(widthTextField.getText());
		final int height = Integer.parseInt(heightTextField.getText());
		drawLayer.resize(width, height);
		drawGrid.getOverlay().resize(width, height);
    }//GEN-LAST:event_resizeLayer

    private void applyFunctionButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_applyFunctionButtonActionPerformed
		ApplyFunctionTool.execute(drawLayer);
    }//GEN-LAST:event_applyFunctionButtonActionPerformed

    private void resizeButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_resizeButtonActionPerformed
		final String newSize = JOptionPane.showInputDialog(this, "Redimensionner à quelle taille (format : largeur x hauteur [x1 pour cleanEdge, x2 pour centrer]) ?");
		if (newSize == null) {
			return;
		}
		final int[] parts = Arrays.stream(newSize.split("x"))
				.mapToInt(TileMapEditor::toInt)
				.toArray();
		if (parts.length < 2 || parts.length > 3) {
			JOptionPane.showMessageDialog(this, "Mauvais format, attendu 'largeur x hauteur' mais reçu : " + newSize);
			return;
		}
		if (parts.length == 3 && parts[2] == 2) {
			// Mode 2 = rédimensionnement sans étirer.
			final int oldWidth = drawLayer.getWidth();
			final int oldHeight = drawLayer.getHeight();
			final int width = parts[0];
			final int height = parts[1];
			final int translateX = (width - oldWidth) / 2;
			final int translateY = (height - oldHeight) / 2;

			// Déplacement avant redimensionnement pour ne pas couper les données au rétrécissement.
			if (translateX <= 0 && translateY <= 0) {
				drawLayer.translate(translateX, translateY);
			} else if (translateX < 0) {
				drawLayer.translate(translateX, 0);
			} else if (translateY < 0) {
				drawLayer.translate(0, translateY);
			}

			drawGrid.getOverlay().resize(width, height);
			drawLayer.resize(width, height);

			// Déplacement après redimensionnement pour ne pas couper les données avant l'agrandissement.
			if (translateX >= 0 && translateY >= 0) {
				drawLayer.translate(translateX, translateY);
			} else if (translateX > 0) {
				drawLayer.translate(translateX, 0);
			} else if (translateY > 0) {
				drawLayer.translate(0, translateY);
			}

			HitboxLayerPlugin hitboxLayerPlugin = drawLayer.getPlugin(HitboxLayerPlugin.class);
			if (hitboxLayerPlugin != null && hitboxLayerPlugin.getHitbox() != null) {
				Rectangle hitbox = new Rectangle(hitboxLayerPlugin.getHitbox());
				hitbox.x += (width - oldWidth) / 2;
				hitbox.y += (height - oldHeight) / 2;
				drawLayer.setPlugin(new HitboxLayerPlugin(hitbox));
			}
			hitboxLayerPlugin = drawLayer.getPlugin(SecondaryHitboxLayerPlugin.class);
			if (hitboxLayerPlugin != null && hitboxLayerPlugin.getHitbox() != null) {
				Rectangle hitbox = new Rectangle(hitboxLayerPlugin.getHitbox());
				hitbox.x += (width - oldWidth) / 2;
				hitbox.y += (height - oldHeight) / 2;
				drawLayer.setPlugin(new SecondaryHitboxLayerPlugin(hitbox));
			}
		} else if (parts.length == 3 && parts[2] == 1) {
			// Mode 1 = étirement en utilisant CleanEdge.
			CleanEdge.builder()
				.palette((ColorPalette) drawMap.getPalette())
				.slope(true)
				.cleanUpSmallDetails(true)
				.scale(new CleanEdge.Point((double)parts[0] / drawLayer.getWidth(), (double)parts[1] / drawLayer.getHeight()))
				.build()
				.shade(drawLayer);
		} else {
			drawLayer.scale(parts[0], parts[1]);
		}
		drawGrid.getOverlay().resize(parts[0], parts[1]);
    }//GEN-LAST:event_resizeButtonActionPerformed

    private void importButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importButtonActionPerformed
		importFileChooser.setMultiSelectionEnabled(false);
		importFileChooser.setFileFilter(new FileNameExtensionFilter("Image BMP, PNG ou JPG", "bmp", "png", "jpg", "jpeg"));
		if (importFileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			final File file = importFileChooser.getSelectedFile();
			try {
				final BufferedImage image = ImageIO.read(file);
				final int width = image.getWidth();
				final int height = image.getHeight();

				final HashMap<Color, Integer> colorMap = new HashMap<>();

				final int size = width * height;
				int[] tiles = new int[size];
				for (int index = 0; index < size; index++) {
					final int x = index % width;
					final int y = index / width;
					final Color color = new Color(image.getRGB(x, y));
					final Integer tile = colorMap.computeIfAbsent(color, this::findClosestColorIndex);
					tiles[index] = tile != null ? tile : -1;
				}
				drawLayer.restoreData(tiles, width, height);
				drawGrid.getOverlay().resize(width, height);
			} catch (IOException e) {
				Exceptions.showStackTrace(e, this);
			}
		}
    }//GEN-LAST:event_importButtonActionPerformed

	public static int toInt(String value) {
		try {
			return Integer.valueOf(value);
		} catch (Exception e) {
			throw new IllegalArgumentException("Mauvaise valeur reçue : " + value, e);
		}
	}

	private void selectColor(MouseEvent event) {
		final Point point = paletteGrid.getLayerLocation(event.getX(), event.getY());
		colorPaletteMap.setSelection(point);
	}

	private void selectAlpha(MouseEvent event) {
		final Point point = alphaPaletteGrid.getLayerLocation(event.getX(), event.getY());
		alphaPaletteMap.setSelection(point);

		final Palette palette = colorPaletteMap.getPalette();
		if (palette instanceof AlphaColorPalette) {
			((AlphaColorPalette) palette).setSelectedAlpha(alphaPalette.getSelectedTile());
		}
	}

	private void wireTool(JToggleButton button, final Tool tool) {
		button.addItemListener(new ItemListener() {

			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					drawGrid.addMouseListener(tool);
					drawGrid.addMouseMotionListener(tool);

					tool.setup();
					firePropertyChange("clipboardFull", null, isClipboardFull());

				} else {
					drawGrid.removeMouseListener(tool);
					drawGrid.removeMouseMotionListener(tool);

					tool.reset();
				}
			}
		});
	}

	private <T extends HitboxLayerPlugin> void listenToPluginChanges(JToggleButton button, AbstractHitboxTool<T> tool) {
		button.addItemListener(new ItemListener() {
			@Override
			public void itemStateChanged(ItemEvent e) {
				if (e.getStateChange() == ItemEvent.SELECTED) {
					currentPlugin = tool.getHitboxPlugin();
				}
			}
		});
	}

	private void copyDrawLayerDataToCurrentTile() {
		if (layerIndex >= 0 && layerIndex < tiles.length && tiles[layerIndex] != null) {
			tiles[layerIndex].restoreData(drawLayer);
		}
	}

	private int findClosestColorIndex(Color targetColor) {
		final ColorPalette palette = getPalette();
        int closest = 0;
        double minDistance = getColorDistance(targetColor, palette.getColor(closest));

        for (int index = 1; index < palette.size(); index++) {
			Color color = palette.getColor(index);
			if (color.equals(targetColor)) {
				return index;
			}
            double distance = getColorDistance(targetColor, color);
            if (distance < minDistance) {
                minDistance = distance;
                closest = index;
            }
        }

        return closest;
    }

    private double getColorDistance(Color c1, Color c2) {
		float[] hsb1 = Color.RGBtoHSB(c1.getRed(), c1.getGreen(), c1.getBlue(), null);
		float[] hsb2 = Color.RGBtoHSB(c2.getRed(), c2.getGreen(), c2.getBlue(), null);

		// Convertir en espace HSL
		float hueDistance = Math.abs(hsb1[0] - hsb2[0]);
		float saturationDistance = Math.abs(hsb1[1] - hsb2[1]);
		float brightnessDistance = Math.abs(hsb1[2] - hsb2[2]);

		// Prendre en compte la circularité de la teinte
		if (hueDistance > 0.5f) {
			hueDistance = 1.0f - hueDistance;
		}

		// Calculer la distance totale dans l'espace HSL
		double distance = Math.sqrt(hueDistance * hueDistance +
									saturationDistance * saturationDistance +
									brightnessDistance * brightnessDistance);

		return distance;
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.rca.mapmaker.model.palette.ColorPalette alphaPalette;
    private fr.rca.mapmaker.ui.Grid alphaPaletteGrid;
    private fr.rca.mapmaker.model.map.PaletteMap alphaPaletteMap;
    private javax.swing.JButton applyFunctionButton;
    private javax.swing.JToggleButton attackHitboxToggleButton;
    private javax.swing.JToggleButton bucketFillToggleButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JToggleButton circleToggleButton;
    private javax.swing.JToggleButton coatToggleButton;
    private fr.rca.mapmaker.model.palette.ColorPalette colorPalette;
    private fr.rca.mapmaker.model.map.PaletteMap colorPaletteMap;
    private javax.swing.JToggleButton colorPickerToggleButton;
    private javax.swing.JButton copyButton;
    private javax.swing.JToggleButton diagonalFillToggleButton;
    private javax.swing.JToggleButton ditherToggleButton;
    private fr.rca.mapmaker.ui.Grid drawGrid;
    private fr.rca.mapmaker.model.map.TileLayer drawLayer;
    private fr.rca.mapmaker.model.map.TileMap drawMap;
    private javax.swing.JToggleButton ellipseFillToggleButton;
    private javax.swing.JToggleButton ellipseToggleButton;
    private javax.swing.JScrollPane gridScrollPane;
    private javax.swing.JTextField heightTextField;
    private javax.swing.JToggleButton hitboxToggleButton;
    private javax.swing.JButton horizontalMirrorButton;
    private javax.swing.JButton importButton;
    private javax.swing.JToggleButton lineToggleButton;
    private javax.swing.JToggleButton magicWandToggleButton;
    private fr.rca.mapmaker.editor.undo.LayerMemento memento;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JButton nextLayerButton;
    private javax.swing.JButton nextStepTreeButton;
    private javax.swing.JButton okButton;
    private fr.rca.mapmaker.ui.Grid paletteGrid;
    private javax.swing.JButton pasteButton;
    private javax.swing.JToggleButton penToggleButton;
    private fr.rca.mapmaker.ui.Grid previewGrid;
    private fr.rca.mapmaker.model.map.TileMap previewMap;
    private javax.swing.JButton previousLayerButton;
    private javax.swing.JToggleButton rectangleFillToggleButton;
    private javax.swing.JToggleButton rectangleToggleButton;
    private javax.swing.JButton redoButton;
    private javax.swing.JToggleButton replaceColorToggleButton;
    private javax.swing.JButton resizeButton;
    private javax.swing.JButton rotateButton;
    private fr.rca.mapmaker.model.selection.SmallSelectionStyle selectionStyle;
    private javax.swing.JToggleButton selectionToggleButton;
    private javax.swing.ButtonGroup toolButtonGroup;
    private javax.swing.JToggleButton treeToggleButton;
    private fr.rca.mapmaker.editor.tool.TreeTool treeTool;
    private javax.swing.JButton undoButton;
    private javax.swing.JButton verticalMirrorButton;
    private javax.swing.JTextField widthTextField;
    private javax.swing.JLabel zoomPercentLabel;
    private javax.swing.JTextField zoomTextField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
