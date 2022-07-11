package fr.rca.mapmaker.team.git;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.UserInfo;
import org.eclipse.jgit.api.TransportConfigCallback;
import org.eclipse.jgit.transport.SshTransport;
import org.eclipse.jgit.transport.Transport;
import org.eclipse.jgit.transport.ssh.jsch.JschConfigSessionFactory;
import org.eclipse.jgit.transport.ssh.jsch.OpenSshConfig;
import org.eclipse.jgit.util.FS;

/**
 *
 * @author Raphaël Calabro (raphael.calabro.external2@banque-france.fr)
 */
public class JSchTransportConfigCallback implements TransportConfigCallback {

	private String privateKeyFile;
	private String passphrase;

	public JSchTransportConfigCallback(String privateKeyFile, String passphrase) {
		this.privateKeyFile = privateKeyFile;
		this.passphrase = passphrase;
	}

	public JSchTransportConfigCallback(String privateKeyFile, char[] passphrase) {
		this.privateKeyFile = privateKeyFile;
		this.passphrase = new String(passphrase);
	}

	@Override
	public void configure(Transport transport) {
		SshTransport sshTransport = (SshTransport) transport;
		sshTransport.setSshSessionFactory(new JschConfigSessionFactory() {
			@Override
			protected JSch createDefaultJSch(FS fs) throws JSchException {
				final JSch defaultJSch = super.createDefaultJSch(fs);
				if (!privateKeyFile.isBlank()) {
					defaultJSch.addIdentity(privateKeyFile);
				}
				return defaultJSch;
			}

			@Override
			protected void configure(OpenSshConfig.Host hc, Session session) {
				session.setUserInfo(new UserInfo() {
					@Override
					public String getPassphrase() {
						return passphrase;
					}

					@Override
					public String getPassword() {
						return null;
					}

					@Override
					public boolean promptPassword(String message) {
						return false;
					}

					@Override
					public boolean promptPassphrase(String message) {
						return !passphrase.isBlank();
					}

					@Override
					public boolean promptYesNo(String message) {
						return false;
					}

					@Override
					public void showMessage(String message) {
					}
				});
			}
		});
	}

}
