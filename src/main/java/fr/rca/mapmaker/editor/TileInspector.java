package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import java.awt.Point;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileInspector extends javax.swing.JDialog {

	public TileInspector() {
	}
	
	/**
	 * Creates new form TileInspector
	 */
	public TileInspector(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		getRootPane().putClientProperty("Window.style", "small");
		initComponents();
		
	}
	
	public void setTile(PaletteMap paletteMap) {
		final int tile = paletteMap.getSelectedTile();
		setTitle("Infos sur la tuile n°" + tile);
		tileIndexLabel.setText("Tuile n°" + tile);
		
		Palette palette = paletteMap.getPalette();
		if(palette instanceof PaletteReference) {
			final PaletteReference reference = (PaletteReference) palette;
			palette = reference.getProject().getPalette(reference.getPaletteIndex());
		}
		
		if(palette instanceof EditableImagePalette) {
			final EditableImagePalette imagePalette = (EditableImagePalette)palette;
			
			tileGrid.setTileMap(new TileMap(imagePalette.getSource(tile), imagePalette.getColorPalette()));
			tileAndHitboxGrid.setTileMap(new TileMap(imagePalette.getSource(tile), imagePalette.getColorPalette()));
		}
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

        jSeparator1 = new javax.swing.JSeparator();
        tileGrid = new fr.rca.mapmaker.ui.Grid();
        tileIndexLabel = new javax.swing.JLabel();
        passThroughCheckBox = new javax.swing.JCheckBox();
        passThroughCheckBox.putClientProperty("JComponent.sizeVariant", "small");
        hitboxLabel = new javax.swing.JLabel();
        functionLabel = new javax.swing.JLabel();
        functionTextField = new javax.swing.JTextField();
        jSeparator2 = new javax.swing.JSeparator();
        previewLabel = new javax.swing.JLabel();
        previewPanel = new javax.swing.JPanel(new fr.rca.mapmaker.ui.LayerLayout(fr.rca.mapmaker.ui.LayerLayout.Disposition.CENTER));
        function = new fr.rca.mapmaker.ui.Function();
        tileAndHitboxGrid = new fr.rca.mapmaker.ui.Grid();
        hitBoxCheckBox = new javax.swing.JCheckBox();

        setTitle("Infos sur la tuile n°12");
        setBackground(new java.awt.Color(236, 236, 236));
        setMinimumSize(new java.awt.Dimension(240, 0));
        setName("tileInspector"); // NOI18N
        setResizable(false);

        tileGrid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));

        tileIndexLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        tileIndexLabel.setText("Tuile n°12");

        passThroughCheckBox.setText("Traversable");

        hitboxLabel.setFont(hitboxLabel.getFont().deriveFont(hitboxLabel.getFont().getSize()-1f));
        hitboxLabel.setText("Hitbox :");

        functionLabel.setFont(functionLabel.getFont().deriveFont(functionLabel.getFont().getSize()-1f));
        functionLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        functionLabel.setText("f(x) :");
        functionLabel.setToolTipText("");

        functionTextField.setFont(functionTextField.getFont().deriveFont(functionTextField.getFont().getSize()-1f));
        functionTextField.setText("0");

        previewLabel.setFont(previewLabel.getFont().deriveFont(previewLabel.getFont().getSize()-1f));
        previewLabel.setText("Aperçu :");

        previewPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(197, 197, 197)));

        function.setPreferredSize(tileAndHitboxGrid.getPreferredSize());
        function.setSourceHeight(tileAndHitboxGrid.getTileMapHeight());
        function.setSourceWidth(tileAndHitboxGrid.getTileMapWidth());

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, functionTextField, org.jdesktop.beansbinding.ELProperty.create("${text}"), function, org.jdesktop.beansbinding.BeanProperty.create("function"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tileAndHitboxGrid, org.jdesktop.beansbinding.ELProperty.create("${preferredSize}"), function, org.jdesktop.beansbinding.BeanProperty.create("preferredSize"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tileAndHitboxGrid, org.jdesktop.beansbinding.ELProperty.create("${tileMapHeight}"), function, org.jdesktop.beansbinding.BeanProperty.create("sourceHeight"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tileAndHitboxGrid, org.jdesktop.beansbinding.ELProperty.create("${tileMapWidth}"), function, org.jdesktop.beansbinding.BeanProperty.create("sourceWidth"));
        bindingGroup.addBinding(binding);

        previewPanel.add(function);

        tileAndHitboxGrid.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(0, 0, 0)));
        tileAndHitboxGrid.setZoom(4.0);
        previewPanel.add(tileAndHitboxGrid);

        hitBoxCheckBox.setText("Hitbox");

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGap(6, 6, 6)
                .addComponent(previewPanel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGap(6, 6, 6))
            .addGroup(layout.createSequentialGroup()
                .addComponent(functionLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(functionTextField)
                .addContainerGap())
            .addComponent(jSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tileGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hitBoxCheckBox)
                            .addComponent(passThroughCheckBox)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(tileIndexLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(hitboxLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(previewLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(jSeparator2)
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tileGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(tileIndexLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(passThroughCheckBox)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hitboxLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hitBoxCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(functionLabel)
                    .addComponent(functionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(jSeparator2, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater(new Runnable() {
			public void run() {
				final EditableImagePalette palette = new EditableImagePalette(32, 4);
				final PaletteMap paletteMap = new PaletteMap(palette, 4);
				paletteMap.setSelection(new Point(0, 0));
				
				TileInspector dialog = new TileInspector(new javax.swing.JFrame(), true);
				dialog.setTile(paletteMap);
				dialog.pack();
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
    private fr.rca.mapmaker.ui.Function function;
    private javax.swing.JLabel functionLabel;
    private javax.swing.JTextField functionTextField;
    private javax.swing.JCheckBox hitBoxCheckBox;
    private javax.swing.JLabel hitboxLabel;
    private javax.swing.JSeparator jSeparator1;
    private javax.swing.JSeparator jSeparator2;
    private javax.swing.JCheckBox passThroughCheckBox;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JPanel previewPanel;
    private fr.rca.mapmaker.ui.Grid tileAndHitboxGrid;
    private fr.rca.mapmaker.ui.Grid tileGrid;
    private javax.swing.JLabel tileIndexLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
