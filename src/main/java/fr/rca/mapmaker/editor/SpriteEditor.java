
package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.sprite.Animation;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.ui.GridList;
import java.awt.Component;
import java.awt.Font;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.ResourceBundle;
import javax.swing.DefaultComboBoxModel;
import javax.swing.DefaultListCellRenderer;
import javax.swing.JList;
import javax.swing.JOptionPane;

/**
 *
 * @author Raphaël Calabro (rcalabro@ideia.fr)
 */
public class SpriteEditor extends javax.swing.JDialog {
	private static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("resources/language");
	private final static List<TileLayer> PASTEBOARD = new ArrayList<TileLayer>();
	
	private final List<ActionListener> actionListeners = new ArrayList<ActionListener>();
	
	private Sprite editedSprite;
	private Animation currentAnimation;
	
	/**
	 * Creates new form SpriteDialog
	 * @param parent Fenêtre parente
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
		final int oldFrequency = getCurrentFrequency();
		
		if(currentAnimation != null) {
			currentAnimation.setFrequency(frequency);
		}
		
		firePropertyChange("currentFrequency", oldFrequency, getCurrentFrequency());
	}
	
	public boolean isAnimationLooping() {
		if(currentAnimation != null) {
			return currentAnimation.isLooping();
		} else {
			return false;
		}
	}
	
	public void setAnimationLooping(boolean looping) {
		if(currentAnimation != null) {
			final boolean oldLooping = currentAnimation.isLooping();
			currentAnimation.setLooping(looping);
			firePropertyChange("animationLooping", oldLooping, looping);
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
        heightTextField = new javax.swing.JTextField();
        sizeByLabel = new javax.swing.JLabel();
        loopCheckBox = new javax.swing.JCheckBox();
        zoomTextField = new javax.swing.JTextField();
        zoomPercentLabel = new javax.swing.JLabel();
        zoomLabel = new javax.swing.JLabel();
        autoRotateButton = new javax.swing.JButton();
        copyButton = new javax.swing.JButton();
        pasteButton = new javax.swing.JButton();

        setTitle("Sprite");

        sizeLabel.setText("Taille :");

        widthTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        widthTextField.setMinimumSize(new java.awt.Dimension(46, 28));

        org.jdesktop.beansbinding.Binding binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${width}"), widthTextField, org.jdesktop.beansbinding.BeanProperty.create("text_ON_ACTION_OR_FOCUS_LOST"), "gridList.gridWidth");
        bindingGroup.addBinding(binding);

        tileLayerList.setOrientation(fr.rca.mapmaker.ui.Orientation.HORIZONTAL);
        tileLayerList.setPalette(sprite.getPalette());

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${height}"), tileLayerList, org.jdesktop.beansbinding.BeanProperty.create("elementHeight"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${width}"), tileLayerList, org.jdesktop.beansbinding.BeanProperty.create("elementWidth"));
        bindingGroup.addBinding(binding);

        tileLayerList.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                tileLayerListActionPerformed(evt);
            }
        });
        tileLayerList.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyPressed(java.awt.event.KeyEvent evt) {
                tileLayerListKeyPressed(evt);
            }
        });
        gridScrollPane.setViewportView(tileLayerList);

        cancelButton.setText(LANGUAGE.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        okButton.setText(LANGUAGE.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        animationComboBox.setModel(animationComboBoxModel);
        animationComboBox.setRenderer(new DefaultListCellRenderer() {

            @Override
            public Component getListCellRendererComponent(JList<?> list, Object value, int index, boolean isSelected, boolean cellHasFocus) {
                super.getListCellRendererComponent(list, value, index, isSelected, cellHasFocus);

                if(editedSprite != null) {
                    final Animation animation = (Animation) value;

                    if(editedSprite.contains(animation) && !editedSprite.get(animation.getName()).getAnglesWithValue().isEmpty()) {
                        setFont(list.getFont().deriveFont(Font.BOLD));
                    } else {
                        setFont(list.getFont().deriveFont(Font.PLAIN));
                    }
                }

                return this;
            }
        });
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

        animationPreview.setZoom(4.0);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${height}"), animationPreview, org.jdesktop.beansbinding.BeanProperty.create("frameHeight"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${width}"), animationPreview, org.jdesktop.beansbinding.BeanProperty.create("frameWidth"));
        bindingGroup.addBinding(binding);
        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentFrequency}"), animationPreview, org.jdesktop.beansbinding.BeanProperty.create("frequency"));
        bindingGroup.addBinding(binding);

        animationPreview.addComponentListener(new java.awt.event.ComponentAdapter() {
            public void componentResized(java.awt.event.ComponentEvent evt) {
                animationPreviewComponentResized(evt);
            }
        });

        frequencyLabel.setText("Vitesse : ");

        frequencyTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        frequencyTextField.setMinimumSize(new java.awt.Dimension(46, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${currentFrequency}"), frequencyTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        heightTextField.setHorizontalAlignment(javax.swing.JTextField.RIGHT);
        heightTextField.setMinimumSize(new java.awt.Dimension(46, 28));

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, sprite, org.jdesktop.beansbinding.ELProperty.create("${height}"), heightTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        sizeByLabel.setText("x");

        loopCheckBox.setText("Jouer en boucle");

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, this, org.jdesktop.beansbinding.ELProperty.create("${animationLooping}"), loopCheckBox, org.jdesktop.beansbinding.BeanProperty.create("selected"));
        bindingGroup.addBinding(binding);

        binding = org.jdesktop.beansbinding.Bindings.createAutoBinding(org.jdesktop.beansbinding.AutoBinding.UpdateStrategy.READ_WRITE, animationPreview, org.jdesktop.beansbinding.ELProperty.create("${zoomAsInteger}"), zoomTextField, org.jdesktop.beansbinding.BeanProperty.create("text"));
        bindingGroup.addBinding(binding);

        zoomPercentLabel.setText("%");

        zoomLabel.setText("Zoom :");

        autoRotateButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/tool_rotate.png"))); // NOI18N
        autoRotateButton.setPreferredSize(new java.awt.Dimension(32, 32));
        autoRotateButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                autoRotateButtonActionPerformed(evt);
            }
        });

        copyButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/copy.png"))); // NOI18N
        copyButton.setPreferredSize(new java.awt.Dimension(32, 32));
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        pasteButton.setIcon(new javax.swing.ImageIcon(getClass().getResource("/resources/paste.png"))); // NOI18N
        pasteButton.setPreferredSize(new java.awt.Dimension(32, 32));
        pasteButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                pasteButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(0, 0, 0)
                        .addComponent(pasteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(autoRotateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton))
                    .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                        .addContainerGap()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(animationLabel)
                            .addComponent(frequencyLabel)
                            .addComponent(sizeLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(animationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(frequencyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 52, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(sizeByLabel)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, 46, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.TRAILING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(directionLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(directionChooser, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(zoomLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(zoomTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(zoomPercentLabel))))
                    .addGroup(layout.createSequentialGroup()
                        .addContainerGap()
                        .addComponent(gridScrollPane))
                    .addGroup(layout.createSequentialGroup()
                        .addGap(6, 6, 6)
                        .addComponent(animationPreview, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addGap(258, 283, Short.MAX_VALUE)))
                .addContainerGap())
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(loopCheckBox)
                .addContainerGap(javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(javax.swing.GroupLayout.Alignment.TRAILING, layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(animationPreview, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                        .addComponent(zoomTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                        .addComponent(zoomPercentLabel)
                        .addComponent(zoomLabel)))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(sizeLabel)
                            .addComponent(widthTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(heightTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                            .addComponent(sizeByLabel)
                            .addComponent(directionLabel))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(animationLabel)
                            .addComponent(animationComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(frequencyLabel)
                            .addComponent(frequencyTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                    .addComponent(directionChooser, javax.swing.GroupLayout.Alignment.TRAILING, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(loopCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(gridScrollPane)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton)
                    .addComponent(autoRotateButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(pasteButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
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
		final List<TileLayer> layers = new ArrayList<TileLayer>(tileLayerList.getElements());
		
		if(GridList.ADD_COMMAND.equals(evt.getActionCommand())) {
			layers.add(new TileLayer(sprite.getWidth(), sprite.getHeight()));
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
				
				directionChooser.setAnglesWithValue(currentAnimation.getAnglesWithValue());
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

    private void tileLayerListKeyPressed(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_tileLayerListKeyPressed
		if(evt.getKeyCode() == KeyEvent.VK_DELETE || evt.getKeyCode() == KeyEvent.VK_BACK_SPACE) {
			tileLayerList.removeSelectedElement();
			directionChooser.setAnglesWithValue(currentAnimation.getAnglesWithValue());
		}
		
		if((evt.getModifiersEx() & (KeyEvent.CTRL_DOWN_MASK | KeyEvent.META_DOWN_MASK)) != 0) {
			if(evt.getExtendedKeyCode() == KeyEvent.VK_C) {
				copyButtonActionPerformed(null);
				
			} else if(evt.getExtendedKeyCode() == KeyEvent.VK_P) {
				pasteButtonActionPerformed(null);
			}
		}
    }//GEN-LAST:event_tileLayerListKeyPressed

    private void animationPreviewComponentResized(java.awt.event.ComponentEvent evt) {//GEN-FIRST:event_animationPreviewComponentResized
		validate();
		revalidate();
		pack();
    }//GEN-LAST:event_animationPreviewComponentResized

    private void autoRotateButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_autoRotateButtonActionPerformed
		final String answer = JOptionPane.showInputDialog("Rotation à appliquer à chaque étape ?");
		try {
			final double step = Double.parseDouble(answer);
			
			final List<TileLayer> frames = new ArrayList<TileLayer>(getCurrentAnimation());
			
			for(double angle = step; angle < 360; angle += step) {
				for(final TileLayer frame : frames) {
					final TileLayer rotatedFrame = new TileLayer(frame.getWidth(), frame.getHeight());
					rotatedFrame.restoreData(frame.copyData(), null);
					
					if(((int)angle) % 90 == 0) {
						rotatedFrame.rotate90(((int)angle) / 90);
					} else {
						rotatedFrame.rotate(angle * Math.PI / 180.0);
					}
					
					tileLayerList.add(rotatedFrame);
				}
			}
			
		} catch(NumberFormatException e) {
			// Ignoré.
		}
    }//GEN-LAST:event_autoRotateButtonActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
		PASTEBOARD.clear();
		
		for(final TileLayer layer : tileLayerList.getSelection()) {
			final TileLayer copy = new TileLayer(layer.getWidth(), layer.getHeight());
			copy.restoreData(layer.copyData(), null);
			
			PASTEBOARD.add(copy);
		}
    }//GEN-LAST:event_copyButtonActionPerformed

    private void pasteButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_pasteButtonActionPerformed
		for(final TileLayer layer : PASTEBOARD) {
			final TileLayer copy = new TileLayer(layer.getWidth(), layer.getHeight());
			copy.restoreData(layer.copyData(), null);
			
			tileLayerList.add(copy);
		}
    }//GEN-LAST:event_pasteButtonActionPerformed

	private void animationChanged() {
		final int oldFrequency = getCurrentFrequency();
		final boolean oldLooping = isAnimationLooping();
		
		updateAnimation();
		
		final List<TileLayer> tiles = getCurrentAnimation();
		tileLayerList.setElements(tiles);
		animationPreview.setFrames(tiles);
		
		if(currentAnimation != null) {
			directionChooser.setAnglesWithValue(currentAnimation.getAnglesWithValue());
		}
		
		firePropertyChange("currentFrequency", oldFrequency, getCurrentFrequency());
		firePropertyChange("animationLooping", oldLooping, isAnimationLooping());
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
	
    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JComboBox<Animation> animationComboBox;
    private javax.swing.DefaultComboBoxModel<Animation> animationComboBoxModel;
    private javax.swing.JLabel animationLabel;
    private fr.rca.mapmaker.ui.AnimatedGrid<TileLayer> animationPreview;
    private javax.swing.JButton autoRotateButton;
    private javax.swing.JButton cancelButton;
    private javax.swing.JButton copyButton;
    private fr.rca.mapmaker.ui.DirectionChooser directionChooser;
    private javax.swing.JLabel directionLabel;
    private javax.swing.JLabel frequencyLabel;
    private javax.swing.JTextField frequencyTextField;
    private javax.swing.JScrollPane gridScrollPane;
    private javax.swing.JTextField heightTextField;
    private javax.swing.JCheckBox loopCheckBox;
    private javax.swing.JButton okButton;
    private javax.swing.JButton pasteButton;
    private javax.swing.JLabel sizeByLabel;
    private javax.swing.JLabel sizeLabel;
    private fr.rca.mapmaker.model.sprite.Sprite sprite;
    private fr.rca.mapmaker.ui.TileLayerList tileLayerList;
    private javax.swing.JTextField widthTextField;
    private javax.swing.JLabel zoomLabel;
    private javax.swing.JLabel zoomPercentLabel;
    private javax.swing.JTextField zoomTextField;
    private org.jdesktop.beansbinding.BindingGroup bindingGroup;
    // End of variables declaration//GEN-END:variables
}
