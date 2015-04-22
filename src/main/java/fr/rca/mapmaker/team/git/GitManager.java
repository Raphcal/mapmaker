package fr.rca.mapmaker.team.git;

import fr.rca.mapmaker.exception.Exceptions;
import fr.rca.mapmaker.io.common.Files;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.beans.PropertyChangeListener;
import java.beans.PropertyChangeSupport;
import java.io.File;
import java.io.IOException;
import javax.swing.JFrame;
import javax.swing.JOptionPane;
import org.eclipse.jgit.api.AddCommand;
import org.eclipse.jgit.api.CommitCommand;
import org.eclipse.jgit.api.Git;
import org.eclipse.jgit.api.GitCommand;
import org.eclipse.jgit.api.InitCommand;
import org.eclipse.jgit.api.PullCommand;
import org.eclipse.jgit.api.PushCommand;
import org.eclipse.jgit.api.Status;
import org.eclipse.jgit.api.TransportCommand;
import org.eclipse.jgit.api.errors.GitAPIException;
import org.eclipse.jgit.api.errors.TransportException;
import org.eclipse.jgit.storage.file.FileRepositoryBuilder;
import org.eclipse.jgit.transport.UsernamePasswordCredentialsProvider;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Gère les opérations de Git.
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class GitManager {
	private static final Logger LOGGER = LoggerFactory.getLogger(GitManager.class);
	private static final String AVAILABLE_PROPERTY = "available";
	private static final String INITIALIZABLE_PROPERTY = "initializable";
	
	private final PropertyChangeSupport propertyChangeSupport = new PropertyChangeSupport(this);
	
	private JFrame parent;
	private File project;
	private Git git;

	/**
	 * Défini la fenêtre parente de ce gestionnaire.
	 * 
	 * @param parent Fenêtre parente.
	 */
	public void setParent(JFrame parent) {
		this.parent = parent;
	}

	/**
	 * Change le projet géré par ce gestionnaire.
	 * 
	 * @param project Emplacement du projet à ouvrir.
	 */
	public void setProject(File project) {
		final boolean oldAvailable = isAvailable();
		final boolean oldInitializable = isInitializable();
		
		this.project = project;
		this.git = openRepository();
		
		propertyChangeSupport.firePropertyChange(AVAILABLE_PROPERTY, oldAvailable, isAvailable());
		propertyChangeSupport.firePropertyChange(INITIALIZABLE_PROPERTY, oldInitializable, isInitializable());
	}
	
	/**
	 * Indique si git est disponible pour le projet ouvert actuellement.
	 * 
	 * @return <code>true</code> si git est disponible, <code>false</code> sinon.
	 */
	public boolean isAvailable() {
		return git != null;
	}
	
	/**
	 * Indique s'il est possible d'appeler la méthode "init".
	 * @return 
	 */
	public boolean isInitializable() {
		return git == null && project != null;
	}
	
	/**
	 * Exécute une commande <code>init</code> sur le projet ouvert et créé un 
	 * nouveau dépôt.
	 * @throws IllegalStateException si le projet actuel possède déjà un dépôt git.
	 */
	public void init() {
		if(git != null) {
			throw new IllegalStateException("Un dépôt git existe déjà pour le projet actuel.");
		} else if(project == null) {
			throw new IllegalStateException("Aucun fichier sélectionné.");
		}
		
		final InitCommand initCommand = Git.init();
		
		final File directory = project.isDirectory() ? project : project.getParentFile();
		initCommand.setDirectory(directory);
		try {
			initCommand.call();
		} catch (GitAPIException ex) {
			LOGGER.error("Erreur lors de l'initialisation d'un dépôt git pour le dossier '" + directory + "'.", ex);
		}
	}
	
	/**
	 * Exécute une commande <code>push</code> sur le projet ouvert.
	 * @throws IllegalStateException si le projet actuel ne possède pas de dépôt git.
	 */
	public void push() {
		ensureAvailable();
		
		try {
			call(git.push());

		} catch (TransportException ex) {
			LOGGER.trace("L'opération 'push' sans authentification n'a pas fonctionnée.", ex);
			// 2ème tentative de push mais avec authentification.
			callWithAuthentification(git.push());

		} catch (GitAPIException ex) {
			LOGGER.error("Erreur lors du push.", ex);
			Exceptions.showStackTrace(ex, parent);
		}
	}
	
	/**
	 * Exécute une commande <code>pull</code> sur le projet ouvert.
	 * @throws IllegalStateException si le projet actuel ne possède pas de dépôt git.
	 */
	public void pull() {
		ensureAvailable();
		
		try {
			call(git.pull());
			
		} catch (TransportException ex) {
			LOGGER.trace("L'opération 'pull' sans authentification n'a pas fonctionnée.", ex);
			// 2ème tentative de push mais avec authentification.
			callWithAuthentification(git.pull());

		} catch (GitAPIException ex) {
			LOGGER.error("Erreur lors du pull.", ex);
			Exceptions.showStackTrace(ex, parent);
		}
	}
	
	/**
	 * Exécute une commande <code>commit</code> sur le projet ouvert.
	 * @throws IllegalStateException si le projet actuel ne possède pas de dépôt git.
	 */
	public void commit() {
		ensureAvailable();
		
		try {
			// Ajout des fichiers
			final AddCommand addCommand = git.add();

			final File workTree = git.getRepository().getWorkTree();

			final String relativePath = Files.getRelativePath(workTree, project);
			final int addOption = JOptionPane.showConfirmDialog(parent, "Ajout de '" + relativePath + "'...\nSouhaitez-vous continuer ?", "Git Add", JOptionPane.YES_NO_OPTION);
			if(addOption == JOptionPane.NO_OPTION) {
				return;
			}

			addCommand.addFilepattern(relativePath);
			addCommand.call();

			final Status status = git.status().call();

			final StringBuilder stringBuilder = new StringBuilder();
			stringBuilder.append("Résultat de Add :");

			for(final String added : status.getAdded()) {
					stringBuilder.append("\nadded: ").append(added);
			}

			for(final String modified : status.getChanged()) {
					stringBuilder.append("\nmodified: ").append(modified);
			}

			for(final String removed : status.getRemoved()) {
					stringBuilder.append("\nremoved: ").append(removed);
			}

			stringBuilder.append("\nSouhaitez-vous continuer ?");

			final int addResultOption = JOptionPane.showConfirmDialog(parent, stringBuilder.toString(), "Git Add", JOptionPane.YES_NO_OPTION);
			if(addResultOption == JOptionPane.NO_OPTION) {
				return;
			}

			// Commit
			final CommitCommand commitCommand = git.commit();

			final String message = JOptionPane.showInputDialog("Message de commit :");
			if(message == null || message.isEmpty()) {
				return;
			}

			commitCommand.setMessage(message);
			call(commitCommand);

		} catch (GitAPIException ex) {
			Exceptions.showStackTrace(ex, parent);
		}
	}
	
	/**
	 * Appel la commande donnée et affiche un message de confirmation si l'opération s'est bien passée.
	 * @param command Commande à exécuter.
	 * @throws GitAPIException En cas d'erreur.
	 */
	private void call(PushCommand command) throws GitAPIException {
		command.call();
		JOptionPane.showMessageDialog(parent, "Push effectué.");
	}
	
	/**
	 * Appel la commande donnée et affiche un message de confirmation si l'opération s'est bien passée.
	 * @param command Commande à exécuter.
	 * @throws GitAPIException En cas d'erreur.
	 */
	private void call(PullCommand command) throws GitAPIException {
		command.call();
		JOptionPane.showMessageDialog(parent, "Pull effectué.");
	}
	
	/**
	 * Appel la commande donnée et affiche un message de confirmation si l'opération s'est bien passée.
	 * @param command Commande à exécuter.
	 * @throws GitAPIException En cas d'erreur.
	 */
	private void call(CommitCommand command) throws GitAPIException {
		command.call();
		JOptionPane.showMessageDialog(parent, "Commit effectué.");
	}
	
	/**
	 * Appel la commande donnée et affiche un message de confirmation si l'opération s'est bien passée.
	 * @param command Commande à exécuter.
	 * @throws GitAPIException En cas d'erreur.
	 */
	private <C extends GitCommand, T> void call(TransportCommand<C, T> command) throws GitAPIException {
		if(command instanceof PushCommand) {
			call((PushCommand) command);
		} else if(command instanceof PullCommand) {
			call((PullCommand) command);
		}
	}
	
	/**
	 * Vérifie si un dépôt git existe pour le projet actuel. Si aucun dépôt exite,
	 * cette méthode lance une exception.
	 * @throws IllegalStateException si le projet actuel ne possède pas de dépôt git.
	 */
	private void ensureAvailable() {
		if(!isAvailable()) {
			throw new IllegalStateException("Le projet actuel ne dispose pas d'un repository git.");
		}
	}
	
	/**
	 * Affiche une popup d'authentification permettant la connexion à un dépôt distant.
	 */
	private <C extends GitCommand, T> void callWithAuthentification(final TransportCommand<C, T> command) {
		final AuthenticationDialog dialog = new AuthenticationDialog(parent, command);
		dialog.getOkButton().addActionListener(new ActionListener() {

			@Override
			public void actionPerformed(ActionEvent e) {
				command.setCredentialsProvider(new UsernamePasswordCredentialsProvider(dialog.getLogin(), dialog.getPassword()));

				try {
					call(command);

				} catch (GitAPIException ex) {
					LOGGER.error("L'opération avec authentification n'a pas fonctionnée.", ex);

					// Affichage de l'erreur.
					Exceptions.showStackTrace(ex, parent);
				}
			}
		});

		dialog.setVisible(true);
	}
	
	/**
	 * Ouvre le dépôt git du projet actuel.
	 * @return Le dépôt git du projet actuel ou <code>null</code> si aucun
	 * dépôt n'a été trouvé.
	 */
	@Nullable
	private Git openRepository() {
		final FileRepositoryBuilder builder = new FileRepositoryBuilder();
		builder.readEnvironment().findGitDir(project);
		
		if(builder.getGitDir() == null) {
			return null;
		}
		
		try {
			return new Git(builder.build());
			
		} catch (IOException ex) {
			// Ignoré
			return null;
		}
	}
	
	/**
	 * Ajoute un listener de propriétés.
	 * @param pl Listener à ajouter.
	 */
	public void addPropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.addPropertyChangeListener(pl);
	}

	/**
	 * Retire un listener de propriétés.
	 * @param pl Listener à retirer.
	 */
	public void removePropertyChangeListener(PropertyChangeListener pl) {
		propertyChangeSupport.removePropertyChangeListener(pl);
	}
}
