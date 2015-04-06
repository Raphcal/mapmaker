/*
 * MapEditor.java
 *
 * Created on 13 mars 2012, 15:33:14
 */
package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.editor.tool.BucketFillTool;
import fr.rca.mapmaker.editor.tool.PenTool;
import fr.rca.mapmaker.editor.tool.RectangleFillTool;
import fr.rca.mapmaker.editor.tool.SelectionTool;
import fr.rca.mapmaker.editor.tool.Tool;
import fr.rca.mapmaker.model.map.Layer;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditablePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.io.Format;
import fr.rca.mapmaker.io.FormatFileFilter;
import fr.rca.mapmaker.io.Formats;
import fr.rca.mapmaker.io.SupportedOperation;
import fr.rca.mapmaker.io.internal.InternalFormat;
import fr.rca.mapmaker.model.map.SpanningTileLayer;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Instance;
import fr.rca.mapmaker.motion.TrajectoryPreview;
import fr.rca.mapmaker.preferences.PreferencesManager;
import fr.rca.mapmaker.ui.Grid;
import fr.rca.mapmaker.ui.LayerLayout;
import java.awt.Color;
import java.awt.Point;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.ItemEvent;
import java.awt.event.ItemListener;
import java.awt.event.KeyEvent;
import java.awt.event.MouseEvent;
import java.awt.event.WindowAdapter;
import java.awt.event.WindowEvent;
import java.beans.PropertyChangeListener;
import java.io.File;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.JFileChooser;
import javax.swing.JMenuItem;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.JScrollPane;
import javax.swing.JToggleButton;
import javax.swing.JViewport;
import javax.swing.border.CompoundBorder;
import javax.swing.border.MatteBorder;
import javax.swing.filechooser.FileFilter;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class MapEditor extends javax.swing.JFrame {
	private static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("resources/language");

	private TileMap selectedTileMap;
	private TileLayer selectedLayer;
	private File currentFile;
	private Format currentFormat;
	
	/** Creates new form MapEditor */
	public MapEditor() {
		initComponents();
		setUpMacOSXStyle();
		
		// Scrolling
		refreshScrollMode();
		
		addWindowListener(new WindowAdapter() {

			@Override
			public void windowClosing(WindowEvent e) {
				final File currentDirectory = fileChooser.getCurrentDirectory();
				PreferencesManager.set(PreferencesManager.CURRENT_DIRECTORY, currentDirectory.getPath());
			}
		});
	}
	
	private void setUpMacOSXStyle() {
		final String osName = System.getProperty("os.name");
		
		if(osName != null && osName.startsWith("Mac OS X")) {
			mapGrid.setBackground(Color.LIGHT_GRAY);
			paletteGrid.setBackground(Color.DARK_GRAY);
			spritePaletteGrid.setBackground(Color.DARK_GRAY);
			
			final MatteBorder border = new MatteBorder(1, 0, 0, 0, Color.GRAY);
			
			mapListScrollPane.setBorder(border);
			mapScrollPane.setBorder(new CompoundBorder(border, new MatteBorder(0, 1, 0, 1, Color.LIGHT_GRAY)));
			paletteScrollPane.setBorder(border);
			spritePaletteScrollPane.setBorder(border);
			layerListScrollPane.setBorder(null);
			
			mapScrollPane.setHorizontalScrollBarPolicy(JScrollPane.HORIZONTAL_SCROLLBAR_ALWAYS);
			mapScrollPane.setVerticalScrollBarPolicy(JScrollPane.VERTICAL_SCROLLBAR_ALWAYS);
			
			layerList.setFont(layerList.getFont().deriveFont(11.0f));
			
			fileMenu.remove(quitSeparator);
			fileMenu.remove(quitMenuItem);
		}
	}
	
	private void repaintMapGrid() {
		mapGrid.repaint(mapScrollPane.getViewport().getViewRect());
	}
	
	private void configureButton(final JToggleButton button, final Tool tool) {
		button.addItemListener(new ItemListener() {
			
			@Override
			public void itemStateChanged(ItemEvent e) {
				
				if(e.getStateChange() == ItemEvent.SELECTED) {
					mapGrid.addMouseListener(tool);
					mapGrid.addMouseMotionListener(tool);
					
				} else {
					mapGrid.removeMouseListener(tool);
					mapGrid.removeMouseMotionListener(tool);
					
					tool.reset();
				}
			}
		});
	}
	
	private void configureFileChooser(SupportedOperation operation) {
		fileChooser.setMultiSelectionEnabled(false);
		
		for(final FileFilter fileFilter : fileChooser.getChoosableFileFilters())
			fileChooser.removeChoosableFileFilter(fileFilter);
		
		Format favoriteFormat = null;
		
		for(final Format format : Formats.getFormats()) {
			if(format.hasSupportFor(operation)) {
				fileChooser.addChoosableFileFilter(format.getFileFilter());
				
				if(favoriteFormat == null || format instanceof InternalFormat) {
					favoriteFormat = format;
				}
			}
		}
		
		if(favoriteFormat != null) {
			fileChooser.setFileFilter(favoriteFormat.getFileFilter());
		}
	}
	
	/** This method is called from within the constructor to
	 * initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is
	 * always regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {
        bindingGroup = new org.jdesktop.beansbinding.BindingGroup();

        project = fr.rca.mapmaker.model.project.Project.createEmptyProject();
        tileMapListRenderer = new fr.rca.mapmaker.ui.TileMapListRenderer();
        mapPopupMenu = new javax.swing.JPopupMenu();
        editMapMenuItem = new javax.swing.JMenuItem();
        layerPopupMenu = new javax.swing.JPopupMenu();
        editLayerMenuItem = new javax.swing.JMenuItem();
        fileChooser = new javax.swing.JFileChooser();
        toolGroup = new javax.swing.ButtonGroup();
        layerMemento = new fr.rca.mapmaker.editor.undo.LayerMemento();
        spriteTool = new fr.rca.mapmaker.editor.tool.SpriteTool();
        tilePopupMenu = new javax.swing.JPopupMenu();
        inspectTileMenuItem = new javax.swing.JMenuItem();
        tileInspector = new TileInspector(this, false);
        penTool = new PenTool(mapGrid, layerMemento);
        mapScrollPane = new javax.swing.JScrollPane();
        mapBackgroundPanel = new JPanel(new LayerLayout(LayerLayout.Disposition.TOP_LEFT));
        spriteLayerPanel = new javax.swing.JPanel();
        mapGrid = new fr.rca.mapmaker.ui.Grid();
        mapListScrollPane = new javax.swing.JScrollPane();
        mapList = new javax.swing.JList();
        javax.swing.JToolBar mapToolBar = new javax.swing.JToolBar();
        javax.swing.JButton addMapButton = new javax.swing.JButton();
        javax.swing.JButton removeMapButton = new javax.swing.JButton();
        gridToolBar = new javax.swing.JToolBar();
        undoButton = new javax.swing.JButton();
        redoButton = new javax.swing.JButton();
        jSeparator1 = new javax.swing.JToolBar.Separator();
        devicePreviewButton = new javax.swing.JButton();
        zoomSeparator = new javax.swing.JToolBar.Separator();
        zoomLabel = new javax.swing.JLabel();
        zoomTextField = new javax.swing.JTextField();
        zoomPercentLabel = new javax.swing.JLabel();
        paletteTabbedPane = new javax.swing.JTabbedPane();
        mapPanel = new javax.swing.JPanel();
        paletteScrollPane = new javax.swing.JScrollPane();
        paletteBackgroundPanel = new JPanel(new fr.rca.mapmaker.ui.LayerLayout(fr.rca.mapmaker.ui.LayerLayout.Disposition.TOP_LEFT));
        paletteGrid = new fr.rca.mapmaker.ui.Grid();
        javax.swing.JToolBar layerToolBar = new javax.swing.JToolBar();
        addLayerButton = new javax.swing.JButton();
        removeLayerButton = new javax.swing.JButton();
        javax.swing.JToggleButton focusToggleButton = new javax.swing.JToggleButton();
        layerUpButton = new javax.swing.JButton();
        layerDownButton = new javax.swing.JButton();
        layerListScrollPane = new javax.swing.JScrollPane();
        layerList = new javax.swing.JList();
        javax.swing.JToolBar paletteToolBar = new javax.swing.JToolBar();
        penToggleButton = new javax.swing.JToggleButton();
        configureButton(penToggleButton, penTool);
        bucketFillToggleButton = new javax.swing.JToggleButton();
        rectangleToggleButton = new javax.swing.JToggleButton();
        selectionToggleButton = new javax.swing.JToggleButton();
        spritePanel = new javax.swing.JPanel();
        spritePaletteScrollPane = new javax.swing.JScrollPane();
        spriteBackgroundPanel = new JPanel(new fr.rca.mapmaker.ui.LayerLayout(fr.rca.mapmaker.ui.LayerLayout.Disposition.TOP_LEFT));
        spritePaletteGrid = new fr.rca.mapmaker.ui.Grid();
        spritePaletteToolBar = new javax.swing.JToolBar();
        spriteToggleButton = new javax.swing.JToggleButton();
        javax.swing.JMenuBar menuBar = new javax.swing.JMenuBar();
        fileMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem newProjectMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem openMenuItem = new javax.swing.JMenuItem();
        openRecentMenu = new javax.swing.JMenu();
        clearRecentMenuItem = new javax.swing.JMenuItem();
        importSeparator = new javax.swing.JPopupMenu.Separator();
        importMenuItem = new javax.swing.JMenuItem();
        saveSeparator = new javax.swing.JPopupMenu.Separator();
        javax.swing.JMenuItem saveMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenuItem saveAsMenuItem = new javax.swing.JMenuItem();
        quitSeparator = new javax.swing.JPopupMenu.Separator();
        quitMenuItem = new javax.swing.JMenuItem();
        javax.swing.JMenu editMenu = new javax.swing.JMenu();
        javax.swing.JMenuItem managePalettesMenuItem = new javax.swing.JMenuItem();
        multipleEditMenuItem = new javax.swing.JMenuItem();
        trajectoryPreviewMenuItem = new javax.swing.JMenuItem();

        javax.swing.GroupLayout tileMapListRendererLayout = new javax.swing.GroupLayout(tileMapListRenderer);
        tileMapListRenderer.setLayout(tileMapListRendererLayout);
        tileMapListRendererLayout.setHorizontalGroup(
            tileMapListRendererLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 104, Short.MAX_VALUE)
        );
        tileMapListRendererLayout.setVerticalGroup(
            tileMapListRendererLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 92, Short.MAX_VALUE)
        );

        editMapMenuItem.setText(LANGUAGE.getString("popupmenu.map.edit")); // NOI18N
        editMapMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editMapMenuItemActionPerformed(evt);
            }
        });
        mapPopupMenu.add(editMapMenuItem);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resources/language"); // NOI18N
        editLayerMenuItem.setText(bundle.getString("popupmenu.layer.edit")); // NOI18N
        editLayerMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                editLayerMenuItemActionPerformed(evt);
            }
        });
        layerPopupMenu.add(editLayerMenuItem);

        final String path = PreferencesManager.get(PreferencesManager.CURRENT_DIRECTORY);
        if(path != null) {
            final File currentDirectory = new File(path);
            fileChooser.setCurrentDirectory(currentDirectory);
        }

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, project, org.jdesktop.beansbinding.ELProperty.create("${currentMap.layers}"), layerMemento, org.jdesktop.beansbinding.BeanProperty.create("layers"));
        bindingGroup.addBinding(binding);

        spriteTool.setSpriteLayer(spriteLayerPanel);
        spriteTool.setSpritePaletteGrid(spritePaletteGrid);
        spriteTool.setProject(project);
        spriteTool.setZoom(1.0);

        inspectTileMenuItem.setText("Inspecter");
        inspectTileMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                inspectTileMenuItemActionPerformed(evt);
            }
        });
        tilePopupMenu.add(inspectTileMenuItem);

        tileInspector.pack();

        javax.swing.GroupLayout tileInspectorLayout = new javax.swing.GroupLayout(tileInspector.getContentPane());
        tileInspector.getContentPane().setLayout(tileInspectorLayout);
        tileInspectorLayout.setHorizontalGroup(
            tileInspectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 400, Short.MAX_VALUE)
        );
        tileInspectorLayout.setVerticalGroup(
            tileInspectorLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 300, Short.MAX_VALUE)
        );

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, project, org.jdesktop.beansbinding.ELProperty.create("${currentPaletteMap}"), penTool, org.jdesktop.beansbinding.BeanProperty.create("paletteMap"));
        bindingGroup.addBinding(binding);

        penTool.setGrid(mapGrid);

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);
        setTitle(LANGUAGE.getString("app.title")); // NOI18N

        mapScrollPane.setBorder(javax.swing.BorderFactory.createCompoundBorder(javax.swing.BorderFactory.createEmptyBorder(0, 5, 0, 5), mapScrollPane.getBorder()));

        mapBackgroundPanel.setBackground(new java.awt.Color(128, 128, 128));

        spriteLayerPanel.setOpaque(false);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, mapGrid, org.jdesktop.beansbinding.ELProperty.create("${preferredSize}"), spriteLayerPanel, org.jdesktop.beansbinding.BeanProperty.create("preferredSize"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout spriteLayerPanelLayout = new javax.swing.GroupLayout(spriteLayerPanel);
        spriteLayerPanel.setLayout(spriteLayerPanelLayout);
        spriteLayerPanelLayout.setHorizontalGroup(
            spriteLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        spriteLayerPanelLayout.setVerticalGroup(
            spriteLayerPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        project.addPropertyChangeListener(new PropertyChangeListener() {
            @Override
            public void propertyChange(java.beans.PropertyChangeEvent changeEvent) {
                spriteInstancesChanged();
            }
        });

        mapBackgroundPanel.add(spriteLayerPanel);

        mapGrid.setViewport(mapScrollPane.getViewport());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, project, org.jdesktop.beansbinding.ELProperty.create("${currentMap}"), mapGrid, org.jdesktop.beansbinding.BeanProperty.create("tileMap"), "");
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout mapGridLayout = new javax.swing.GroupLayout(mapGrid);
        mapGrid.setLayout(mapGridLayout);
        mapGridLayout.setHorizontalGroup(
            mapGridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );
        mapGridLayout.setVerticalGroup(
            mapGridLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGap(0, 5, Short.MAX_VALUE)
        );

        mapBackgroundPanel.add(mapGrid);

        mapScrollPane.setViewportView(mapBackgroundPanel);

        mapListScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        mapListScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        mapList.setBackground(new java.awt.Color(223, 230, 238));
        mapList.setModel(project);
        mapList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        mapList.setCellRenderer(tileMapListRenderer);
        mapList.setSelectedIndex(0);
        mapList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mayShowMapPopup(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mayShowMapPopup(evt);
            }
        });
        mapList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                mapListValueChanged(evt);
            }
        });
        mapListScrollPane.setViewportView(mapList);

        mapToolBar.setFloatable(false);
        mapToolBar.setRollover(true);

        addMapButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add.png"))); // NOI18N
        addMapButton.setToolTipText(bundle.getString("map.add")); // NOI18N
        addMapButton.setFocusable(false);
        addMapButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addMapButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        addMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addMapButtonActionPerformed(evt);
            }
        });
        mapToolBar.add(addMapButton);

        removeMapButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/remove.png"))); // NOI18N
        removeMapButton.setToolTipText(bundle.getString("map.remove")); // NOI18N
        removeMapButton.setFocusable(false);
        removeMapButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeMapButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        removeMapButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeMapButtonActionPerformed(evt);
            }
        });
        mapToolBar.add(removeMapButton);

        gridToolBar.setFloatable(false);
        gridToolBar.setRollover(true);

        undoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_undo.png"))); // NOI18N
        undoButton.setFocusable(false);
        undoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        undoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, layerMemento, org.jdesktop.beansbinding.ELProperty.create("${undoable}"), undoButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        undoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                undoButtonActionPerformed(evt);
            }
        });
        gridToolBar.add(undoButton);

        redoButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_redo.png"))); // NOI18N
        redoButton.setFocusable(false);
        redoButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        redoButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, layerMemento, org.jdesktop.beansbinding.ELProperty.create("${redoable}"), redoButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        redoButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                redoButtonActionPerformed(evt);
            }
        });
        gridToolBar.add(redoButton);
        gridToolBar.add(jSeparator1);

        devicePreviewButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/preview.png"))); // NOI18N
        devicePreviewButton.setFocusable(false);
        devicePreviewButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        devicePreviewButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        devicePreviewButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                devicePreviewButtonActionPerformed(evt);
            }
        });
        gridToolBar.add(devicePreviewButton);
        gridToolBar.add(zoomSeparator);

        zoomLabel.setText(LANGUAGE.getString("view.zoom")); // NOI18N
        gridToolBar.add(zoomLabel);

        zoomTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        zoomTextField.setText("100");
        zoomTextField.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                zoomTextFieldActionPerformed(evt);
            }
        });
        gridToolBar.add(zoomTextField);

        zoomPercentLabel.setText("%");
        gridToolBar.add(zoomPercentLabel);

        mapPanel.setPreferredSize(new java.awt.Dimension(150, 423));

        paletteScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        paletteScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        paletteBackgroundPanel.setBackground(java.awt.Color.darkGray);

        paletteGrid.setComponentPopupMenu(tilePopupMenu);
        paletteGrid.setMaximumSize(new java.awt.Dimension(128, 32767));
        paletteGrid.setMinimumSize(new java.awt.Dimension(128, 5));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, project, org.jdesktop.beansbinding.ELProperty.create("${currentPaletteMap}"), paletteGrid, org.jdesktop.beansbinding.BeanProperty.create("tileMap"));
        bindingGroup.addBinding(binding);

        paletteGrid.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                selectTile(evt);
            }
        });
        paletteGrid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectTile(evt);
                editTile(evt);
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
            .addGap(0, 5, Short.MAX_VALUE)
        );

        paletteBackgroundPanel.add(paletteGrid);

        paletteScrollPane.setViewportView(paletteBackgroundPanel);

        layerToolBar.setFloatable(false);
        layerToolBar.setRollover(true);

        addLayerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/add.png"))); // NOI18N
        addLayerButton.setToolTipText(bundle.getString("layer.add")); // NOI18N
        addLayerButton.setFocusable(false);
        addLayerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        addLayerButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, project, org.jdesktop.beansbinding.ELProperty.create("${selected}"), addLayerButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        addLayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                addLayerButtonActionPerformed(evt);
            }
        });
        layerToolBar.add(addLayerButton);

        removeLayerButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/remove.png"))); // NOI18N
        removeLayerButton.setToolTipText(bundle.getString("layer.remove")); // NOI18N
        removeLayerButton.setFocusable(false);
        removeLayerButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        removeLayerButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ, project, org.jdesktop.beansbinding.ELProperty.create("${selected}"), removeLayerButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        removeLayerButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeLayerButtonActionPerformed(evt);
            }
        });
        layerToolBar.add(removeLayerButton);

        focusToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/focus.png"))); // NOI18N
        focusToggleButton.setToolTipText(bundle.getString("focus.visible")); // NOI18N
        focusToggleButton.setFocusable(false);
        focusToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        focusToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, project, org.jdesktop.beansbinding.ELProperty.create("${selected}"), focusToggleButton, org.jdesktop.beansbinding.BeanProperty.create("enabled"));
        bindingGroup.addBinding(binding);

        focusToggleButton.addItemListener(new java.awt.event.ItemListener() {
            public void itemStateChanged(java.awt.event.ItemEvent evt) {
                focusToggleButtonItemStateChanged(evt);
            }
        });
        layerToolBar.add(focusToggleButton);

        layerUpButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/arrow_up.png"))); // NOI18N
        layerUpButton.setToolTipText(bundle.getString("layer.moveUp")); // NOI18N
        layerUpButton.setFocusable(false);
        layerUpButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        layerUpButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        layerUpButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layerUpButtonActionPerformed(evt);
            }
        });
        layerToolBar.add(layerUpButton);

        layerDownButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/arrow_down.png"))); // NOI18N
        layerDownButton.setToolTipText(bundle.getString("layer.moveDown")); // NOI18N
        layerDownButton.setFocusable(false);
        layerDownButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        layerDownButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        layerDownButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                layerDownButtonActionPerformed(evt);
            }
        });
        layerToolBar.add(layerDownButton);

        layerListScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        layerListScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        layerList.setSelectionMode(javax.swing.ListSelectionModel.SINGLE_SELECTION);
        layerList.setSelectedIndex(0);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, project, org.jdesktop.beansbinding.ELProperty.create("${currentLayerModel}"), layerList, org.jdesktop.beansbinding.BeanProperty.create("model"));
        bindingGroup.addBinding(binding);

        layerList.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mousePressed(java.awt.event.MouseEvent evt) {
                mayShowLayerPopup(evt);
            }
            public void mouseReleased(java.awt.event.MouseEvent evt) {
                mayShowLayerPopup(evt);
            }
        });
        layerList.addListSelectionListener(new javax.swing.event.ListSelectionListener() {
            public void valueChanged(javax.swing.event.ListSelectionEvent evt) {
                layerListValueChanged(evt);
            }
        });
        layerListScrollPane.setViewportView(layerList);

        paletteToolBar.setFloatable(false);
        paletteToolBar.setRollover(true);

        toolGroup.add(penToggleButton);
        penToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_pen.png"))); // NOI18N
        penToggleButton.setSelected(true);
        penToggleButton.setFocusable(false);
        penToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        penToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        paletteToolBar.add(penToggleButton);

        toolGroup.add(bucketFillToggleButton);
        bucketFillToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_bucket_fill.png"))); // NOI18N
        bucketFillToggleButton.setFocusable(false);
        bucketFillToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        bucketFillToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        configureButton(bucketFillToggleButton, new BucketFillTool(mapGrid));
        paletteToolBar.add(bucketFillToggleButton);

        toolGroup.add(rectangleToggleButton);
        rectangleToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_rectangle_fill.png"))); // NOI18N
        rectangleToggleButton.setFocusable(false);
        rectangleToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        rectangleToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        configureButton(rectangleToggleButton, new RectangleFillTool(mapGrid));
        paletteToolBar.add(rectangleToggleButton);

        toolGroup.add(selectionToggleButton);
        selectionToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_selection.png"))); // NOI18N
        selectionToggleButton.setFocusable(false);
        selectionToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        selectionToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        configureButton(selectionToggleButton, new SelectionTool(mapGrid));
        paletteToolBar.add(selectionToggleButton);

        javax.swing.GroupLayout mapPanelLayout = new javax.swing.GroupLayout(mapPanel);
        mapPanel.setLayout(mapPanelLayout);
        mapPanelLayout.setHorizontalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(paletteScrollPane)
            .addComponent(layerListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
            .addComponent(layerToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
            .addComponent(paletteToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
        );
        mapPanelLayout.setVerticalGroup(
            mapPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, mapPanelLayout.createSequentialGroup()
                .addComponent(paletteToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(paletteScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layerToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(layerListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
        );

        paletteTabbedPane.addTab(LANGUAGE.getString("palette.background"), mapPanel); // NOI18N

        spritePanel.setPreferredSize(new java.awt.Dimension(144, 423));

        spritePaletteScrollPane.setHorizontalScrollBarPolicy(javax.swing.ScrollPaneConstants.HORIZONTAL_SCROLLBAR_NEVER);
        spritePaletteScrollPane.setVerticalScrollBarPolicy(javax.swing.ScrollPaneConstants.VERTICAL_SCROLLBAR_ALWAYS);

        spriteBackgroundPanel.setBackground(java.awt.Color.darkGray);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, project, org.jdesktop.beansbinding.ELProperty.create("${currentSpritePaletteMap}"), spritePaletteGrid, org.jdesktop.beansbinding.BeanProperty.create("tileMap"));
        bindingGroup.addBinding(binding);

        spritePaletteGrid.addMouseMotionListener(new java.awt.event.MouseMotionAdapter() {
            public void mouseDragged(java.awt.event.MouseEvent evt) {
                selectSprite(evt);
            }
        });
        spritePaletteGrid.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                selectSprite(evt);
                editSprite(evt);
            }
        });
        spritePaletteGrid.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                spritePaletteGridKeyPressed(evt);
            }
        });
        spriteBackgroundPanel.add(spritePaletteGrid);

        spritePaletteScrollPane.setViewportView(spriteBackgroundPanel);

        spritePaletteToolBar.setFloatable(false);
        spritePaletteToolBar.setRollover(true);

        toolGroup.add(spriteToggleButton);
        spriteToggleButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_sprite.png"))); // NOI18N
        spriteToggleButton.setFocusable(false);
        spriteToggleButton.setHorizontalTextPosition(javax.swing.SwingConstants.CENTER);
        spriteToggleButton.setVerticalTextPosition(javax.swing.SwingConstants.BOTTOM);
        configureButton(spriteToggleButton, spriteTool);
        spritePaletteToolBar.add(spriteToggleButton);

        javax.swing.GroupLayout spritePanelLayout = new javax.swing.GroupLayout(spritePanel);
        spritePanel.setLayout(spritePanelLayout);
        spritePanelLayout.setHorizontalGroup(
            spritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(spritePaletteScrollPane)
            .addGroup(spritePanelLayout.createSequentialGroup()
                .addComponent(spritePaletteToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 50, Short.MAX_VALUE))
        );
        spritePanelLayout.setVerticalGroup(
            spritePanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, spritePanelLayout.createSequentialGroup()
                .addComponent(spritePaletteToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(spritePaletteScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 392, Short.MAX_VALUE))
        );

        paletteTabbedPane.addTab(LANGUAGE.getString("palette.sprites"), spritePanel); // NOI18N

        fileMenu.setText(bundle.getString("menu.file")); // NOI18N

        newProjectMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_N, java.awt.event.InputEvent.META_MASK));
        newProjectMenuItem.setText(bundle.getString("menu.file.new")); // NOI18N
        newProjectMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                newProjectMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(newProjectMenuItem);

        openMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_O, java.awt.event.InputEvent.META_MASK));
        openMenuItem.setText(bundle.getString("menu.file.open")); // NOI18N
        openMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                openMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(openMenuItem);

        openRecentMenu.setText(bundle.getString("menu.file.openrecent")); // NOI18N
        buildRecentMenu();

        clearRecentMenuItem.setText(bundle.getString("menu.file.openrecent.clear")); // NOI18N
        final List<String> recents = PreferencesManager.getList(PreferencesManager.RECENT);
        clearRecentMenuItem.setEnabled(!recents.isEmpty());
        clearRecentMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                clearRecentMenuItemActionPerformed(evt);
            }
        });
        openRecentMenu.add(clearRecentMenuItem);

        fileMenu.add(openRecentMenu);
        fileMenu.add(importSeparator);

        importMenuItem.setText(bundle.getString("menu.file.import")); // NOI18N
        importMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                importMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(importMenuItem);
        fileMenu.add(saveSeparator);

        saveMenuItem.setAccelerator(javax.swing.KeyStroke.getKeyStroke(java.awt.event.KeyEvent.VK_S, java.awt.event.InputEvent.META_MASK));
        saveMenuItem.setText(bundle.getString("menu.file.save")); // NOI18N
        saveMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveMenuItem);

        saveAsMenuItem.setText(bundle.getString("menu.file.saveas")); // NOI18N
        saveAsMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                saveAsMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(saveAsMenuItem);
        fileMenu.add(quitSeparator);

        quitMenuItem.setText(bundle.getString("menu.file.quit")); // NOI18N
        quitMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                quitMenuItemActionPerformed(evt);
            }
        });
        fileMenu.add(quitMenuItem);

        menuBar.add(fileMenu);

        editMenu.setText(bundle.getString("menu.edit")); // NOI18N

        managePalettesMenuItem.setText(bundle.getString("menu.edit.managepalettes")); // NOI18N
        managePalettesMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                managePalettesMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(managePalettesMenuItem);

        multipleEditMenuItem.setText(LANGUAGE.getString("menu.edit.multipleedit")); // NOI18N
        multipleEditMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                multipleEditMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(multipleEditMenuItem);

        trajectoryPreviewMenuItem.setText(LANGUAGE.getString("menu.edit.trajectorypreview")); // NOI18N
        trajectoryPreviewMenuItem.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                trajectoryPreviewMenuItemActionPerformed(evt);
            }
        });
        editMenu.add(trajectoryPreviewMenuItem);

        menuBar.add(editMenu);

        setJMenuBar(menuBar);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mapToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(mapListScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 135, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(gridToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 210, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 411, Short.MAX_VALUE))
                    .addComponent(mapScrollPane))
                .addGap(0, 0, 0)
                .addComponent(paletteTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mapToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(gridToolBar, javax.swing.GroupLayout.PREFERRED_SIZE, 25, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addGap(0, 0, 0)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(mapScrollPane, javax.swing.GroupLayout.PREFERRED_SIZE, 0, Short.MAX_VALUE)
                    .addComponent(mapListScrollPane)))
            .addComponent(paletteTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void addMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addMapButtonActionPerformed
	final TileMapPropertiesDialog dialog = new TileMapPropertiesDialog(this, true);
	dialog.setTitle(LANGUAGE.getString("dialog.map.title.new"));
	dialog.setProject(project);
	dialog.setVisible(true);
	
	if(dialog.hasBeenConfirmed()) {
		project.addMap(dialog.getTileMap());
	}
}//GEN-LAST:event_addMapButtonActionPerformed

private void mapListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_mapListValueChanged
	project.setSelectedIndex(mapList.getSelectedIndex());
	mapBackgroundPanel.repaint();
	
	refreshScrollMode();
}//GEN-LAST:event_mapListValueChanged

private void editMapMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editMapMenuItemActionPerformed
	final TileMapPropertiesDialog dialog = new TileMapPropertiesDialog(
			selectedTileMap, this, true);
	dialog.setTitle(LANGUAGE.getString("dialog.map.title.edit"));
	dialog.setProject(project);
	dialog.setVisible(true);
	
	if(dialog.hasBeenConfirmed()) {
		final TileMap editedTileMap = dialog.getTileMap();
		
		selectedTileMap.setBackgroundColor(editedTileMap.getBackgroundColor());
		selectedTileMap.setPalette(editedTileMap.getPalette());
		
		final float width = editedTileMap.getWidth();
		final float height = editedTileMap.getHeight();
		
		for(final Layer layer : selectedTileMap.getLayers()) {
			if(layer instanceof TileLayer) {
				final int correctedWidth = (int) Math.ceil(width * Math.max(layer.getScrollRate(), 1.0f));
				final int correctedHeight = (int) Math.ceil(height * Math.max(layer.getScrollRate(), 1.0f));
				
				((TileLayer)layer).resize(correctedWidth, correctedHeight);
			}
		}
		
		project.currentPaletteChanged();
		repaintMapGrid();
	}
}//GEN-LAST:event_editMapMenuItemActionPerformed

private void mayShowMapPopup(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mayShowMapPopup
	if (evt.isPopupTrigger()) {
		selectedTileMap = project.getElementAt(mapList.locationToIndex(evt.getPoint()));
		mapPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
	}
}//GEN-LAST:event_mayShowMapPopup

private void quitMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_quitMenuItemActionPerformed
	setVisible(false);
	dispose();
}//GEN-LAST:event_quitMenuItemActionPerformed

private void addLayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_addLayerButtonActionPerformed
	final TileMap tileMap = project.getCurrentMap();
	
	final TileLayerPropertiesDialog dialog = new TileLayerPropertiesDialog("Calque " + (tileMap.getSize() + 1), this, true);
	dialog.setTitle(LANGUAGE.getString("dialog.layer.title.new"));
	dialog.setVisible(true);
	
	if(dialog.hasBeenConfirmed()) {
		final LayerProperties properties = dialog.getLayerProperties();
		
		final int width = (int) (tileMap.getWidth() * Math.max(properties.getScrollRate(), 1.0f));
		final int height = (int) (tileMap.getHeight() * Math.max(properties.getScrollRate(), 1.0f));
		
		final TileLayer tileLayer = new TileLayer(width, height);
		tileLayer.setScrollRate(properties.getScrollRate());
		tileLayer.setName(properties.getName());
		tileMap.add(tileLayer);
	}
}//GEN-LAST:event_addLayerButtonActionPerformed

private void removeLayerButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeLayerButtonActionPerformed
	final TileMap tileMap = project.getCurrentMap();
	tileMap.remove(layerList.getSelectedIndex());
	
	mapGrid.repaint(mapScrollPane.getViewport().getViewRect());
}//GEN-LAST:event_removeLayerButtonActionPerformed

private void removeMapButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeMapButtonActionPerformed
	project.removeMap(mapList.getSelectedIndex());
}//GEN-LAST:event_removeMapButtonActionPerformed

private void editLayerMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_editLayerMenuItemActionPerformed
	final TileLayerPropertiesDialog dialog = new TileLayerPropertiesDialog(
			selectedLayer, this, true);
	dialog.setTitle(LANGUAGE.getString("dialog.layer.title.edit"));
	dialog.setVisible(true);
	
	if(dialog.hasBeenConfirmed()) {
		final LayerProperties properties = dialog.getLayerProperties();
		final TileMap tileMap = project.getCurrentMap();
		
		final int width = (int) (tileMap.getWidth() * Math.max(properties.getScrollRate(), 1.0f));
		final int height = (int) (tileMap.getHeight() * Math.max(properties.getScrollRate(), 1.0f));
		
		selectedLayer.setScrollRate(properties.getScrollRate());
		selectedLayer.resize(width, height);
		selectedLayer.setName(properties.getName());
		
		repaintMapGrid();
		refreshScrollMode();
	}
}//GEN-LAST:event_editLayerMenuItemActionPerformed

private void mayShowLayerPopup(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_mayShowLayerPopup
	if (evt.isPopupTrigger()) {
		final TileMap map = project.getCurrentMap();
		final Layer layer = map.getElementAt(layerList.locationToIndex(evt.getPoint()));
		
		if(layer instanceof TileLayer) {
			selectedLayer = (TileLayer) layer;
			layerPopupMenu.show(evt.getComponent(), evt.getX(), evt.getY());
		}
	}
}//GEN-LAST:event_mayShowLayerPopup

private void focusToggleButtonItemStateChanged(java.awt.event.ItemEvent evt) {//GEN-FIRST:event_focusToggleButtonItemStateChanged
	mapGrid.setFocusVisible(evt.getStateChange() == ItemEvent.SELECTED);
	mapGrid.repaint(mapScrollPane.getViewport().getViewRect());
}//GEN-LAST:event_focusToggleButtonItemStateChanged

private void layerListValueChanged(javax.swing.event.ListSelectionEvent evt) {//GEN-FIRST:event_layerListValueChanged
	mapGrid.setActiveLayer(layerList.getSelectedIndex());
				
	if(mapGrid.isFocusVisible()) {
		mapGrid.repaint(mapScrollPane.getViewport().getViewRect());
	}
}//GEN-LAST:event_layerListValueChanged

private void selectTile(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectTile
	select(evt, paletteGrid);
	tileInspector.setTile((PaletteMap)paletteGrid.getTileMap());
}//GEN-LAST:event_selectTile

private void editTile(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editTile
	edit(evt, paletteGrid);
}//GEN-LAST:event_editTile

private void managePalettesMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_managePalettesMenuItemActionPerformed
	final ManagePalettesDialog dialog = new ManagePalettesDialog(project, this, true);
	dialog.setVisible(true);
}//GEN-LAST:event_managePalettesMenuItemActionPerformed

private void setCurrentFile(File file) {
	currentFile = file;
	getRootPane().putClientProperty("Window.documentFile", file);
	
	if(file != null) {
		setTitle(file.getName().substring(0, file.getName().lastIndexOf('.')) + " - MapMaker");
	} else {
		setTitle("MapMaker");
	}
}

private void saveAsMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveAsMenuItemActionPerformed
	configureFileChooser(SupportedOperation.SAVE);
	
	final int action = fileChooser.showSaveDialog(this);
				
	if(action == JFileChooser.APPROVE_OPTION) {
		final Format format = ((FormatFileFilter)fileChooser.getFileFilter()).getFormat();
		final File destination = format.normalizeFile(fileChooser.getSelectedFile());
		
		if(format.canLoadFiles()) {
			currentFormat = format;
			setCurrentFile(destination);
		}
		
		format.saveProject(project, destination);
	}
}//GEN-LAST:event_saveAsMenuItemActionPerformed

private void saveMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_saveMenuItemActionPerformed
	if(currentFile == null || currentFormat == null) {
		saveAsMenuItemActionPerformed(evt);
	} else {
		currentFormat.saveProject(project, currentFile);
	}
}//GEN-LAST:event_saveMenuItemActionPerformed

private void openMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_openMenuItemActionPerformed
	configureFileChooser(SupportedOperation.LOAD);
	
	final int action = fileChooser.showOpenDialog(this);
				
	if(action == JFileChooser.APPROVE_OPTION) {
		openProject(fileChooser.getSelectedFile(), ((FormatFileFilter)fileChooser.getFileFilter()).getFormat());
	}
}//GEN-LAST:event_openMenuItemActionPerformed

public void openFile(final File file) {
	if(file != null) {
		final Format format = Formats.getFormat(file.getName());
		
		if(format != null) {
			openProject(file, format);
		}
	}
}

private void openProject(File file, Format format) {
	currentFormat = format;
	setCurrentFile(file);
	addToRecentFiles(file);
		
	final Project newProject = currentFormat.openProject(currentFile);
	project.morphTo(newProject);
	mapList.setSelectedIndex(0);

	spritePaletteGrid.refresh();
	refreshScrollMode();
}

private void addToRecentFiles(File file) {
	final List<String> recents = PreferencesManager.getList(PreferencesManager.RECENT);
	
	final String path = file.getPath();
	int position = -1;
	
	final int size = recents.size();
	for(int index = 0; index < size; index++) {
		final String entry = recents.get(index);
		
		if(entry != null && entry.equals(path)) {
			position = index;
		}
	}
	
	if(position != -1) {
		recents.remove(position);
	}
	
	recents.add(0, file.getPath());
	
	while(recents.size() > 10) {
		recents.remove(10);
	}
	
	clearRecentMenuItem.setEnabled(true);
	buildRecentMenu();
}

private void buildRecentMenu() {
	openRecentMenu.removeAll();
	
	final List<String> recents = PreferencesManager.getList(PreferencesManager.RECENT);
	
	for(final String recent : recents) {
		if(recent != null) {
			final File file = new File(recent);

			final JMenuItem item = new JMenuItem();
			item.setText(file.getName());
			item.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					openFile(file);
				}
			});

			openRecentMenu.add(item);
		}
	}
	
	if(!recents.isEmpty()) {
		openRecentMenu.addSeparator();
	}
	
	openRecentMenu.add(clearRecentMenuItem);
}

	private void newProjectMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_newProjectMenuItemActionPerformed
		mapList.setSelectedIndex(0);
		project.morphTo(Project.createEmptyProject());
		
		spritePaletteGrid.refresh();
		refreshScrollMode();
		
		setCurrentFile(null);
		currentFormat = null;
	}//GEN-LAST:event_newProjectMenuItemActionPerformed

private void undoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_undoButtonActionPerformed
	// TODO: Prendre en charge l'ajout de layers
	layerMemento.undo();
}//GEN-LAST:event_undoButtonActionPerformed

private void redoButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_redoButtonActionPerformed
	layerMemento.redo();
}//GEN-LAST:event_redoButtonActionPerformed

    private void layerUpButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layerUpButtonActionPerformed
		final int selectedIndex = layerList.getSelectedIndex();
		if(selectedIndex > 0) {
			final TileMap tileMap = project.getCurrentMap();
			tileMap.swapLayers(selectedIndex, selectedIndex - 1);
			layerList.setSelectedIndex(selectedIndex - 1);

			mapGrid.repaint(mapScrollPane.getViewport().getViewRect());
		}
    }//GEN-LAST:event_layerUpButtonActionPerformed

    private void layerDownButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_layerDownButtonActionPerformed
		final int selectedIndex = layerList.getSelectedIndex();
		if(selectedIndex < project.getCurrentLayerModel().getSize() - 1) {
			final TileMap tileMap = project.getCurrentMap();
			tileMap.swapLayers(selectedIndex, selectedIndex + 1);
			layerList.setSelectedIndex(selectedIndex + 1);

			mapGrid.repaint(mapScrollPane.getViewport().getViewRect());
		}
    }//GEN-LAST:event_layerDownButtonActionPerformed

    private void importMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_importMenuItemActionPerformed
		configureFileChooser(SupportedOperation.IMPORT);
		
		fileChooser.setMultiSelectionEnabled(true);
		final int action = fileChooser.showOpenDialog(this);

		if(action == JFileChooser.APPROVE_OPTION) {
			final Format format = ((FormatFileFilter)fileChooser.getFileFilter()).getFormat();
			final File[] files = fileChooser.getSelectedFiles();
			format.importFiles(files, project);
		}
    }//GEN-LAST:event_importMenuItemActionPerformed

    private void devicePreviewButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_devicePreviewButtonActionPerformed
		final GamePreviewDialog gamePreviewDialog = new GamePreviewDialog(this, true);
		gamePreviewDialog.setTileMap(mapGrid.getTileMap());
		gamePreviewDialog.setVisible(true);
    }//GEN-LAST:event_devicePreviewButtonActionPerformed

    private void zoomTextFieldActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_zoomTextFieldActionPerformed
		try {
			final int size = Integer.parseInt(zoomTextField.getText());
			final double zoom = (double)size/100.0;
			
			// Zoom de la carte.
			mapGrid.setZoom(zoom);
			
			// Zoom des instances de sprite.
			spriteTool.setZoom(zoom);
			for(final Instance instance : project.getInstances()) {
				instance.setZoom(zoom);
			}
			
		} catch(NumberFormatException e) {
			// Ignoré.
		}
    }//GEN-LAST:event_zoomTextFieldActionPerformed

	private void spriteInstancesChanged() {
		spriteLayerPanel.removeAll();
		final List<Instance> instances = project.getInstances();
		if(instances != null) {
			final int size = Integer.parseInt(zoomTextField.getText());
			final double zoom = (double)size/100.0;
			
			final int mapWidth = mapGrid.getTileMapWidth() * mapGrid.getTileSize();
			final int mapHeight = mapGrid.getTileMapHeight() * mapGrid.getTileSize();
			
			for(final Instance instance : instances) {
				instance.setZoom(zoom);
				spriteLayerPanel.add(instance);
				
				if(instance.getX() > mapWidth || instance.getY() > mapHeight) {
					instance.setBounds(Math.min(mapWidth - instance.getWidth(), instance.getX()), 
						Math.min(mapHeight - instance.getHeight(), instance.getY()), 
						instance.getWidth(), instance.getHeight());
				}
			}
		}
		spriteTool.setInstances(instances);
	}
	
    private void selectSprite(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_selectSprite
		select(evt, spritePaletteGrid);
    }//GEN-LAST:event_selectSprite

    private void editSprite(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_editSprite
        final int editedIndex = edit(evt, spritePaletteGrid);
		
		if(editedIndex >= 0) {
			// Rafraîchissement des instances du sprite sélectionné.
			for(final Instance instance : project.getInstances()) {
				if(instance.getIndex() == editedIndex) {
					instance.redraw();
				}
			}
		}
    }//GEN-LAST:event_editSprite

    private void spritePaletteGridKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_spritePaletteGridKeyPressed
		if(evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			final PaletteMap paletteMap = (PaletteMap) spritePaletteGrid.getTileMap();
			paletteMap.removeSelectedTile();
			spritePaletteGrid.repaint(paletteMap.getSelection());
		}
    }//GEN-LAST:event_spritePaletteGridKeyPressed

    private void inspectTileMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_inspectTileMenuItemActionPerformed
		tileInspector.setVisible(true);
    }//GEN-LAST:event_inspectTileMenuItemActionPerformed

    private void trajectoryPreviewMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_trajectoryPreviewMenuItemActionPerformed
		new TrajectoryPreview(this, true).setVisible(true);
    }//GEN-LAST:event_trajectoryPreviewMenuItemActionPerformed

    private void multipleEditMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_multipleEditMenuItemActionPerformed
		final String size = JOptionPane.showInputDialog("Taille de la surface à éditer (exemple : 2x3) ?");
		
		final int xIndex = size.indexOf('x');
		if(xIndex > 0) {
			final int columns = Integer.parseInt(size.substring(0, xIndex));
			final int rows = Integer.parseInt(size.substring(xIndex + 1));
			
			final SpanningTileLayer layer = new SpanningTileLayer();
			layer.setSize(columns, rows);
			
			final PaletteMap paletteMap = (PaletteMap) paletteGrid.getTileMap();
			final PaletteReference reference = (PaletteReference) paletteMap.getPalette();
			final EditableImagePalette imagePalette = (EditableImagePalette) project.getPalette(reference.getPaletteIndex());
			
			final Point origin = paletteMap.getSelection();
			
			for(int row = 0; row < rows; row++) {
				for(int column = 0; column < columns; column++) {
					layer.setLayer(imagePalette.getSource(paletteMap.getTileFromPoint(new Point(origin.x + column, origin.y + row))), column, row);
				}
			}
			
			layer.updateSize();
			
			final TileMapEditor editor = new TileMapEditor(this);
			editor.setLayerAndPalette(layer, imagePalette.getColorPalette());
			editor.addActionListener(new ActionListener() {

				@Override
				public void actionPerformed(ActionEvent e) {
					for(int row = 0; row < rows; row++) {
						for(int column = 0; column < columns; column++) {
							imagePalette.refreshSource(paletteMap.getTileFromPoint(new Point(origin.x + column, origin.y + row)));
						}
					}
				}
			});
			editor.setVisible(true);
			mapBackgroundPanel.repaint(mapScrollPane.getViewport().getViewRect());
		}
    }//GEN-LAST:event_multipleEditMenuItemActionPerformed

    private void clearRecentMenuItemActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_clearRecentMenuItemActionPerformed
		final List<String> recents = PreferencesManager.getList(PreferencesManager.RECENT);
		recents.clear();
		
		clearRecentMenuItem.setEnabled(false);
		
		buildRecentMenu();
    }//GEN-LAST:event_clearRecentMenuItemActionPerformed

	private void select(MouseEvent event, Grid grid) {
		final Point point = paletteGrid.getLayerLocation(event.getX(), event.getY());
				
		final PaletteMap paletteMap = (PaletteMap) grid.getTileMap();
		paletteMap.setSelection(point);
		
		grid.requestFocusInWindow();
	}
	
	private int edit(MouseEvent event, Grid grid) {
		if(event.getButton() == MouseEvent.BUTTON1 && event.getClickCount() == 2) {
			final PaletteMap paletteMap = (PaletteMap) grid.getTileMap();
			final int tileIndex = paletteMap.getSelectedTile();
			final Palette palette = paletteMap.getPalette();

			if(tileIndex > -1 && palette.isEditable()) {
				final EditablePalette editablePalette = (EditablePalette) palette;
				final Point selection = paletteMap.getSelection();

				editablePalette.editTile(tileIndex, this);

				grid.repaint(selection);
				mapBackgroundPanel.repaint(mapScrollPane.getViewport().getViewRect());
				
				return tileIndex;
			}
		}
		return -1;
	}
	
	private void refreshScrollMode() {
		boolean hasParallax = false;
		if(project == null || project.getCurrentMap() == null) {
			hasParallax = false;
		} else {
			for(final Layer layer : project.getCurrentMap().getLayers()) {
				hasParallax = hasParallax || layer.getScrollRate() != 1.0;
			}
		}
		
		if(!hasParallax) {
			mapScrollPane.getViewport().setScrollMode(JViewport.BLIT_SCROLL_MODE);
		} else {
			mapScrollPane.getViewport().setScrollMode(JViewport.SIMPLE_SCROLL_MODE);
		}
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton addLayerButton;
    private javax.swing.JToggleButton bucketFillToggleButton;
    private javax.swing.JMenuItem clearRecentMenuItem;
    private javax.swing.JButton devicePreviewButton;
    private javax.swing.JMenuItem editLayerMenuItem;
    private javax.swing.JMenuItem editMapMenuItem;
    private javax.swing.JFileChooser fileChooser;
    private javax.swing.JMenu fileMenu;
    private javax.swing.JToolBar gridToolBar;
    private javax.swing.JMenuItem importMenuItem;
    private javax.swing.JPopupMenu.Separator importSeparator;
    private javax.swing.JMenuItem inspectTileMenuItem;
    private javax.swing.JToolBar.Separator jSeparator1;
    private javax.swing.JButton layerDownButton;
    private javax.swing.JList layerList;
    private javax.swing.JScrollPane layerListScrollPane;
    private fr.rca.mapmaker.editor.undo.LayerMemento layerMemento;
    private javax.swing.JPopupMenu layerPopupMenu;
    private javax.swing.JButton layerUpButton;
    private javax.swing.JPanel mapBackgroundPanel;
    private fr.rca.mapmaker.ui.Grid mapGrid;
    private javax.swing.JList mapList;
    private javax.swing.JScrollPane mapListScrollPane;
    private javax.swing.JPanel mapPanel;
    private javax.swing.JPopupMenu mapPopupMenu;
    private javax.swing.JScrollPane mapScrollPane;
    private javax.swing.JMenuItem multipleEditMenuItem;
    private javax.swing.JMenu openRecentMenu;
    private javax.swing.JPanel paletteBackgroundPanel;
    private fr.rca.mapmaker.ui.Grid paletteGrid;
    private javax.swing.JScrollPane paletteScrollPane;
    private javax.swing.JTabbedPane paletteTabbedPane;
    private javax.swing.JToggleButton penToggleButton;
    private fr.rca.mapmaker.editor.tool.PenTool penTool;
    private fr.rca.mapmaker.model.project.Project project;
    private javax.swing.JMenuItem quitMenuItem;
    private javax.swing.JPopupMenu.Separator quitSeparator;
    private javax.swing.JToggleButton rectangleToggleButton;
    private javax.swing.JButton redoButton;
    private javax.swing.JButton removeLayerButton;
    private javax.swing.JPopupMenu.Separator saveSeparator;
    private javax.swing.JToggleButton selectionToggleButton;
    private javax.swing.JPanel spriteBackgroundPanel;
    private javax.swing.JPanel spriteLayerPanel;
    private fr.rca.mapmaker.ui.Grid spritePaletteGrid;
    private javax.swing.JScrollPane spritePaletteScrollPane;
    private javax.swing.JToolBar spritePaletteToolBar;
    private javax.swing.JPanel spritePanel;
    private javax.swing.JToggleButton spriteToggleButton;
    private fr.rca.mapmaker.editor.tool.SpriteTool spriteTool;
    private fr.rca.mapmaker.editor.TileInspector tileInspector;
    private fr.rca.mapmaker.ui.TileMapListRenderer tileMapListRenderer;
    private javax.swing.JPopupMenu tilePopupMenu;
    private javax.swing.ButtonGroup toolGroup;
    private javax.swing.JMenuItem trajectoryPreviewMenuItem;
    private javax.swing.JButton undoButton;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JLabel zoomPercentLabel;
    private javax.swing.JToolBar.Separator zoomSeparator;
    private javax.swing.JTextField zoomTextField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
