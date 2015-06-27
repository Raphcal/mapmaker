package fr.rca.mapmaker.io.bundle;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.text.SimpleDateFormat;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.Map;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;
import org.jetbrains.annotations.NotNull;
import org.xml.sax.SAXException;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public final class Plists {
	
	private static final Charset UTF8_CHARSET = Charset.forName("UTF-8");
	private static final String PLIST_START = "<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n" +
		"<!DOCTYPE plist PUBLIC \"-//Apple//DTD PLIST 1.0//EN\" \"http://www.apple.com/DTDs/PropertyList-1.0.dtd\">\n" +
		"<plist version=\"1.0\">\n";
	private static final String PLIST_END = "</plist>";
	
	private static final String INDENT = "\t";
	
	private static final String KEY_START = "<key>";
	private static final String KEY_END = "</key>\n";
	
	private static final String DICT_START = "<dict>\n";
	private static final String DICT_END = "</dict>\n";
	
	private static final String ARRAY_START = "<array>\n";
	private static final String ARRAY_END = "</array>\n";
	
	private static final String STRING_START = "<string>";
	private static final String STRING_END = "</string>\n";
	
	private static final String INTEGER_START = "<integer>";
	private static final String INTEGER_END = "</integer>\n";
	
	private static final String REAL_START = "<real>";
	private static final String REAL_END = "</real>\n";
	
	private static final String TRUE = "<true/>\n";
	private static final String FALSE = "<false/>\n";
	
	private static final String DATE_START = "<date>";
	private static final String DATE_END = "</date>\n";
	
	public static final String DATE_FORMAT = "yyyy-MM-dd'T'HH:mm:ssXXXX";
	
	
	private Plists() {
	}
	
	@NotNull
	public static Map<String, Object> read(InputStream inputStream) throws IOException {
		try {
			final SAXParserFactory parserFactory = SAXParserFactory.newInstance();
			parserFactory.setFeature("http://xml.org/sax/features/validation", false);
			parserFactory.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false);
			parserFactory.setFeature("http://xml.org/sax/features/external-general-entities", false);
			parserFactory.setFeature("http://xml.org/sax/features/external-parameter-entities", false);
			parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
			parserFactory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
			parserFactory.setFeature("http://xml.org/sax/features/use-entity-resolver2", false);   
			parserFactory.setFeature("http://xml.org/sax/features/resolve-dtd-uris", false);
			parserFactory.setFeature("http://apache.org/xml/features/validation/dynamic", false);
			parserFactory.setFeature("http://apache.org/xml/features/validation/schema/augment-psvi", false);
			
			final SAXParser parser = parserFactory.newSAXParser();
			final PlistHandler handler = new PlistHandler();
			parser.parse(inputStream, handler);
			
			final Map<String, Object> map = handler.getContent();
			if(map != null) {
				return map;
			} else {
				return Collections.emptyMap();
			}
			
		} catch (ParserConfigurationException ex) {
			throw new IOException("Erreur lors de la configuration de l'analyseur SAX.", ex);
		} catch (SAXException ex) {
			throw new IOException("Erreur lors de la lecture d'un fichier PLIST.", ex);
		}
	}
	
	public static void write(Map<String, Object> values, OutputStream outputStream) throws IOException {
		write(PLIST_START, outputStream);
		write(0, null, values, outputStream);
		write(PLIST_END, outputStream);
	}
	
	private static void write(String string, OutputStream outputStream) throws IOException {
		outputStream.write(string.getBytes(UTF8_CHARSET));
	}
	
	private static void writeLevel(int level, OutputStream outputStream) throws IOException {
		for(int index = 0; index < level; index++) {
			write(INDENT, outputStream);
		}
	}
	
	private static void writeKey(int level, String key, OutputStream outputStream) throws IOException {
		if(key != null) {
			writeLevel(level, outputStream);
			write(KEY_START, outputStream);
			write(key, outputStream);
			write(KEY_END, outputStream);
		}
	}
	
	private static void write(int level, String key, Object value, OutputStream outputStream) throws IOException {
		if(value instanceof Map) {
			write(level, key, (Map) value, outputStream);
			
		} if(value instanceof List) {
			write(level, key, (List) value, outputStream);

		} else if(value instanceof String) {
			write(level, key, (String) value, outputStream);

		} else if(value instanceof Integer) {
			write(level, key, (Integer) value, outputStream);

		} else if(value instanceof Long) {
			write(level, key, (Long) value, outputStream);

		} else if(value instanceof Float) {
			write(level, key, (Float) value, outputStream);

		} else if(value instanceof Double) {
			write(level, key, (Double) value, outputStream);

		} else if(value instanceof Boolean) {
			write(level, key, (Boolean) value, outputStream);

		} else if(value instanceof Date) {
			write(level, key, (Date) value, outputStream);
		}
	}
	
	private static void write(int level, String key, Map<String, Object> values, OutputStream outputStream) throws IOException {
		writeKey(level, key, outputStream);
		writeLevel(level, outputStream);
		write(DICT_START, outputStream);
		
		for(Map.Entry<String, Object> entry : values.entrySet()) {
			write(level + 1, entry.getKey(), entry.getValue(), outputStream);
		}
		
		writeLevel(level, outputStream);
		write(DICT_END, outputStream);
	}
	
	private static void write(int level, String key, List<Object> values, OutputStream outputStream) throws IOException {
		writeKey(level, key, outputStream);
		writeLevel(level, outputStream);
		write(ARRAY_START, outputStream);
		
		for(final Object value : values) {
			write(level + 1, null, value, outputStream);
		}
		
		writeLevel(level, outputStream);
		write(ARRAY_END, outputStream);
	}
	
	private static void write(int level, String key, String value, OutputStream outputStream) throws IOException {
		writeKey(level, key, outputStream);
		writeLevel(level, outputStream);
		write(STRING_START, outputStream);
		write(value, outputStream);
		write(STRING_END, outputStream);
	}
	
	private static void write(int level, String key, Long value, OutputStream outputStream) throws IOException {
		writeKey(level, key, outputStream);
		writeLevel(level, outputStream);
		write(INTEGER_START, outputStream);
		write(value.toString(), outputStream);
		write(INTEGER_END, outputStream);
	}
	
	private static void write(int level, String key, Integer value, OutputStream outputStream) throws IOException {
		write(level, key, value.longValue(), outputStream);
	}
	
	private static void write(int level, String key, Double value, OutputStream outputStream) throws IOException {
		writeKey(level, key, outputStream);
		writeLevel(level, outputStream);
		write(REAL_START, outputStream);
		write(value.toString(), outputStream);
		write(REAL_END, outputStream);
	}
	
	private static void write(int level, String key, Float value, OutputStream outputStream) throws IOException {
		write(level, key, value.doubleValue(), outputStream);
	}
	
	private static void write(int level, String key, Boolean value, OutputStream outputStream) throws IOException {
		writeKey(level, key, outputStream);
		writeLevel(level, outputStream);
		if(value) {
			write(TRUE, outputStream);
		} else {
			write(FALSE, outputStream);
		}
	}
	
	private static void write(int level, String key, Date value, OutputStream outputStream) throws IOException {
		writeKey(level, key, outputStream);
		writeLevel(level, outputStream);
		write(DATE_START, outputStream);
		
		final SimpleDateFormat formatter = new SimpleDateFormat(DATE_FORMAT);
		write(formatter.format(value), outputStream);
		
		write(DATE_END, outputStream);
	}
}
