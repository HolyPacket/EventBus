package de.rubymc.eventbus;

import de.rubymc.eventbus.interfaces.Event;
import de.rubymc.eventbus.interfaces.Subscribe;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * A simple event bus implementation that allows objects to subscribe and publish events.
 * Objects can register as listeners to handle specific types of events.
 */
public class EventBus {
    private final Map<Class<? extends Event>, List<EventHandlerWrapper>> handlers;
    private final ExecutorService executorService;

    /**
     * Constructs an EventBus object with an internal ConcurrentHashMap to store event handlers
     * and a cached thread pool ExecutorService for handling asynchronous event execution.
     */
    public EventBus() {
        this.handlers = new ConcurrentHashMap<>();
        this.executorService = Executors.newCachedThreadPool();
    }

    /**
     * Static factory method to create a new instance of EventBus.
     *
     * @return A new EventBus instance.
     */
    public static EventBus create() {
        return new EventBus();
    }

    /**
     * Registers a listener object to the event bus. The listener object's methods annotated with @Subscribe
     * will be added as event handlers for specific event types.
     *
     * @param listener The listener object that wants to handle events.
     */
    public void register(Object listener) {
        for (Method method : listener.getClass().getMethods()) {
            if (method.isAnnotationPresent(Subscribe.class)) {
                Class<?>[] parameterTypes = method.getParameterTypes();
                if (parameterTypes.length == 1 && Event.class.isAssignableFrom(parameterTypes[0])) {
                    Class<? extends Event> eventType = parameterTypes[0].asSubclass(Event.class);
                    EventHandlerWrapper wrapper = new EventHandlerWrapper(listener, method);
                    handlers.computeIfAbsent(eventType, k -> new CopyOnWriteArrayList<>()).add(wrapper);
                }
            }
        }
    }

    /**
     * Unregisters a listener object from the event bus. The listener's event handlers will no longer
     * receive events after being unregistered.
     *
     * @param listener The listener object to unregister.
     */
    public void unregister(Object listener) {
        for (List<EventHandlerWrapper> wrappers : handlers.values()) {
            wrappers.removeIf(wrapper -> wrapper.getListener() == listener);
        }
    }

    /**
     * Posts an event to the event bus, notifying all registered event handlers for that event type.
     * Event handlers can be executed asynchronously if marked with @Subscribe(async = true).
     *
     * @param event The event to be published.
     */
    public void post(Event event) {
        List<EventHandlerWrapper> wrappers = handlers.get(event.getClass());
        if (wrappers != null) {
            for (EventHandlerWrapper wrapper : wrappers) {
                if (wrapper.isAsync()) {
                    executorService.submit(() -> invokeHandler(wrapper, event));
                } else {
                    invokeHandler(wrapper, event);
                }
            }
        }
    }

    /**
     * Invokes the event handler for a given event with the provided event object.
     *
     * @param wrapper The EventHandlerWrapper containing the event handler to be invoked.
     * @param event The event object to be passed to the event handler method.
     */
    private void invokeHandler(EventHandlerWrapper wrapper, Event event) {
        try {
            wrapper.getMethod().invoke(wrapper.getListener(), event);
        } catch (IllegalAccessException | InvocationTargetException e) {
            e.printStackTrace();
        }
    }
}