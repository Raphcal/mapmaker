package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.HasFunctionHitbox;
import fr.rca.mapmaker.model.map.PaletteMap;
import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.EditableImagePalette;
import fr.rca.mapmaker.model.palette.Palette;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.ui.PalettePicker;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileInspector extends javax.swing.JDialog {

	private Palette palette;
	
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
	
	public void setTile(PalettePicker palettePicker) {
		setTile(palettePicker.getSelectedTile(), palettePicker.getPalette());
	}
	
	public void setTile(PaletteMap paletteMap) {
		setTile(paletteMap.getSelectedTile(), paletteMap.getPalette());
	}
	
	private void setTile(final int tile, Palette palette) {
		setTitle("Infos sur la tuile n°" + tile);
		tileIndexLabel.setText("Tuile n°" + tile);
		
		if(palette instanceof PaletteReference) {
			final PaletteReference reference = (PaletteReference) palette;
			palette = reference.getProject().getPalette(reference.getPaletteIndex());
		}
		
		this.palette = palette;
		
		if(palette instanceof EditableImagePalette) {
			final EditableImagePalette imagePalette = (EditableImagePalette)palette;
			
			final TileLayer source = imagePalette.getSource(tile);
			
			tileGrid.setTileMap(new TileMap(source, imagePalette.getColorPalette()));
			tileAndHitboxGrid.setTileMap(new TileMap(source, imagePalette.getColorPalette()));
			
			tileAndHitboxGrid.setZoom(256.0 / (double)source.getWidth());
		}
		
		final boolean hasFunctionHitbox = palette instanceof HasFunctionHitbox;
		hitboxCheckBox.setVisible(hasFunctionHitbox);
		hitboxLabel.setVisible(hasFunctionHitbox);
		hitboxSeparator.setVisible(hasFunctionHitbox);
		functionLabel.setVisible(hasFunctionHitbox);
		functionTextField.setVisible(hasFunctionHitbox);
		function.setVisible(hasFunctionHitbox);
		
		if(hasFunctionHitbox && palette != null) {
			final String hitbox = ((HasFunctionHitbox)palette).getFunction(palette.getSelectedTile());
			
			hitboxCheckBox.setSelected(hitbox != null);
			functionTextField.setEnabled(hitbox != null);
			
			firePropertyChange("currentFunctionHitbox", null, hitbox);
		}
		
		previewPanel.repaint();
	}

	public void setCurrentFunctionHitbox(String functionHitbox) {
		if(palette instanceof HasFunctionHitbox) {
			final HasFunctionHitbox hasFunctionHitbox = (HasFunctionHitbox) palette;
			
			final String oldHitbox = hasFunctionHitbox.getFunction(palette.getSelectedTile());
			hasFunctionHitbox.setFunction(palette.getSelectedTile(), functionHitbox);
			
			firePropertyChange("currentFunctionHitbox", oldHitbox, functionHitbox);
		}
	}
	
	public String getCurrentFunctionHitbox() {
		if(palette instanceof HasFunctionHitbox) {
			final HasFunctionHitbox hasFunctionHitbox = (HasFunctionHitbox) palette; 
			final String hitbox = hasFunctionHitbox.getFunction(palette.getSelectedTile());
			
			if(hitbox != null) {
				return hitbox;
			}
		}
		
		return "";
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

        hitboxSeparator = new javax.swing.JSeparator();
        tileGrid = new fr.rca.mapmaker.ui.Grid();
        tileIndexLabel = new javax.swing.JLabel();
        passThroughCheckBox = new javax.swing.JCheckBox();
        passThroughCheckBox.putClientProperty("JComponent.sizeVariant", "small");
        hitboxLabel = new javax.swing.JLabel();
        functionLabel = new javax.swing.JLabel();
        functionTextField = new javax.swing.JTextField();
        previewSeparator = new javax.swing.JSeparator();
        previewLabel = new javax.swing.JLabel();
        previewPanel = new javax.swing.JPanel(new fr.rca.mapmaker.ui.LayerLayout(fr.rca.mapmaker.ui.LayerLayout.Disposition.CENTER));
        function = new fr.rca.mapmaker.ui.Function();
        tileAndHitboxGrid = new fr.rca.mapmaker.ui.Grid();
        hitboxCheckBox = new javax.swing.JCheckBox();
        hitboxCheckBox.putClientProperty("JComponent.sizeVariant", "small");

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

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentFunctionHitbox}"), functionTextField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"));
        bindingGroup.addBinding(binding);

        previewLabel.setFont(previewLabel.getFont().deriveFont(previewLabel.getFont().getSize()-1f));
        previewLabel.setText("Aperçu :");

        previewPanel.setBorder(javax.swing.BorderFactory.createLineBorder(new java.awt.Color(197, 197, 197)));
        previewPanel.setPreferredSize(new java.awt.Dimension(256, 256));

        function.setPreferredSize(tileAndHitboxGrid.getPreferredSize());
        function.setSourceHeight(tileAndHitboxGrid.getTileMapHeight());
        function.setSourceWidth(tileAndHitboxGrid.getTileMapWidth());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentFunctionHitbox}"), function, org.jdesktop.beansbinding.BeanProperty.create("function"));
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

        hitboxCheckBox.setText("Hitbox");
        hitboxCheckBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                hitboxCheckBoxActionPerformed(evt);
            }
        });

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
            .addComponent(hitboxSeparator)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(tileGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(hitboxCheckBox)
                            .addComponent(passThroughCheckBox)))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(44, 44, 44)
                        .addComponent(tileIndexLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(hitboxLabel))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(previewLabel)))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
            .addComponent(previewSeparator)
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
                .addComponent(hitboxSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(hitboxLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hitboxCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(functionLabel)
                    .addComponent(functionTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(previewLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(previewPanel, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void hitboxCheckBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_hitboxCheckBoxActionPerformed
		functionTextField.setEnabled(hitboxCheckBox.isSelected());
		function.setVisible(hitboxCheckBox.isSelected());
		
		if(!hitboxCheckBox.isSelected()) {
			setCurrentFunctionHitbox(null);
		}
    }//GEN-LAST:event_hitboxCheckBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.rca.mapmaker.ui.Function function;
    private javax.swing.JLabel functionLabel;
    private javax.swing.JTextField functionTextField;
    private javax.swing.JCheckBox hitboxCheckBox;
    private javax.swing.JLabel hitboxLabel;
    private javax.swing.JSeparator hitboxSeparator;
    private javax.swing.JCheckBox passThroughCheckBox;
    private javax.swing.JLabel previewLabel;
    private javax.swing.JPanel previewPanel;
    private javax.swing.JSeparator previewSeparator;
    private fr.rca.mapmaker.ui.Grid tileAndHitboxGrid;
    private fr.rca.mapmaker.ui.Grid tileGrid;
    private javax.swing.JLabel tileIndexLabel;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
