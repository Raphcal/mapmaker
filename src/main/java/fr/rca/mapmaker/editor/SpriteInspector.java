package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import java.util.Collections;
import java.util.List;
import java.util.Set;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpriteInspector extends javax.swing.JDialog {

	private Sprite sprite;
	
	public SpriteInspector() {
	}
	
	/**
	 * Creates new form TileInspector
	 */
	public SpriteInspector(java.awt.Frame parent, boolean modal) {
		super(parent, modal);
		getRootPane().putClientProperty("Window.style", "small");
		initComponents();
	}

	public void setSprite(Sprite sprite) {
		final Sprite oldSprite = this.sprite;
		this.sprite = sprite;
		
		setTitle("Infos sur le sprite " + sprite.getName());
		if(hasAnimation(Animation.RUN)) {
			setAnimation(sprite.get(Animation.RUN));
			
		} else if(hasAnimation(Animation.WALK)) {
			setAnimation(sprite.get(Animation.WALK));
			
		} else {
			setAnimation(sprite.get(Animation.STAND));
		}
		
		firePropertyChange("sprite", oldSprite, sprite);
		typeComboBox.setSelectedIndex(sprite.getType());
	}
	
	public void setSpriteIndex(int index) {
		tileIndexLabel.setText("Sprite n°" + index);
	}
	
	private boolean hasAnimation(String animation) {
		return sprite.contains(new Animation(animation)) &&
				!sprite.get(Animation.RUN).getAnglesWithValue().isEmpty();
	}
	
	public Sprite getSprite() {
		return sprite;
	}
	
	private void setAnimation(Animation animation) {
		animatedGrid.stop();
		
		// Étapes d'animation
		final List<TileLayer> frames;
		
		if(animation == null) {
			frames = Collections.emptyList();
			
		} else {
			final Set<Double> angles = animation.getAnglesWithValue();
			if(angles.contains(0.0)) {
				frames = animation.getFrames(0.0);
			} else if(!angles.isEmpty()) {
				frames = animation.getFrames(angles.iterator().next());
			} else {
				frames = Collections.emptyList();
			}
			
			animatedGrid.setFrequency(animation.getFrequency());
		}
		
		animatedGrid.setFrames(frames);
		animatedGrid.setPalette(sprite.getPalette());
		
		animatedGrid.start();
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

        tileIndexLabel = new javax.swing.JLabel();
        typeLabel = new javax.swing.JLabel();
        hitboxSeparator = new javax.swing.JSeparator();
        typeComboBox = new javax.swing.JComboBox();
        scriptLabel = new javax.swing.JLabel();
        scriptTextField = new javax.swing.JTextField();
        hitboxSeparator1 = new javax.swing.JSeparator();
        hitboxLabel = new javax.swing.JLabel();
        hitboxLabel2 = new javax.swing.JLabel();
        typeLabel1 = new javax.swing.JLabel();
        nameTextField = new javax.swing.JTextField();
        animatedGrid = new fr.rca.mapmaker.ui.AnimatedGrid<TileLayer>();

        setTitle("Infos sur la tuile n°12");
        setBackground(new java.awt.Color(236, 236, 236));
        setMinimumSize(new java.awt.Dimension(240, 0));
        setName("tileInspector"); // NOI18N
        setResizable(false);

        tileIndexLabel.setFont(new java.awt.Font("Lucida Grande", 1, 13)); // NOI18N
        tileIndexLabel.setText("Sprite n°12");

        typeLabel.setFont(typeLabel.getFont().deriveFont(typeLabel.getFont().getSize()-1f));
        typeLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        typeLabel.setText("Type :");
        typeLabel.setToolTipText("");

        typeComboBox.setModel(new javax.swing.DefaultComboBoxModel(new String[] { "Décoration", "Joueur", "Plateforme", "Bonus", "Destructible", "Méchant" }));
        typeComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                typeComboBoxActionPerformed(evt);
            }
        });

        scriptLabel.setFont(scriptLabel.getFont().deriveFont(scriptLabel.getFont().getSize()-1f));
        scriptLabel.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        scriptLabel.setText("Script :");
        scriptLabel.setToolTipText("");

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sprite.scriptFile}"), scriptTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        hitboxLabel.setFont(hitboxLabel.getFont().deriveFont(hitboxLabel.getFont().getSize()-1f));
        hitboxLabel.setText("Général :");

        hitboxLabel2.setFont(hitboxLabel2.getFont().deriveFont(hitboxLabel2.getFont().getSize()-1f));
        hitboxLabel2.setText("Comportement :");

        typeLabel1.setFont(typeLabel1.getFont().deriveFont(typeLabel1.getFont().getSize()-1f));
        typeLabel1.setHorizontalAlignment(javax.swing.SwingConstants.RIGHT);
        typeLabel1.setText("Nom :");
        typeLabel1.setToolTipText("");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${sprite.name}"), nameTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addComponent(hitboxSeparator)
            .addComponent(hitboxSeparator1)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(typeComboBox, 0, 216, Short.MAX_VALUE))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(scriptLabel, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(scriptTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addComponent(typeLabel1, javax.swing.GroupLayout.PREFERRED_SIZE, 78, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(nameTextField))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(animatedGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(tileIndexLabel))
                            .addComponent(hitboxLabel)
                            .addComponent(hitboxLabel2))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(tileIndexLabel)
                    .addComponent(animatedGrid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hitboxSeparator, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(hitboxLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel1)
                    .addComponent(nameTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(typeLabel)
                    .addComponent(typeComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(hitboxSeparator1, javax.swing.GroupLayout.PREFERRED_SIZE, 10, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addGap(0, 0, 0)
                .addComponent(hitboxLabel2)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(scriptLabel)
                    .addComponent(scriptTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void typeComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_typeComboBoxActionPerformed
		sprite.setType(typeComboBox.getSelectedIndex());
    }//GEN-LAST:event_typeComboBoxActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.rca.mapmaker.ui.AnimatedGrid<TileLayer> animatedGrid;
    private javax.swing.JLabel hitboxLabel;
    private javax.swing.JLabel hitboxLabel2;
    private javax.swing.JSeparator hitboxSeparator;
    private javax.swing.JSeparator hitboxSeparator1;
    private javax.swing.JTextField nameTextField;
    private javax.swing.JLabel scriptLabel;
    private javax.swing.JTextField scriptTextField;
    private javax.swing.JLabel tileIndexLabel;
    private javax.swing.JComboBox typeComboBox;
    private javax.swing.JLabel typeLabel;
    private javax.swing.JLabel typeLabel1;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
