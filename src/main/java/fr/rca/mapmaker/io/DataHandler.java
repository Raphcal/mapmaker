package fr.rca.mapmaker.io;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 *
 * @author daeke
 */
public interface DataHandler<T> {
	void write(T t, OutputStream outputStream) throws IOException;
	T read(InputStream inputStream) throws IOException;

	default String fileNameFor(T t) {
		throw new UnsupportedOperationException("Not supported");
	}
}
