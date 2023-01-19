package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.editor.tool.ApplyFunctionTool;
import fr.rca.mapmaker.editor.tool.PenTool;
import fr.rca.mapmaker.editor.undo.LayerMemento;
import fr.rca.mapmaker.model.map.DataLayer;
import fr.rca.mapmaker.model.map.FunctionLayerPlugin;
import fr.rca.mapmaker.model.map.HasLayerPlugin;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.SpanningTileLayer;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.palette.ColorPalette;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.EditablePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.util.CleanEdge;
import java.awt.Dimension;
import java.awt.Frame;
import java.awt.Point;
import java.awt.event.MouseEvent;
import java.util.Arrays;
import java.util.Collections;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import lombok.Getter;
import lombok.Setter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

/**
 *
 * @author Raphaël Calabro <raph_kun at yahoo.fr>
 */
@Getter
@Setter
public class GenerateTileDialog extends javax.swing.JDialog {
	private Project project;
	private String gridSize = "4x2";
	private String functionString = "";
	private boolean cleanEdgeEnabled;

	private int sourceWidth;
	private int sourceHeight;

	private LayerMemento memento = new LayerMemento();

	/**
	 * Creates new form GenerateTileDialog
	 */
	public GenerateTileDialog(java.awt.Frame parent, Project project) {
		super(parent, false);
		this.project = project;

		initComponents();

		final PenTool penTool = new PenTool(drawGrid, memento);
		drawGrid.addMouseListener(penTool);
		drawGrid.addMouseMotionListener(penTool);

		if (project != null) {
			drawGrid.getTileMap().setPalette(getImagePalette());
			previewGrid.getTileMap().setPalette(getColorPalette());
		}
		final TileLayer drawLayer = (TileLayer)drawGrid.getTileMap().getLayers().get(0);
		memento.setLayers(Collections.singletonList(drawLayer));

		sizeTextFieldActionPerformed(null);
	}

	private EditableImagePalette getImagePalette() {
		Palette palette = project.getCurrentMap().getPalette();
		if (palette instanceof PaletteReference) {
			palette = ((PaletteReference) palette).getPalette();
		}
		if (palette instanceof EditableImagePalette) {
			return (EditableImagePalette)palette;
		} else {
			return null;
		}
	}

	private ColorPalette getColorPalette() {
		final EditableImagePalette imagePalette = getImagePalette();
		return imagePalette != null
				? imagePalette.getColorPalette()
				: null;
	}

	public String getXFunctionHitbox() {
		Layer previewLayer = previewGrid.getTileMap().getLayers().get(0);
		if (previewLayer instanceof HasLayerPlugin) {
			FunctionLayerPlugin plugin = ((HasLayerPlugin) previewLayer).getPlugin(FunctionLayerPlugin.class);
			if (plugin != null) {
				return plugin.getFunction();
			}
		}
		return "";
	}

	public String getYFunctionHitbox() {
		Layer previewLayer = previewGrid.getTileMap().getLayers().get(0);
		if (previewLayer instanceof HasLayerPlugin) {
			FunctionLayerPlugin plugin = ((HasLayerPlugin) previewLayer).getPlugin(FunctionLayerPlugin.class, FunctionLayerPlugin.Y_FUNCTION_NAME);
			if (plugin != null) {
				return plugin.getFunction();
			}
		}
		return "";
	}

	private void select(MouseEvent event, Grid grid) {
		final Point point = grid.getLayerLocation(event.getX(), event.getY());

		final PaletteMap paletteMap = (PaletteMap) grid.getTileMap();
		paletteMap.setSelection(point);

		grid.requestFocusInWindow();
	}

	private int edit(MouseEvent event, Grid grid) {
		if (event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
			final PaletteMap paletteMap = (PaletteMap) grid.getTileMap();
			final int tileIndex = paletteMap.getSelectedTile();
			final Palette palette = paletteMap.getPalette();

			if (tileIndex > -1 && palette.isEditable()) {
				final EditablePalette editablePalette = (EditablePalette) palette;
				final Point selection = paletteMap.getSelection();

				editablePalette.editTile(tileIndex, (Frame)getParent());

				grid.repaint(selection);
				drawGrid.repaint();

				return tileIndex;
			}
		}
		return -1;
	}

	private SpanningTileLayer createSpanningLayer(Dimension dimension) {
		final SpanningTileLayer layer = new SpanningTileLayer();
		layer.setSize(dimension.width, dimension.height);

		final PaletteMap paletteMap = (PaletteMap) paletteGrid.getTileMap();
		Palette palette = paletteMap.getPalette();
		if (palette instanceof PaletteReference) {
			palette = ((PaletteReference) palette).getPalette();
		}
		final EditableImagePalette imagePalette;
		if (palette instanceof EditableImagePalette) {
			imagePalette = (EditableImagePalette) palette;
		} else {
			JOptionPane.showMessageDialog(this, "La palette n'est pas une instance d'EditableImagePalette : " + palette.getClass().getSimpleName(), "Erreur", JOptionPane.ERROR_MESSAGE);
			return null;
		}

		final TileLayer drawLayer = (TileLayer)drawGrid.getTileMap().getLayers().get(0);

		for (int row = 0; row < dimension.height; row++) {
			for (int column = 0; column < dimension.width; column++) {
				final TileLayer tile;
				final int tileIndex = drawLayer.getTile(column, row);
				if (tileIndex >= 0 && tileIndex < imagePalette.size()) {
					tile = new TileLayer(imagePalette.getSource(tileIndex));
				} else {
					tile = new TileLayer(imagePalette.getTileSize(), imagePalette.getTileSize());
				}
				layer.setLayer(tile, column, row);
			}
		}

		layer.updateSize();
		return layer;
	}

	private void applyFunction(final DataLayer previewLayer, final TileLayer drawLayer) {
		final String oldFunctionString = getXFunctionHitbox();
		final int separatorIndex = functionString.lastIndexOf(ApplyFunctionTool.SEPARATOR);
		final String suffix;
		final String prefix;
		if (separatorIndex >= 0) {
			suffix = functionString.substring(separatorIndex + 1);
			// Retire le suffixe de la fonction pour appliquer juste une partie de la fonction à la déformation.
			prefix = functionString.substring(0, separatorIndex);
		} else {
			suffix = "";
			prefix = functionString;
		}
		if (cleanEdgeEnabled) {
			CleanEdge.builder()
					.function(prefix)
					.palette(getColorPalette())
					.slope(true)
					.cleanUpSmallDetails(true)
					.build()
					.shade(previewLayer);
		} else {
			ApplyFunctionTool.execute(previewLayer, prefix);
		}
		if (previewLayer instanceof HasLayerPlugin) {
			((HasLayerPlugin) previewLayer).setPlugin(new FunctionLayerPlugin(prefix + suffix));
		}
		firePropertyChange("XFunctionHitbox", oldFunctionString, getXFunctionHitbox());
		previewGrid.repaint();
		function.repaint();
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

        drawGrid = new fr.rca.mapmaker.ui.Grid();
        paletteScrollPane = new javax.swing.JScrollPane();
        paletteBackgroundPanel = new JPanel(new fr.rca.mapmaker.ui.LayerLayout(fr.rca.mapmaker.ui.LayerLayout.Disposition.TOP_LEFT));
        paletteGrid = new fr.rca.mapmaker.ui.Grid();
        javax.swing.JTextField sizeTextField = new javax.swing.JTextField();
        javax.swing.JLabel sizeLabel = new javax.swing.JLabel();
        javax.swing.JLabel previewLabel = new javax.swing.JLabel();
        javax.swing.JLabel functionLabel = new javax.swing.JLabel();
        javax.swing.JTextField functionTextField = new javax.swing.JTextField();
        javax.swing.JButton addButton = new javax.swing.JButton();
        javax.swing.JCheckBox cleanEdgeCheckBox = new javax.swing.JCheckBox();
        previewPanel = new javax.swing.JPanel(new fr.rca.mapmaker.ui.LayerLayout(fr.rca.mapmaker.ui.LayerLayout.Disposition.CENTER));
        function = new fr.rca.mapmaker.ui.Function();
        previewGrid = new fr.rca.mapmaker.ui.Grid();
        presetComboBox = new javax.swing.JComboBox<>();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        paletteScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        paletteScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        paletteBackgroundPanel.setBackground(java.awt.Color.darkGray);
        paletteBackgroundPanel.setMinimumSize(new java.awt.Dimension(128, 15));

        paletteGrid.setMaximumSize(new java.awt.Dimension(128, 32767));
        paletteGrid.setMinimumSize(new java.awt.Dimension(128, 5));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${project.currentPaletteMap}"), paletteGrid, org.jdesktop.beansbinding.BeanProperty.create("tileMap"));
        bindingGroup.addBinding(binding);

        paletteGrid.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                selectTileOnDrag(evt);
            }
        });
        paletteGrid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectTileOnClick(evt);
                paletteGrideditTile(evt);
            }
        });

        javax.swing.GroupLayout paletteGridLayout = new javax.swing.GroupLayout(paletteGrid);
        paletteGrid.setLayout(paletteGridLayout);
        paletteGridLayout.setHorizontalGroup(
            paletteGridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 0, Short.MAX_VALUE)
        );
        paletteGridLayout.setVerticalGroup(
            paletteGridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 32, Short.MAX_VALUE)
        );

        paletteBackgroundPanel.add(paletteGrid);

        paletteScrollPane.setViewportView(paletteBackgroundPanel);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${gridSize}"), sizeTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        sizeTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                sizeTextFieldActionPerformed(evt);
            }
        });

        sizeLabel.setText("Taille :");

        previewLabel.setText("Aperçu :");

        functionLabel.setText("Fonction :");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${functionString}"), functionTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        functionTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                functionTextFieldActionPerformed(evt);
            }
        });

        addButton.setText("Ajouter");
        addButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addButtonActionPerformed(evt);
            }
        });

        cleanEdgeCheckBox.setText("clean edge");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${cleanEdgeEnabled}"), cleanEdgeCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        cleanEdgeCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cleanEdgeCheckBoxActionPerformed(evt);
            }
        });

        function.setMinimumPixelSize(4);
        function.setPreferredSize(previewGrid.getPreferredSize());
        function.setSourceHeight(previewGrid.getTileMapHeight());
        function.setSourceWidth(previewGrid.getTileMapWidth());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, previewGrid, org.jdesktop.beansbinding.ELProperty.create("${preferredSize}"), function, org.jdesktop.beansbinding.BeanProperty.create("preferredSize"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sourceHeight}"), function, org.jdesktop.beansbinding.BeanProperty.create("sourceHeight"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sourceWidth}"), function, org.jdesktop.beansbinding.BeanProperty.create("sourceWidth"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${XFunctionHitbox}"), function, org.jdesktop.beansbinding.BeanProperty.create("XFunction"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${YFunctionHitbox}"), function, org.jdesktop.beansbinding.BeanProperty.create("YFunction"));
        bindingGroup.addBinding(binding);

        previewPanel.add(function);
        previewPanel.add(previewGrid);

        presetComboBox.setModel(new javax.swing.DefaultComboBoxModel<>(new String[] { "32 - x / 2", "x / 2", "32 - (x * x) / 512", "32 - ((x - 128) * (x - 128)) / 512", "(x * x) / 512", "((x - 128) * (x - 128)) / 512", "x", "32 - x", "(x * x) / 256", "-(x * x) / 256 + 64", "-((x - 128) * (x - 128)) / 256 + 64", "((x - 128) * (x - 128)) / 256", "sqrt(64 * 64 - ((x + 64) - 64) * ((x + 64) - 64))", "sqrt(64 * 64 - (x - 64) * (x - 64))", "64 - sqrt(64 * 64 - (x - 64) * (x - 64))", "64 - sqrt(64 * 64 - ((x + 64) - 64) * ((x + 64) - 64))", " " }));
        presetComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                presetComboBoxActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(sizeTextField)
                    .addComponent(functionTextField)
                    .addComponent(sizeLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(drawGrid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 165, Short.MAX_VALUE)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(previewLabel)
                            .addComponent(functionLabel))
                        .addGap(0, 0, Short.MAX_VALUE))
                    .addComponent(cleanEdgeCheckBox, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                    .addComponent(presetComboBox, 0, 0, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(paletteScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 169, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(addButton, javax.swing.GroupLayout.Alignment.TRAILING))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                    .addComponent(addButton)
                    .addComponent(sizeLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(sizeTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(functionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(functionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(7, 7, 7)
                        .addComponent(presetComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cleanEdgeCheckBox)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(drawGrid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(previewLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, 31, Short.MAX_VALUE))
                    .addComponent(paletteScrollPane))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void selectTileOnDrag(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectTileOnDrag
        select(evt, paletteGrid);
    }//GEN-LAST:event_selectTileOnDrag

    private void selectTileOnClick(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectTileOnClick
        select(evt, paletteGrid);
    }//GEN-LAST:event_selectTileOnClick

    private void paletteGrideditTile(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_paletteGrideditTile
        edit(evt, paletteGrid);
    }//GEN-LAST:event_paletteGrideditTile

    private void sizeTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_sizeTextFieldActionPerformed
		final int[] size = Arrays.stream(gridSize.split("x"))
			.mapToInt(TileMapEditor::toInt)
			.toArray();
		if (size.length != 2) {
			return;
		}
		final Dimension dimension = new Dimension(size[0], size[1]);
		final TileLayer drawLayer = (TileLayer)drawGrid.getTileMap().getLayers().get(0);
		drawLayer.resize(dimension.width, dimension.height);

		final SpanningTileLayer previewLayer = createSpanningLayer(dimension);
		applyFunction(previewLayer, drawLayer);
		previewGrid.getTileMap().setLayerAtIndex(0, previewLayer);

		final int tileSize = getImagePalette().getTileSize();
		final Dimension gridDimension = new Dimension(dimension.width * tileSize, dimension.height * tileSize);

		drawGrid.setPreferredSize(gridDimension);
		drawGrid.setSize(gridDimension);
		previewGrid.setPreferredSize(gridDimension);
		previewGrid.setSize(gridDimension);

		pack();

		final int oldSourceWith = sourceWidth;
		sourceWidth = gridDimension.width;
		firePropertyChange("sourceWidth", oldSourceWith, sourceWidth);
		final int oldSourceHeight = sourceHeight;
		sourceHeight = gridDimension.height;
		firePropertyChange("sourceHeight", oldSourceHeight, sourceHeight);

    }//GEN-LAST:event_sizeTextFieldActionPerformed

    private void functionTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_functionTextFieldActionPerformed
		final TileLayer drawLayer = (TileLayer)drawGrid.getTileMap().getLayers().get(0);

		final SpanningTileLayer previewLayer = createSpanningLayer(new Dimension(drawLayer.getWidth(), drawLayer.getHeight()));
		previewGrid.getTileMap().setLayerAtIndex(0, previewLayer);

		applyFunction(previewLayer, drawLayer);
    }//GEN-LAST:event_functionTextFieldActionPerformed

    private void addButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addButtonActionPerformed
		final Layer drawLayer = drawGrid.getTileMap().getLayers().get(0);
		final SpanningTileLayer spanningTileLayer = (SpanningTileLayer)previewGrid.getTileMap().getLayers().get(0);
		final EditableImagePalette imagePalette = getImagePalette();

		final Dimension sizeToFit = new Dimension(drawLayer.getWidth(), drawLayer.getHeight());
		final Dimension paletteDimension = new Dimension(imagePalette.getColumns(), imagePalette.size() / imagePalette.getColumns());

		final Point topLeft = getInsertPoint(sizeToFit, paletteDimension, imagePalette);
		for (int y = 0; y < sizeToFit.height; y++) {
			for (int x = 0; x < sizeToFit.width; x++) {
				DataLayer source = spanningTileLayer.getLayer(x, y);
				if (source == null) {
					source = imagePalette.createEmptySource();
				}
				final int index = (y + topLeft.y) * paletteDimension.width + (x + topLeft.x);
				if (index < imagePalette.size()) {
					imagePalette.getSource(index).restoreData(source);
					imagePalette.renderTile(index);
				} else {
					imagePalette.addDataLayers(Collections.singletonList(source));
				}
			}
			for (int x = sizeToFit.width + topLeft.x; x < paletteDimension.width; x++) {
				final int index = (y + topLeft.y) * paletteDimension.width + x;
				if (index >= imagePalette.size()) {
					imagePalette.addDataLayers(Collections.singletonList(imagePalette.createEmptySource()));
				}
			}
		}
    }//GEN-LAST:event_addButtonActionPerformed

	private @NotNull Point getInsertPoint(Dimension sizeToFit, Dimension paletteDimension, EditableImagePalette imagePalette) {
		final int maxX = paletteDimension.width - sizeToFit.width;
		int minimumY = Integer.MAX_VALUE;
		Integer bestX = null;
		for (int x = 0; x <= maxX; x++) {
			Integer y = canFitAtX(sizeToFit, x, paletteDimension, imagePalette);
			if (y != null && y < minimumY) {
				minimumY = y;
				bestX = x;
			}
		}
		return bestX != null
				? new Point(bestX, minimumY)
				: new Point(0, paletteDimension.height);
	}

	private @Nullable Integer canFitAtX(Dimension sizeToFit, int startX, Dimension paletteDimension, EditableImagePalette imagePalette) {
		Integer fitFromY = null;
		final int maxX = startX + sizeToFit.width;
		for (int y = paletteDimension.height - 1; y >= 0; y--) {
			for (int x = startX; x < maxX; x++) {
				if (!imagePalette.getSource(y * paletteDimension.width + x).isEmpty()) {
					return fitFromY;
				}
			}
			fitFromY = y;
		}
		return fitFromY;
	}

    private void cleanEdgeCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cleanEdgeCheckBoxActionPerformed
		functionTextFieldActionPerformed(evt);
    }//GEN-LAST:event_cleanEdgeCheckBoxActionPerformed

    private void presetComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_presetComboBoxActionPerformed
		final String oldFunctionString = this.functionString;
		this.functionString = (String) presetComboBox.getModel().getSelectedItem();
		firePropertyChange("functionString", oldFunctionString, functionString);
		functionTextFieldActionPerformed(evt);
    }//GEN-LAST:event_presetComboBoxActionPerformed

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Set the Nimbus look and feel */
        //<editor-fold defaultstate="collapsed" desc=" Look and feel setting code (optional) ">
        /* If Nimbus (introduced in Java SE 6) is not available, stay with the default look and feel.
		 * For details see http://download.oracle.com/javase/tutorial/uiswing/lookandfeel/plaf.html 
		 */
		try {
			for (javax.swing.UIManager.LookAndFeelInfo info : javax.swing.UIManager.getInstalledLookAndFeels()) {
				if ("Nimbus".equals(info.getName())) {
					javax.swing.UIManager.setLookAndFeel(info.getClassName());
					break;
				}
			}
		} catch (ClassNotFoundException ex) {
			java.util.logging.Logger.getLogger(GenerateTileDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (InstantiationException ex) {
			java.util.logging.Logger.getLogger(GenerateTileDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (IllegalAccessException ex) {
			java.util.logging.Logger.getLogger(GenerateTileDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		} catch (javax.swing.UnsupportedLookAndFeelException ex) {
			java.util.logging.Logger.getLogger(GenerateTileDialog.class.getName()).log(java.util.logging.Level.SEVERE, null, ex);
		}
        //</editor-fold>

		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				GenerateTileDialog dialog = new GenerateTileDialog(new javax.swing.JFrame(), null);
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.rca.mapmaker.ui.Grid drawGrid;
    private fr.rca.mapmaker.ui.Function function;
    private javax.swing.JPanel paletteBackgroundPanel;
    private fr.rca.mapmaker.ui.Grid paletteGrid;
    private javax.swing.JScrollPane paletteScrollPane;
    private javax.swing.JComboBox<String> presetComboBox;
    private fr.rca.mapmaker.ui.Grid previewGrid;
    private javax.swing.JPanel previewPanel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
