
package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.ui.GridList;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import javax.swing.DefaultComboBoxModel;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpriteEditor extends javax.swing.JDialog {
	
	private final ArrayList<ActionListener> actionListeners = new ArrayList<ActionListener>();
	
	private Sprite editedSprite;
	private Animation currentAnimation;
	
	/**
	 * Creates new form SpriteDialog
	 */
	public SpriteEditor(java.awt.Frame parent) {
		super(parent, true);
		initComponents();
	}

	public void setSprite(Sprite sprite) {
		this.editedSprite = sprite;
		this.sprite.morphTo(sprite);
		this.tileLayerList.setPalette(sprite.getPalette());
		
		this.sprite.clear();
		this.currentAnimation = null;
		
		animationChanged();
	}
	
	private void updateAnimation() {
		final Animation animation = (Animation) animationComboBoxModel.getSelectedItem();
		if(animation != null) {
			final String name = animation.getName();
			
			if(editedSprite != null && !sprite.contains(animation) && editedSprite.contains(animation)) {
				sprite.getAnimations().add(editedSprite.get(name).copy());
			}
			
			currentAnimation = sprite.get(name);
			
		} else {
			currentAnimation = null;
		}
	}

	public List<TileLayer> getCurrentAnimation() {
		if(currentAnimation != null) {
			return currentAnimation.getOrCreateFrames(directionChooser.getDirection());
			
		} else {
			return Collections.<TileLayer>emptyList();
		}
	}
	
	public int getCurrentFrequency() {
		if(currentAnimation != null) {
			return currentAnimation.getFrequency();
			
		} else {
			return 24;
		}
	}
	
	public void setCurrentFrequency(int frequency) {
		if(currentAnimation != null) {
			currentAnimation.setFrequency(frequency);
		}
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

        sprite = new fr.rca.mapmaker.model.sprite.Sprite();
        animationComboBoxModel = new DefaultComboBoxModel<Animation>(Animation.getDefaultAnimations());
        sizeLabel = new javax.swing.JLabel();
        widthTextField = new javax.swing.JTextField();
        gridScrollPane = new javax.swing.JScrollPane();
        tileLayerList = new fr.rca.mapmaker.ui.TileLayerList();
        cancelButton = new javax.swing.JButton();
        okButton = new javax.swing.JButton();
        animationComboBox = new javax.swing.JComboBox<Animation>();
        animationLabel = new javax.swing.JLabel();
        directionLabel = new javax.swing.JLabel();
        directionChooser = new fr.rca.mapmaker.ui.DirectionChooser();
        animationPreview = new fr.rca.mapmaker.ui.AnimatedGrid<TileLayer>();
        animationPreview.start();
        frequencyLabel = new javax.swing.JLabel();
        frequencyTextField = new javax.swing.JTextField();

        setTitle("Sprite");

        sizeLabel.setText("Taille :");

        widthTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        widthTextField.setMinimumSize(new java.awt.Dimension(46, 28));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${size}"), widthTextField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "gridList.gridWidth");
        bindingGroup.addBinding(binding);

        tileLayerList.setOrientation(fr.rca.mapmaker.ui.Orientation.HORIZONTAL);
        tileLayerList.setPalette(sprite.getPalette());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${size}"), tileLayerList, org.jdesktop.beansbinding.BeanProperty.create("gridSize"));
        bindingGroup.addBinding(binding);

        tileLayerList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileLayerListActionPerformed(evt);
            }
        });
        gridScrollPane.setViewportView(tileLayerList);

        cancelButton.setText("Annuler");
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText("OK");
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        animationComboBox.setModel(animationComboBoxModel);
        animationComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                animationComboBoxActionPerformed(evt);
            }
        });

        animationLabel.setText("Animation : ");

        directionLabel.setText("Direction :");

        directionChooser.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                directionChooserActionPerformed(evt);
            }
        });

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentFrequency}"), animationPreview, org.jdesktop.beansbinding.BeanProperty.create("frequency"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${size}"), animationPreview, org.jdesktop.beansbinding.BeanProperty.create("layerSize"));
        bindingGroup.addBinding(binding);

        frequencyLabel.setText("Vitesse : ");

        frequencyTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyTextField.setMinimumSize(new java.awt.Dimension(46, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentFrequency}"), frequencyTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(animationLabel)
                            .addComponent(sizeLabel)
                            .addComponent(frequencyLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING, false)
                            .addComponent(widthTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(animationComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                            .addComponent(frequencyTextField, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                        .addComponent(animationPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, 157, Short.MAX_VALUE)
                        .addComponent(directionLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(directionChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(gridScrollPane)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(sizeLabel)
                                    .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                    .addComponent(directionLabel))
                                .addGap(10, 10, 10)
                                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                                    .addComponent(animationLabel)
                                    .addComponent(animationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                            .addComponent(animationPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(frequencyLabel)
                            .addComponent(frequencyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(directionChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.UNRELATED)
                .addComponent(gridScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 101, Short.MAX_VALUE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        bindingGroup.bind();

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		setVisible(false);
		animationPreview.stop();
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void tileLayerListActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_tileLayerListActionPerformed
		final ArrayList<TileLayer> layers = new ArrayList<TileLayer>(tileLayerList.getElements());
		
		if(GridList.ADD_COMMAND.equals(evt.getActionCommand())) {
			layers.add(new TileLayer(tileLayerList.getGridSize(), tileLayerList.getGridSize()));
		}
		final int index = evt.getID();
		
		final TileMapEditor editor = new TileMapEditor(null);
		editor.setLayers(layers, index, sprite.getPalette());
		editor.addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				if(layers.size() > tileLayerList.getElements().size()) {
					tileLayerList.add(layers.get(index));
				} else {
					tileLayerList.updateElement(index);
				}
			}
		});
		editor.setVisible(true);
    }//GEN-LAST:event_tileLayerListActionPerformed

    private void animationComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_animationComboBoxActionPerformed
		animationChanged();
    }//GEN-LAST:event_animationComboBoxActionPerformed

    private void directionChooserActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_directionChooserActionPerformed
		animationChanged();
    }//GEN-LAST:event_directionChooserActionPerformed

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
        setVisible(false);
		animationPreview.stop();
		
		editedSprite.merge(sprite);
		
		fireActionPerformed();
    }//GEN-LAST:event_okButtonActionPerformed

	private void animationChanged() {
		final int oldFrequency = getCurrentFrequency();
		
		updateAnimation();
		
		final List<TileLayer> tiles = getCurrentAnimation();
		tileLayerList.setElements(tiles);
		animationPreview.setFrames(tiles);
		
		firePropertyChange("currentFrequency", oldFrequency, getCurrentFrequency());
	}
	
	public void addActionListener(ActionListener listener) {
		actionListeners.add(listener);
	}
	
	public void removeActionListener(ActionListener listener) {
		actionListeners.remove(listener);
	}
	
	protected void fireActionPerformed() {
		for(final ActionListener listener : actionListeners) {
			listener.actionPerformed(new ActionEvent(this, 0, "SPRITE_EDITED"));
		}
	}
	
	/**
	 * @param args the command line arguments
	 */
	public static void main(String args[]) {
		/* Create and display the dialog */
		java.awt.EventQueue.invokeLater(new Runnable() {
			@Override
			public void run() {
				SpriteEditor dialog = new SpriteEditor(new javax.swing.JFrame());
				dialog.addWindowListener(new java.awt.event.WindowAdapter() {
					@Override
					public void windowClosing(java.awt.event.WindowEvent e) {
						System.exit(0);
					}
				});
				dialog.cancelButton.addActionListener(new ActionListener() {

					@Override
					public void actionPerformed(ActionEvent e) {
						System.exit(0);
					}
				});
				dialog.setVisible(true);
			}
		});
	}
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Animation> animationComboBox;
    private javax.swing.DefaultComboBoxModel<Animation> animationComboBoxModel;
    private javax.swing.JLabel animationLabel;
    private fr.rca.mapmaker.ui.AnimatedGrid<TileLayer> animationPreview;
    private javax.swing.JButton cancelButton;
    private fr.rca.mapmaker.ui.DirectionChooser directionChooser;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JLabel frequencyLabel;
    private javax.swing.JTextField frequencyTextField;
    private javax.swing.JScrollPane gridScrollPane;
    private javax.swing.JButton okButton;
    private javax.swing.JLabel sizeLabel;
    private fr.rca.mapmaker.model.sprite.Sprite sprite;
    private fr.rca.mapmaker.ui.TileLayerList tileLayerList;
    private javax.swing.JTextField widthTextField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
