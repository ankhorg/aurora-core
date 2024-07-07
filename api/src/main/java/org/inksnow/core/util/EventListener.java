package org.inksnow.core.util;

/**
 * Represents a listener accepting events of a specified type.
 *
 * @param <T> The type of the event
 */
@FunctionalInterface
public interface EventListener<T> {

    /**
     * Called when a event registered to this listener is called.
     *
     * @param event The called event
     * @throws Exception If an error occurs
     */
    void handle(T event) throws Exception;
}
