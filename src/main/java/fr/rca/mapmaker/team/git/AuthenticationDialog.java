package fr.rca.mapmaker.team.git;

import fr.rca.mapmaker.preferences.PreferencesManager;
import java.io.File;
import java.util.Optional;
import java.util.ResourceBundle;
import javax.swing.JButton;
import javax.swing.JFileChooser;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.TransportCommand;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

enum AuthType {
	PASSWORD, KEY_PAIR
}

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class AuthenticationDialog extends javax.swing.JDialog {

	private static final ResourceBundle LANGUAGE = ResourceBundle.getBundle("resources/language"); // NO18N

	public static final String GIT_PREFIX = "git.";
	public static final String METHOD_SUFFIX = ".method";
	public static final String LOGIN_SUFFIX = ".login";
	public static final String PASSWORD_SUFFIX = ".password";
	public static final String PRIVATE_KEY_FILE_SUFFIX = ".privateKeyFile";
	public static final String PASSPHRASE_SUFFIX = ".passphrase";

	private final String remoteName;
	private final String workTreePath;

	/**
	 * Creates new form AuthenticationDialog
	 *
	 * @param <C> Type de la commande git.
	 * @param <T>
	 * @param parent Fenêtre parente.
	 * @param command Commande à exécuter avec authentification.
	 */
	public <C extends GitCommand, T> AuthenticationDialog(java.awt.Frame parent, TransportCommand<C, T> command) {
		super(parent, false);
		this.remoteName = getRemote(command);
		this.workTreePath = command.getRepository().getWorkTree().getPath();
		initComponents();

		AuthType method = Optional.ofNullable(PreferencesManager.get(GIT_PREFIX + workTreePath + METHOD_SUFFIX))
				.map(AuthType::valueOf)
				.orElse(null);
		final String login = PreferencesManager.get(GIT_PREFIX + workTreePath + LOGIN_SUFFIX);
		final String password = PreferencesManager.get(GIT_PREFIX + workTreePath + PASSWORD_SUFFIX);
		final String privateKeyFile = PreferencesManager.get(GIT_PREFIX + workTreePath + PRIVATE_KEY_FILE_SUFFIX);
		final String passphrase = PreferencesManager.get(GIT_PREFIX + workTreePath + PASSPHRASE_SUFFIX);

		if (method == null && (login != null || password != null)) {
			method = AuthType.PASSWORD;
		}

		if (method != null) {
			methodTabbedPane.setSelectedIndex(method.ordinal());
		}

		loginField.setText(login);
		passwordField.setText(password);
		privateKeyFileTextField.setText(privateKeyFile);
		passphrasePasswordField.setText(passphrase);
		rememberCheckBox.setSelected(method != null);
	}

	@Nullable
	private <C extends GitCommand, T> String getRemote(TransportCommand<C, T> command) {
		if (command instanceof PushCommand) {
			return ((PushCommand) command).getRemote();

		} else if (command instanceof PullCommand) {
			return ((PullCommand) command).getRemote();

		} else {
			return null;
		}
	}

	@Nullable
	public String getRemoteName() {
		return remoteName;
	}

	@NotNull
	AuthType getMethod() {
		return AuthType.values()[methodTabbedPane.getSelectedIndex()];
	}

	@NotNull
	public String getLogin() {
		return loginField.getText();
	}

	@NotNull
	public char[] getPassword() {
		return passwordField.getPassword();
	}

	@NotNull
	public String getPrivateKeyFile() {
		return privateKeyFileTextField.getText();
	}

	@NotNull
	public char[] getPassphrase() {
		return passphrasePasswordField.getPassword();
	}

	public boolean mustRememberPassword() {
		return rememberCheckBox.isSelected();
	}

	public JButton getOkButton() {
		return okButton;
	}

	public JButton getCancelButton() {
		return cancelButton;
	}

	/**
	 * This method is called from within the constructor to initialize the form.
	 * WARNING: Do NOT modify this code. The content of this method is always
	 * regenerated by the Form Editor.
	 */
	@SuppressWarnings("unchecked")
    // <editor-fold defaultstate="collapsed" desc="Generated Code">//GEN-BEGIN:initComponents
    private void initComponents() {

        inviteLabel = new javax.swing.JLabel();
        rememberCheckBox = new javax.swing.JCheckBox();
        okButton = new javax.swing.JButton();
        cancelButton = new javax.swing.JButton();
        methodTabbedPane = new javax.swing.JTabbedPane();
        passwordPanel = new javax.swing.JPanel();
        passwordLabel = new javax.swing.JLabel();
        loginLabel = new javax.swing.JLabel();
        loginField = new javax.swing.JTextField();
        passwordField = new javax.swing.JPasswordField();
        keyPairPanel = new javax.swing.JPanel();
        jLabel1 = new javax.swing.JLabel();
        jLabel2 = new javax.swing.JLabel();
        privateKeyFileTextField = new javax.swing.JTextField();
        selectPrivateKeyFileButton = new javax.swing.JButton();
        passphrasePasswordField = new javax.swing.JPasswordField();

        setDefaultCloseOperation(javax.swing.WindowConstants.DISPOSE_ON_CLOSE);

        inviteLabel.setText(java.text.MessageFormat.format(LANGUAGE.getString("team.git.authenticate"), new Object[] {getRemoteName()})); // NOI18N

        rememberCheckBox.setText(LANGUAGE.getString("team.git.savecredentials")); // NOI18N

        okButton.setText(LANGUAGE.getString("button.ok")); // NOI18N
        okButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                okButtonActionPerformed(evt);
            }
        });

        cancelButton.setText(LANGUAGE.getString("button.cancel")); // NOI18N
        cancelButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                cancelButtonActionPerformed(evt);
            }
        });

        passwordLabel.setText(LANGUAGE.getString("team.git.password")); // NOI18N

        loginLabel.setText(LANGUAGE.getString("team.git.login")); // NOI18N

        javax.swing.GroupLayout passwordPanelLayout = new javax.swing.GroupLayout(passwordPanel);
        passwordPanel.setLayout(passwordPanelLayout);
        passwordPanelLayout.setHorizontalGroup(
            passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(passwordPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(passwordLabel)
                    .addComponent(loginLabel))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(loginField, javax.swing.GroupLayout.DEFAULT_SIZE, 328, Short.MAX_VALUE)
                    .addComponent(passwordField))
                .addContainerGap())
        );
        passwordPanelLayout.setVerticalGroup(
            passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(passwordPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(loginLabel)
                    .addComponent(loginField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(passwordPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(passwordLabel)
                    .addComponent(passwordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        methodTabbedPane.addTab(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("resources/language").getString("team.git.auth.password"), new Object[] {}), passwordPanel); // NOI18N

        jLabel1.setText("Private Key File:");

        jLabel2.setText("Passphrase:");

        selectPrivateKeyFileButton.setText("...");
        selectPrivateKeyFileButton.addActionListener(new java.awt.event.ActionListener() {
            public void actionPerformed(java.awt.event.ActionEvent evt) {
                selectPrivateKeyFileButtonActionPerformed(evt);
            }
        });

        javax.swing.GroupLayout keyPairPanelLayout = new javax.swing.GroupLayout(keyPairPanel);
        keyPairPanel.setLayout(keyPairPanelLayout);
        keyPairPanelLayout.setHorizontalGroup(
            keyPairPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(keyPairPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(keyPairPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addComponent(jLabel1)
                    .addComponent(jLabel2))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(keyPairPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(keyPairPanelLayout.createSequentialGroup()
                        .addComponent(privateKeyFileTextField, javax.swing.GroupLayout.DEFAULT_SIZE, 266, Short.MAX_VALUE)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(selectPrivateKeyFileButton))
                    .addComponent(passphrasePasswordField))
                .addContainerGap())
        );
        keyPairPanelLayout.setVerticalGroup(
            keyPairPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(keyPairPanelLayout.createSequentialGroup()
                .addContainerGap()
                .addGroup(keyPairPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel1)
                    .addComponent(privateKeyFileTextField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE)
                    .addComponent(selectPrivateKeyFileButton))
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addGroup(keyPairPanelLayout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(jLabel2)
                    .addComponent(passphrasePasswordField, javax.swing.GroupLayout.PREFERRED_SIZE, javax.swing.GroupLayout.DEFAULT_SIZE, javax.swing.GroupLayout.PREFERRED_SIZE))
                .addContainerGap(9, Short.MAX_VALUE))
        );

        methodTabbedPane.addTab(java.text.MessageFormat.format(java.util.ResourceBundle.getBundle("resources/language").getString("team.git.auth.keypair"), new Object[] {}), keyPairPanel); // NOI18N

        javax.swing.GroupLayout layout = new javax.swing.GroupLayout(getContentPane());
        getContentPane().setLayout(layout);
        layout.setHorizontalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                    .addGroup(layout.createSequentialGroup()
                        .addGap(0, 0, Short.MAX_VALUE)
                        .addComponent(cancelButton)
                        .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                        .addComponent(okButton))
                    .addComponent(methodTabbedPane, javax.swing.GroupLayout.Alignment.TRAILING)
                    .addGroup(layout.createSequentialGroup()
                        .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
                            .addComponent(inviteLabel)
                            .addComponent(rememberCheckBox))
                        .addGap(0, 0, Short.MAX_VALUE)))
                .addContainerGap())
        );
        layout.setVerticalGroup(
            layout.createParallelGroup(javax.swing.GroupLayout.Alignment.LEADING)
            .addGroup(layout.createSequentialGroup()
                .addContainerGap()
                .addComponent(inviteLabel)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(methodTabbedPane, javax.swing.GroupLayout.PREFERRED_SIZE, 100, javax.swing.GroupLayout.PREFERRED_SIZE)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED)
                .addComponent(rememberCheckBox)
                .addPreferredGap(javax.swing.LayoutStyle.ComponentPlacement.RELATED, javax.swing.GroupLayout.DEFAULT_SIZE, Short.MAX_VALUE)
                .addGroup(layout.createParallelGroup(javax.swing.GroupLayout.Alignment.BASELINE)
                    .addComponent(cancelButton)
                    .addComponent(okButton))
                .addContainerGap())
        );

        pack();
    }// </editor-fold>//GEN-END:initComponents

    private void okButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_okButtonActionPerformed
		setVisible(false);

		final boolean mustRememberPassword = mustRememberPassword();
		final AuthType method = getMethod();

		if (!mustRememberPassword || method != AuthType.PASSWORD) {
			PreferencesManager.remove(GIT_PREFIX + workTreePath + LOGIN_SUFFIX);
			PreferencesManager.remove(GIT_PREFIX + workTreePath + PASSWORD_SUFFIX);
		}
		if (!mustRememberPassword || method != AuthType.KEY_PAIR) {
			PreferencesManager.remove(GIT_PREFIX + workTreePath + PRIVATE_KEY_FILE_SUFFIX);
			PreferencesManager.remove(GIT_PREFIX + workTreePath + PASSPHRASE_SUFFIX);
		}
		if (mustRememberPassword) {
			PreferencesManager.set(GIT_PREFIX + workTreePath + METHOD_SUFFIX, getMethod().name());
			if (method == AuthType.PASSWORD) {
				PreferencesManager.set(GIT_PREFIX + workTreePath + LOGIN_SUFFIX, getLogin());
				PreferencesManager.set(GIT_PREFIX + workTreePath + PASSWORD_SUFFIX, new String(getPassword()));
			} else if (method == AuthType.KEY_PAIR) {
				PreferencesManager.set(GIT_PREFIX + workTreePath + PRIVATE_KEY_FILE_SUFFIX, getPrivateKeyFile());
				PreferencesManager.set(GIT_PREFIX + workTreePath + PASSPHRASE_SUFFIX, new String(getPassphrase()));
			}
		} else {
			PreferencesManager.remove(GIT_PREFIX + workTreePath + METHOD_SUFFIX);
		}
    }//GEN-LAST:event_okButtonActionPerformed

    private void cancelButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_cancelButtonActionPerformed
		setVisible(false);
    }//GEN-LAST:event_cancelButtonActionPerformed

    private void selectPrivateKeyFileButtonActionPerformed(java.awt.event.ActionEvent evt) {//GEN-FIRST:event_selectPrivateKeyFileButtonActionPerformed
		JFileChooser fileChooser = new JFileChooser();
		fileChooser.setMultiSelectionEnabled(false);
		if (fileChooser.showOpenDialog(this) == JFileChooser.APPROVE_OPTION) {
			File file = fileChooser.getSelectedFile();
			privateKeyFileTextField.setText(file.getPath());
		}
    }//GEN-LAST:event_selectPrivateKeyFileButtonActionPerformed

    // Variables declaration - do not modify//GEN-BEGIN:variables
    private javax.swing.JButton cancelButton;
    private javax.swing.JLabel inviteLabel;
    private javax.swing.JLabel jLabel1;
    private javax.swing.JLabel jLabel2;
    private javax.swing.JPanel keyPairPanel;
    private javax.swing.JTextField loginField;
    private javax.swing.JLabel loginLabel;
    private javax.swing.JTabbedPane methodTabbedPane;
    private javax.swing.JButton okButton;
    private javax.swing.JPasswordField passphrasePasswordField;
    private javax.swing.JPasswordField passwordField;
    private javax.swing.JLabel passwordLabel;
    private javax.swing.JPanel passwordPanel;
    private javax.swing.JTextField privateKeyFileTextField;
    private javax.swing.JCheckBox rememberCheckBox;
    private javax.swing.JButton selectPrivateKeyFileButton;
    // End of variables declaration//GEN-END:variables
}
