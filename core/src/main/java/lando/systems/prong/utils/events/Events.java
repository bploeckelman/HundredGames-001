package lando.systems.prong.utils.events;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

public class Events {
    private static final Events instance = new Events();

    private final Map<EventType, Set<EventListener>> listeners = new HashMap<>();

    public static Events get() {
        return instance;
    }

    public void subscribe(EventType type, EventListener listener) {
        Set<EventListener> eventListeners = listeners.get(type);
        if (eventListeners == null) {
            eventListeners = new HashSet<>();
            listeners.put(type, eventListeners);
        }
        eventListeners.add(listener);
    }

    public void unsubscribe(EventType type, EventListener listener) {
        Set<EventListener> eventListeners = listeners.get(type);
        if (eventListeners != null) {
            eventListeners.remove(listener);
        }
    }

    public void dispatch(EventType type, Object... data) {
        Set<EventListener> eventListeners = listeners.get(type);
        if (eventListeners != null) {
            for (EventListener listener : eventListeners) {
                listener.onEvent(type, data);
            }
        }
    }

    public interface EventListener {
        void onEvent(EventType eventType, Object... data);
    }
}
