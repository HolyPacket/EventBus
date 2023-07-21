package de.rubymc.eventbus;

import de.rubymc.eventbus.interfaces.Subscribe;

import java.lang.reflect.Method;

/**
 * A wrapper class that represents an event handler bound to a listener object and its associated method.
 * This class is used to encapsulate and manage event handlers in an event-based system.
 */
public class EventHandlerWrapper {
    private final Object listener;
    private final Method method;
    private final boolean async;

    /**
     * Constructs an EventHandlerWrapper object with the provided listener object and method.
     *
     * @param listener The listener object that contains the event handler method.
     * @param method The method representing the event handler.
     */
    public EventHandlerWrapper(Object listener, Method method) {
        this.listener = listener;
        this.method = method;
        this.async = method.isAnnotationPresent(Subscribe.class) && method.getAnnotation(Subscribe.class).async();
    }

    /**
     * Returns the listener object associated with this event handler.
     *
     * @return The listener object.
     */
    public Object getListener() {
        return listener;
    }

    /**
     * Returns the method representing the event handler.
     *
     * @return The event handler method.
     */
    public Method getMethod() {
        return method;
    }

    /**
     * Checks if the event handler should be executed asynchronously.
     *
     * @return {@code true} if the event handler should be executed asynchronously, {@code false} otherwise.
     */
    public boolean isAsync() {
        return async;
    }
}