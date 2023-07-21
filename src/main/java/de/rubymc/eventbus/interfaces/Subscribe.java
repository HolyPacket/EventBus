package de.rubymc.eventbus.interfaces;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * A custom annotation to mark a method as an event handler that can subscribe to events published in the event bus.
 * The annotated method will be invoked when an event of the specified type is posted to the event bus.
 */
@Retention(RetentionPolicy.RUNTIME)
@Target(ElementType.METHOD)
public @interface Subscribe {

    /**
     * Specifies whether the annotated event handler method should be executed asynchronously or not.
     *
     * @return {@code true} if the event handler method should be executed asynchronously,
     *         {@code false} if it should be executed synchronously (default).
     */
    boolean async() default false;
}