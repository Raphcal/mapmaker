package fr.rca.mapmaker.team.git;

import java.io.File;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class CommitEntry {
	
	public static enum Status {
		ADDED, MODIFIED, REMOVED
	}
	
	private final String path;
	private final Status status;

	public CommitEntry(String path, Status status) {
		this.path = path;
		this.status = status;
	}
	
	public String getFileName() {
		return new File(path).getName();
	}
	
	public String getRelativePath() {
		return path;
	}

	public Status getStatus() {
		return status;
	}
	
}
