package fr.rca.mapmaker.io.plist;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.Deque;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.Nullable;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class PlistHandler extends DefaultHandler {
	private static final Logger LOGGER = LoggerFactory.getLogger(PlistHandler.class);

	private Map<String, Object> content;
	
	private Deque<Insertable> stack;
	private StringBuilder stringBuilder;
	private Mode mode;
	private String currentKey;

	@Nullable
	public Map<String, Object> getContent() {
		return content;
	}

	@Override
	public void startDocument() throws SAXException {
		stack = new ArrayDeque<Insertable>();
		stringBuilder = new StringBuilder();
	}
	
	@Override
	public void startElement(String uri, String localName, String qName, Attributes attributes) throws SAXException {
		mode = Mode.valueOf(qName.toUpperCase());
		
		switch(mode) {
			case DICT:
				final InsertableMap dict = new InsertableMap();
				if(content == null) {
					content = dict.get();
					stack.push(dict);
					stringBuilder.setLength(0);
				} else {
					put(dict.get());
					stack.push(dict);
				}
				break;
				
			case ARRAY:
				final Insertable array = new InsertableList();
				put(array.get());
				stack.push(array);
				break;
				
			case TRUE:
				put(true);
				break;
				
			case FALSE:
				put(false);
				break;
				
			default:
				break;
		}
	}

	@Override
	public void endElement(String uri, String localName, String qName) throws SAXException {
		final Mode currentMode = Mode.valueOf(qName.toUpperCase());

		final String characters = stringBuilder.toString().trim();
		
		switch(currentMode) {
			case KEY:
				currentKey = characters;
				stringBuilder.setLength(0);
				break;

			case DICT:
			case ARRAY:
				stack.pop();
				break;

			case STRING:
				put(characters);
				break;

			case INTEGER:
				put(Integer.parseInt(characters));
				break;

			case REAL:
				put(Double.parseDouble(characters));
				break;

			case DATE:
				final SimpleDateFormat formatter = new SimpleDateFormat(Plists.DATE_FORMAT);
				final String date = characters;
				try {
					put(formatter.parse(date));
				} catch (ParseException ex) {
					LOGGER.warn("Erreur lors du parsing de la date '" + stringBuilder + "'.", ex);
				}
				stringBuilder.setLength(0);
				break;
				
			default:
				break;
		}
	}

	@Override
	public void characters(char[] ch, int start, int length) throws SAXException {
		stringBuilder.append(ch, start, length);
	}
	
	private void put(Object value) {
		stack.peek().insert(currentKey, value);
		currentKey = null;
		stringBuilder.setLength(0);
	}
	
	private static interface Insertable {
		Object get();
		void insert(String key, Object object);
	}
	
	private static class InsertableMap implements Insertable {
		private final Map<String, Object> map = new HashMap<String, Object>();

		@Override
		public Map<String, Object> get() {
			return map;
		}

		@Override
		public void insert(String key, Object object) {
			map.put(key, object);
		}
	}
	
	private static class InsertableList implements Insertable {
		private final List<Object> list = new ArrayList<Object>();

		@Override
		public List<Object> get() {
			return list;
		}

		@Override
		public void insert(String key, Object object) {
			list.add(object);
		}
	}
	
	private static enum Mode {
		PLIST, KEY, DICT, ARRAY, STRING, INTEGER, REAL, TRUE, FALSE, DATE
	}
}
