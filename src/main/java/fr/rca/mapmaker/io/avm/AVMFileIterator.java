package fr.rca.mapmaker.io.avm;

import java.io.File;
import java.util.Iterator;

/**
 *
 * @author daeke
 */
public class AVMFileIterator implements Iterator<File> {

	private File source;
	private File parent;
	private String name;
	private int count;
	
	public AVMFileIterator(File source) {
		this.parent = source.getParentFile();
		this.name = source.getName().substring(0, source.getName().lastIndexOf('.'));
		this.source = source;
		this.count = 1;
	}

	private File nextFile() {
		
		if(count == 1) {
			return source;
		} else {
			return new File(parent, name + count + ".avm");
		}
	}
	
	@Override
	public boolean hasNext() {
		
		return nextFile().exists();
	}

	@Override
	public File next() {
		
		final File file = nextFile();
		count++;
		
		return file;
	}

	@Override
	public void remove() {
		throw new UnsupportedOperationException("Not supported yet.");
	}
}
