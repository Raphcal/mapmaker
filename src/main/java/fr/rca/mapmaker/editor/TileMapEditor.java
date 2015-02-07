package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.editor.tool.BucketFillTool;
import fr.rca.mapmaker.editor.tool.CircleStrokeTool;
import fr.rca.mapmaker.editor.tool.ColorPickerTool;
import fr.rca.mapmaker.editor.tool.EllipseFillTool;
import fr.rca.mapmaker.editor.tool.EllipseStrokeTool;
import fr.rca.mapmaker.editor.tool.LineTool;
import fr.rca.mapmaker.editor.tool.MagicWandSelectionTool;
import fr.rca.mapmaker.editor.tool.PasteSelectionTool;
import fr.rca.mapmaker.editor.tool.PenTool;
import fr.rca.mapmaker.editor.tool.RectangleFillTool;
import fr.rca.mapmaker.editor.tool.RectangleStrokeTool;
import fr.rca.mapmaker.editor.tool.SelectionTool;
import fr.rca.mapmaker.editor.tool.Tool;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.AlphaColorPalette;
import fr.rca.mapmaker.model.palette.ColorPalette;
import java.awt.Point;
import java.awt.Rectangle;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.MouseEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.JToggleButton;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileMapEditor extends javax.swing.JDialog {
	
	private static final int PALETTE_WIDTH = 8;
	
	private static int[] clipboardData;
	private static Rectangle clipboardSurface;

	private final PasteSelectionTool pasteSelectionTool;
	
	private final ArrayList<ActionListener> listeners;
	private final ArrayList<TileLayer> layers;
	private int[][] tiles;
	private int layerIndex;
	
	/**
	 * Créé un nouvel éditeur de dessin.
	 * 
	 * @param parent Fenêtre parente.
	 */
	public TileMapEditor(java.awt.Frame parent) {
		super(parent, true);
		initComponents();
		
		listeners = new ArrayList<ActionListener>();
		layers = new ArrayList<TileLayer>();
		
		paletteGrid.setTileMap(colorPaletteMap);
		
		pasteSelectionTool = new PasteSelectionTool(drawGrid);
	}

	public void setLayerAndPalette(TileLayer layer, ColorPalette palette) {
		editedLayer = layer;
		drawLayer.restoreData(layer.copyData(), layer.getWidth(), layer.getHeight());
		memento.clear();
		layers.clear();
		
		setPalette(palette);
		
		previousLayerButton.setVisible(false);
		nextLayerButton.setVisible(false);
		
		pack();
	}
	
	public void setLayers(List<TileLayer> layers, int index, ColorPalette palette) {
		this.layers.clear();
		this.layers.addAll(layers);
		this.editedLayer = null;
		
		tiles = new int[layers.size()][];
		
		setPalette(palette);
		setLayerIndex(index);
		
		previousLayerButton.setVisible(true);
		nextLayerButton.setVisible(true);
		
		pack();
	}
	
	public void setPalette(ColorPalette palette) {
		drawMap.setPalette(palette);
		colorPaletteMap.setPalette(palette);
		
		alphaPaletteGrid.setVisible(palette instanceof AlphaColorPalette);
	}

	public void setLayerIndex(int layerIndex) {
		copyDrawLayerDataToCurrentTile();
		
		this.layerIndex = layerIndex;
		
		final TileLayer layer = layers.get(layerIndex);
		
		if(tiles[layerIndex] == null) {
			tiles[layerIndex] = layer.copyData();
		}
		
		drawLayer.restoreData(tiles[layerIndex], layer.getWidth(), layer.getHeight());
		memento.clear();
		
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
		return clipboardData != null;
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
        java.awt.GridBagConstraints gridBagConstraints;
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
        editedLayer = new fr.rca.mapmaker.model.map.TileLayer();
        gridScrollPane = new javax.swing.JScrollPane();
        centerPanel = new javax.swing.JPanel();
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

        setTitle("Éditeur");

        centerPanel.setBackground(new java.awt.Color(128, 128, 128));
        centerPanel.setLayout(new java.awt.GridBagLayout());

        drawGrid.setTileMap(drawMap);
        drawGrid.setZoom(8.0);
        drawGrid.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                drawGridComponentResized(evt);
            }
        });
        gridBagConstraints = new java.awt.GridBagConstraints();
        gridBagConstraints.gridx = 0;
        gridBagConstraints.gridy = 0;
        gridBagConstraints.anchor = java.awt.GridBagConstraints.NORTHWEST;
        centerPanel.add(drawGrid, gridBagConstraints);

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
        wireTool(selectionToggleButton, new SelectionTool(drawGrid));

        toolButtonGroup.add(magicWandToggleButton);
        magicWandToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_magic_wand.png"))); // NOI18N
        magicWandToggleButton.setToolTipText("Baguette magique");
        wireTool(magicWandToggleButton, new MagicWandSelectionTool(drawGrid));

        toolButtonGroup.add(penToggleButton);
        penToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_pen.png"))); // NOI18N
        penToggleButton.setToolTipText("Stylo");
        wireTool(penToggleButton, new PenTool(drawGrid, memento));
        penToggleButton.setSelected(true);

        toolButtonGroup.add(bucketFillToggleButton);
        bucketFillToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_bucket_fill.png"))); // NOI18N
        bucketFillToggleButton.setToolTipText("Pot de peinture");
        wireTool(bucketFillToggleButton, new BucketFillTool(drawGrid));

        toolButtonGroup.add(lineToggleButton);
        lineToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_line.png"))); // NOI18N
        lineToggleButton.setToolTipText("Trait");
        wireTool(lineToggleButton, new LineTool(drawGrid, drawLayer, drawGrid.getOverlay()));

        toolButtonGroup.add(circleToggleButton);
        circleToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_circle.png"))); // NOI18N
        circleToggleButton.setToolTipText("Cercle");
        wireTool(circleToggleButton, new CircleStrokeTool(drawGrid, drawLayer, drawGrid.getOverlay()));

        toolButtonGroup.add(rectangleToggleButton);
        rectangleToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_rectangle.png"))); // NOI18N
        rectangleToggleButton.setToolTipText("Rectangle");
        wireTool(rectangleToggleButton, new RectangleStrokeTool(drawGrid));

        toolButtonGroup.add(rectangleFillToggleButton);
        rectangleFillToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_rectangle_fill.png"))); // NOI18N
        rectangleFillToggleButton.setToolTipText("Rectangle plein");
        wireTool(rectangleFillToggleButton, new RectangleFillTool(drawGrid));

        toolButtonGroup.add(ellipseToggleButton);
        ellipseToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_ellipse.png"))); // NOI18N
        ellipseToggleButton.setToolTipText("Ellipse");
        wireTool(ellipseToggleButton, new EllipseStrokeTool(drawGrid));

        toolButtonGroup.add(ellipseFillToggleButton);
        ellipseFillToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_ellipse_fill.png"))); // NOI18N
        ellipseFillToggleButton.setToolTipText("Ellipse pleine");
        wireTool(ellipseFillToggleButton, new EllipseFillTool(drawGrid));

        toolButtonGroup.add(colorPickerToggleButton);
        colorPickerToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_color_picker.png"))); // NOI18N
        colorPickerToggleButton.setToolTipText("Pipette");
        wireTool(colorPickerToggleButton, new ColorPickerTool(colorPaletteMap, drawGrid, drawLayer));

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

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(selectionToggleButton)
                            .addComponent(penToggleButton)
                            .addComponent(lineToggleButton))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(circleToggleButton)
                            .addComponent(bucketFillToggleButton)
                            .addComponent(magicWandToggleButton)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(rectangleToggleButton)
                        .addGap(0, 0, 0)
                        .addComponent(rectangleFillToggleButton))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(ellipseToggleButton)
                        .addGap(0, 0, 0)
                        .addComponent(ellipseFillToggleButton))
                    .addComponent(colorPickerToggleButton)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(horizontalMirrorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(verticalMirrorButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(undoButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(redoButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(pasteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(zoomTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 49, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(2, 2, 2)
                        .addComponent(zoomPercentLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(previousLayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(nextLayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 429, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.CENTER)
                    .addComponent(alphaPaletteGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(previewGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(cancelButton)
                    .addComponent(paletteGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(okButton, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 93, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(gridScrollPane)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(magicWandToggleButton)
                            .addComponent(selectionToggleButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(penToggleButton)
                            .addComponent(bucketFillToggleButton))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(lineToggleButton)
                            .addComponent(circleToggleButton))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(rectangleToggleButton)
                            .addComponent(rectangleFillToggleButton))
                        .addGap(0, 0, 0)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(ellipseToggleButton)
                            .addComponent(ellipseFillToggleButton))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(colorPickerToggleButton)
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
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(previousLayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(nextLayerButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(paletteGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(alphaPaletteGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(previewGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addGap(0, 0, 0)
                        .addComponent(cancelButton)))
                .addContainerGap())
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
		if(editedLayer != null) {
			editedLayer.restoreData(drawLayer.copyData(), null);
		}
		if(!layers.isEmpty()) {
			copyDrawLayerDataToCurrentTile();
			
			for(int index = 0; index < layers.size(); index++) {
				if(tiles[index] != null) {
					layers.get(index).restoreData(tiles[index], null);
				}
			}
		}
		setVisible(false);
		for(final ActionListener listener : listeners) {
			listener.actionPerformed(evt);
		}
    }//GEN-LAST:event_okButtonActionPerformed

    private void previousLayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_previousLayerButtonActionPerformed
		setLayerIndex(layerIndex - 1);
    }//GEN-LAST:event_previousLayerButtonActionPerformed

    private void nextLayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_nextLayerButtonActionPerformed
        setLayerIndex(layerIndex + 1);
    }//GEN-LAST:event_nextLayerButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
		final boolean oldClipboardFull = isClipboardFull();
		
		clipboardData = drawLayer.copyData();
		clipboardSurface = new Rectangle(drawLayer.getWidth(), drawLayer.getHeight());
		
		firePropertyChange("clipboardFull", oldClipboardFull, isClipboardFull());
    }//GEN-LAST:event_copyButtonActionPerformed

    private void pasteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteButtonActionPerformed
		toolButtonGroup.clearSelection();
		pasteSelectionTool.setSelection(clipboardData, clipboardSurface);

		drawGrid.addMouseListener(pasteSelectionTool);
		drawGrid.addMouseMotionListener(pasteSelectionTool);
    }//GEN-LAST:event_pasteButtonActionPerformed

    private void horizontalMirrorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_horizontalMirrorButtonActionPerformed
		drawLayer.flipHorizontally();
    }//GEN-LAST:event_horizontalMirrorButtonActionPerformed

    private void verticalMirrorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_verticalMirrorButtonActionPerformed
		drawLayer.flipVertically();
    }//GEN-LAST:event_verticalMirrorButtonActionPerformed

	private void selectColor(MouseEvent event) {
		final Point point = paletteGrid.getLayerLocation(event.getX(), event.getY());
		colorPaletteMap.setSelection(point);
	}
	
	private void selectAlpha(MouseEvent event) {
		final Point point = alphaPaletteGrid.getLayerLocation(event.getX(), event.getY());
		alphaPaletteMap.setSelection(point);
		
		if(colorPalette instanceof AlphaColorPalette) {
			((AlphaColorPalette)colorPalette).setSelectedAlpha(alphaPalette.getSelectedTile());
		}
	}
	
	private void wireTool(JToggleButton button, final Tool tool) {
		button.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					drawGrid.addMouseListener(tool);
					drawGrid.addMouseMotionListener(tool);
					
				} else {
					drawGrid.removeMouseListener(tool);
					drawGrid.removeMouseMotionListener(tool);
					
					tool.reset();
				}
			}
		});
	}
	
	private void copyDrawLayerDataToCurrentTile() {
		if(layerIndex >= 0 && layerIndex < tiles.length && tiles[layerIndex] != null) {
			tiles[layerIndex] = drawLayer.copyData();
		}
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.rca.mapmaker.model.palette.ColorPalette alphaPalette;
    private fr.rca.mapmaker.ui.Grid alphaPaletteGrid;
    private fr.rca.mapmaker.model.map.PaletteMap alphaPaletteMap;
    private javax.swing.JToggleButton bucketFillToggleButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JPanel centerPanel;
    private javax.swing.JToggleButton circleToggleButton;
    private fr.rca.mapmaker.model.palette.ColorPalette colorPalette;
    private fr.rca.mapmaker.model.map.PaletteMap colorPaletteMap;
    private javax.swing.JToggleButton colorPickerToggleButton;
    private javax.swing.JButton copyButton;
    private fr.rca.mapmaker.ui.Grid drawGrid;
    private fr.rca.mapmaker.model.map.TileLayer drawLayer;
    private fr.rca.mapmaker.model.map.TileMap drawMap;
    private fr.rca.mapmaker.model.map.TileLayer editedLayer;
    private javax.swing.JToggleButton ellipseFillToggleButton;
    private javax.swing.JToggleButton ellipseToggleButton;
    private javax.swing.JScrollPane gridScrollPane;
    private javax.swing.JButton horizontalMirrorButton;
    private javax.swing.JToggleButton lineToggleButton;
    private javax.swing.JToggleButton magicWandToggleButton;
    private fr.rca.mapmaker.editor.undo.LayerMemento memento;
    private javax.swing.JButton nextLayerButton;
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
    private fr.rca.mapmaker.model.selection.SmallSelectionStyle selectionStyle;
    private javax.swing.JToggleButton selectionToggleButton;
    private javax.swing.ButtonGroup toolButtonGroup;
    private javax.swing.JButton undoButton;
    private javax.swing.JButton verticalMirrorButton;
    private javax.swing.JLabel zoomPercentLabel;
    private javax.swing.JTextField zoomTextField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
