/*
 * TileMapPropertiesDialog.java
 *
 * Created on 20 mars 2012, 12:16:48
 */
package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.map.TileMap;
import fr.rca.mapmaker.model.palette.PaletteReference;
import fr.rca.mapmaker.model.project.Project;
import java.awt.Color;
import java.util.ResourceBundle;
import javax.swing.JColorChooser;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class TileMapPropertiesDialog extends javax.swing.JDialog {
	private static final ResourceBundle language = ResourceBundle.getBundle("resources/language");

	private boolean confirmed;
	
	/** Creates new form TileMapPropertiesDialog */
	public TileMapPropertiesDialog(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		initComponents();
		
		// Fond blanc
		tileMap.setBackgroundColor(Color.WHITE);
		
		// Ajout d'un calque vide
		final TileLayer emptyTileLayer = new TileLayer(20, 15);
		emptyTileLayer.setName("Calque 1");
		tileMap.add(emptyTileLayer);
	}
	
	public TileMapPropertiesDialog(TileMap tileMap, java.awt.Frame parent, boolean modal) {
		this(parent, modal);
		
		this.tileMap.clear();
		
		this.tileMap.setWidth(tileMap.getWidth());
		this.tileMap.setHeight(tileMap.getHeight());
		this.tileMap.setBackgroundColor(tileMap.getBackgroundColor());
		this.tileMap.addAll(tileMap.getLayers());
		this.tileMap.setPalette(tileMap.getPalette());
	}

	public void setProject(Project project) {
		this.project = project;
		this.paletteComboBox.setModel(project.getPaletteListModel());
		
		// Modification du modèle donc recherche de la palette actuelle
		if(tileMap.getPalette() != null)
			this.paletteComboBox.setSelectedItem(tileMap.getPalette());
		else
			this.paletteComboBox.setSelectedIndex(0);
	}

	public TileMap getTileMap() {
		return tileMap;
	}

	public boolean hasBeenConfirmed() {
		return confirmed;
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

        tileMap = new fr.rca.mapmaker.model.map.TileMap();
        project = new fr.rca.mapmaker.model.project.Project();
        javax.swing.JLabel widthLabel = new javax.swing.JLabel();
        javax.swing.JLabel heightLabel = new javax.swing.JLabel();
        javax.swing.JLabel paletteLabel = new javax.swing.JLabel();
        javax.swing.JLabel backgroundColorLabel = new javax.swing.JLabel();
        paletteComboBox = new javax.swing.JComboBox();
        javax.swing.JTextField backgroundColorField = new javax.swing.JTextField();
        javax.swing.JFormattedTextField widthTextField = new javax.swing.JFormattedTextField();
        javax.swing.JFormattedTextField heightTextField = new javax.swing.JFormattedTextField();
        javax.swing.JButton okButton = new javax.swing.JButton();
        javax.swing.JButton cancelButton = new javax.swing.JButton();
        removeBackgroundColorButton = new javax.swing.JButton();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        java.util.ResourceBundle bundle = java.util.ResourceBundle.getBundle("resources/language"); // NOI18N
        widthLabel.setText(bundle.getString("dialog.map.width")); // NOI18N

        heightLabel.setText(language.getString("dialog.map.height")); // NOI18N

        paletteLabel.setText(language.getString("dialog.map.palette")); // NOI18N

        backgroundColorLabel.setText(language.getString("dialog.map.background")); // NOI18N

        backgroundColorField.setEditable(false);

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tileMap, org.jdesktop.beansbinding.ELProperty.create("${backgroundColor}"), backgroundColorField, org.jdesktop.beansbinding.BeanProperty.create("background"));
        bindingGroup.addBinding(binding);

        backgroundColorField.addMouseListener(new java.awt.event.MouseAdapter() {
            public void mouseClicked(java.awt.event.MouseEvent evt) {
                backgroundColorFieldMouseClicked(evt);
            }
        });

        widthTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tileMap, org.jdesktop.beansbinding.ELProperty.create("${width}"), widthTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        heightTextField.setFormatterFactory(new javax.swing.text.DefaultFormatterFactory(new javax.swing.text.NumberFormatter(new java.text.DecimalFormat("#0"))));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, tileMap, org.jdesktop.beansbinding.ELProperty.create("${height}"), heightTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        okButton.setText(language.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(language.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        removeBackgroundColorButton.setText("X");
        removeBackgroundColorButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                removeBackgroundColorButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING, false)
                            .addComponent(paletteLabel, javax.swing.GroupLayout.Alignment.LEADING, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(widthLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(heightLabel, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(heightTextField)
                            .addComponent(widthTextField)
                            .addComponent(paletteComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(backgroundColorLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(backgroundColorField, javax.swing.GroupLayout.DEFAULT_SIZE, 37, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(removeBackgroundColorButton, javax.swing.GroupLayout.PREFERRED_SIZE, 43, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(okButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(cancelButton)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(widthLabel)
                    .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(heightLabel)
                    .addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(paletteLabel)
                    .addComponent(paletteComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(backgroundColorLabel)
                    .addComponent(backgroundColorField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(removeBackgroundColorButton))
                .addGap(18, 18, 18)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

private void backgroundColorFieldMouseClicked(java.awt.event.MouseEvent evt) {//GEN-FIRST:event_backgroundColorFieldMouseClicked
	final Color newColor = JColorChooser.showDialog(this, language.getString("dialog.map.editbackground"), tileMap.getBackgroundColor());
	
	if(newColor != null)
		tileMap.setBackgroundColor(newColor);
}//GEN-LAST:event_backgroundColorFieldMouseClicked

private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
	
	tileMap.setPalette(new PaletteReference(project, paletteComboBox.getSelectedIndex()));
	
	confirmed = true;
	setVisible(false);
}//GEN-LAST:event_okButtonActionPerformed

private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
	setVisible(false);
}//GEN-LAST:event_cancelButtonActionPerformed

	private void removeBackgroundColorButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_removeBackgroundColorButtonActionPerformed
		tileMap.setBackgroundColor(null);
	}//GEN-LAST:event_removeBackgroundColorButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox paletteComboBox;
    private fr.rca.mapmaker.model.project.Project project;
    private javax.swing.JButton removeBackgroundColorButton;
    private fr.rca.mapmaker.model.map.TileMap tileMap;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
