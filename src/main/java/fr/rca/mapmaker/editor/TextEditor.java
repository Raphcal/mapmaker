package fr.rca.mapmaker.editor;

import fr.rca.mapmaker.model.map.TileLayer;
import fr.rca.mapmaker.model.project.Project;
import fr.rca.mapmaker.model.sprite.Sprite;
import fr.rca.mapmaker.model.sprite.SpriteType;
import java.awt.Dimension;
import java.awt.Point;
import java.nio.CharBuffer;
import java.util.Collections;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import javax.swing.ComboBoxModel;
import javax.swing.DefaultComboBoxModel;
import javax.swing.JComboBox;
import javax.swing.JTextField;
import javax.swing.SwingWorker;
import javax.swing.text.JTextComponent;
import lombok.Getter;

/**
 *
 * @author Raphaël Calabro (ddaeke-github at yahoo.fr)
 */
public class TextEditor extends javax.swing.JDialog {

	@Getter
	private Project project;

	private List<Sprite> fonts;
	private DefaultComboBoxModel<String> fontModel = new DefaultComboBoxModel<>();

	private Sprite selectedFont;
	private Map<String, TileLayer> fontData;
	private int leading;

	private TileLayer layer;

	private SwingWorker worker;

	/**
	 * Creates new form TextEditor
	 */
	public TextEditor(java.awt.Frame parent) {
		super(parent);
		initComponents();
	}

	public void setProject(Project project) {
		this.project = project;
		this.fonts = project.getSprites().stream()
				.filter(sprite -> sprite.getType() == SpriteType.FONT.ordinal())
				.collect(Collectors.toList());
		fontModel.removeAllElements();
		if (!fonts.isEmpty()) {
			fontModel.addAll(fonts.stream().map(sprite -> sprite.getName()).collect(Collectors.toList()));
			fontModel.setSelectedItem(fonts.get(0).getName());
			selectFont(fonts.get(0));
			drawInBackground(textArea.getText());
		}
	}

	public ComboBoxModel<String> getFontModel() {
		return fontModel;
	}

	private void selectFont(Sprite font) {
		if (font == selectedFont) {
			return;
		}
		this.selectedFont = font;

		final HashMap<String, TileLayer> fontData = new HashMap<>();
		font.getAnimations().stream()
				.flatMap(animation -> animation.getFrames().entrySet().stream())
				.flatMap(frames -> frames.getValue().stream())
				.forEach(tileLayer -> fontData.put(tileLayer.getName(), tileLayer));
		this.fontData = fontData;

		drawInBackground(textArea.getText());
	}

	private Dimension textDimension(String text, Sprite font, Map<String, TileLayer> fontData, int leading) {
		final Dimension dimension = new Dimension();
		dimension.height = font.getHeight() + (int)CharBuffer.wrap(text.toCharArray()).chars()
				.filter(c -> c == '\n')
				.count() * (font.getHeight() + leading);
		dimension.width = text.lines()
				.mapToInt(line -> (int)CharBuffer.wrap(line.toCharArray()).chars()
					.mapToObj(c -> fontData.get(c == ' ' ? "space" : Character.toString(c)))
					.mapToInt(tileLayer -> tileLayer != null ? tileLayer.getWidth() : font.getWidth())
					.sum())
				.max()
				.orElse(font.getWidth());
		return dimension;
	}

	private TileLayer draw(String text, Sprite font, Map<String, TileLayer> fontData, int leading) {
		final Dimension dimension = textDimension(text, font, fontData, leading);
		final TileLayer result = new TileLayer(dimension.width, dimension.height);
		final Iterator<String> lines = text.lines().iterator();
		int y = 0;
		while (lines.hasNext()) {
			final String line = lines.next();
			int x = 0;
			for (char c : line.toCharArray()) {
				TileLayer characterLayer = fontData.get(c == ' ' ? "space" : Character.toString(c));
				if (characterLayer != null) {
					result.mergeAtPoint(characterLayer, new Point(x, y));
					x += characterLayer.getWidth();
				} else {
					x += font.getWidth();
				}
			}
			y += font.getHeight() + leading;
		}
		return result;
	}

	private void drawInBackground(final String text) {
		if (worker != null) {
			worker.cancel(true);
		}
		if (text == null || selectedFont == null || fontData == null) {
			return;
		}
		worker = new SwingWorker() {
			private TileLayer workerResult;
			
			@Override
			protected Object doInBackground() throws Exception {
				workerResult = draw(text, selectedFont, fontData, leading);
				worker = null;
				return null;
			}
			
			@Override
			protected void done() {
				if (workerResult == null) {
					// Tâche annulée.
					return;
				}
				layer.restoreData(workerResult);
				grid.setPreferredSize(new Dimension(layer.getWidth(), layer.getHeight()));
				pack();
			}
		};
		worker.execute();
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        final javax.swing.JLabel fontLabel = new javax.swing.JLabel();
        final javax.swing.JComboBox<String> fontComboBox = new javax.swing.JComboBox<>();
        grid = new fr.rca.mapmaker.ui.Grid();
        final java.awt.Button copyButton = new java.awt.Button();
        final javax.swing.JScrollPane textScrollPane = new javax.swing.JScrollPane();
        textArea = new javax.swing.JTextArea();

        setTitle(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("resources/language").getString("dialog.rendertext.title"), new Object[] {})); // NOI18N
        setModal(true);

        fontLabel.setText(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("resources/language").getString("dialog.font.select"), new Object[] {})); // NOI18N

        fontComboBox.setModel(getFontModel());
        fontComboBox.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                fontComboBoxActionPerformed(evt);
            }
        });

        this.layer = (TileLayer)grid.getTileMap().getLayers().get(0);

        copyButton.setLabel(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("resources/language").getString("menu.edit.copy"), new Object[] {})); // NOI18N
        copyButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                copyButtonActionPerformed(evt);
            }
        });

        textArea.setColumns(20);
        textArea.setRows(5);
        textArea.setText("The quick brown fox jumps\nover the lazy dog.");
        textArea.addKeyListener(new java.awt.event.KeyAdapter() {
            public void keyReleased(java.awt.event.KeyEvent evt) {
                textAreaKeyReleased(evt);
            }
        });
        textScrollPane.setViewportView(textArea);

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addGroup(layout.createSequentialGroup()
                                .addComponent(fontLabel)
                                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                                .addComponent(fontComboBox, 0, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                            .addComponent(textScrollPane, javax.swing.GroupLayout.DEFAULT_SIZE, 311, Short.MAX_VALUE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(grid, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                            .addComponent(fontLabel)
                            .addComponent(fontComboBox, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(textScrollPane))
                    .addComponent(grid, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(copyButton, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addContainerGap())
        );
    }// </editor-fold>//GEN-END:initComponents

    private void fontComboBoxActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_fontComboBoxActionPerformed
		final JComboBox comboBox = (JComboBox) evt.getSource();
		int index = comboBox.getSelectedIndex();
		if (index >= 0 && index < fonts.size()) {
			this.selectFont(fonts.get(index));
		}
    }//GEN-LAST:event_fontComboBoxActionPerformed

    private void copyButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_copyButtonActionPerformed
		SpriteEditor.copy(Collections.singleton(layer));
		TileMapEditor.copy(layer);
    }//GEN-LAST:event_copyButtonActionPerformed

    private void textAreaKeyReleased(java.awt.event.KeyEvent evt) {//GEN-FIRST:event_textAreaKeyReleased
		final JTextComponent textField = (JTextComponent) evt.getComponent();
		final String text = textField.getText();
		drawInBackground(text);
    }//GEN-LAST:event_textAreaKeyReleased


    // Variables declaration - do not modify//GEN-BEGIN:variables
    private fr.rca.mapmaker.ui.Grid grid;
    private javax.swing.JTextArea textArea;
    // End of variables declaration//GEN-END:variables
}
