package fr.rca.mapmaker.io.common;

import java.io.File;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Files {
	
	private Files() {
	}
	
	public static String getRelativePath(File parent, File child) {
		final String[] parentPath = parent.getPath().split(File.separator);
		final String[] childPath = child.getPath().split(File.separator);
		
		if(childPath.length < parentPath.length) {
			throw new IllegalArgumentException("Le chemin du fils doit-être plus long que celui de son parent. Fils : '" + child + "', parent : '" + parent + "'.");
		}
		
		int index = 0;
		while(index < parentPath.length && parentPath[index].equals(childPath[index])) {
			index++;
		}
		
		final int common = index;
		
		final StringBuilder pathBuilder = new StringBuilder();
		for(index = common; index < childPath.length; index++) {
			if(pathBuilder.length() > 0) {
				pathBuilder.append('/');
			}
			pathBuilder.append(childPath[index]);
		}
		
		return pathBuilder.toString();
	}
}
