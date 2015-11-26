package fr.rca.mapmaker.event;

import java.util.ArrayList;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import org.jetbrains.annotations.NotNull;

/**
 *
 * @author Raphaël Calabro (raphael.calabro@netapsys.fr)
 */
public class EventBus {
	
	public static final EventBus INSTANCE = new EventBus();
	
	private final Map<Event, List<EventListener>> allListeners = new EnumMap<Event, List<EventListener>>(Event.class);
	
	public void listenToEventsOfType(@NotNull Event event, @NotNull EventListener listener) {
		List<EventListener> listeners = allListeners.get(event);
		if (listeners == null) {
			listeners = new ArrayList<EventListener>();
			allListeners.put(event, listeners);
		}
		listeners.add(listener);
	}
	
	public void fireEvent(Event event, Object... arguments) {
		final List<EventListener> listeners = allListeners.get(event);
		if (listeners != null) {
			for (final EventListener listener : listeners) {
				listener.onEvent(event, arguments);
			}
		}
	}
	
}
