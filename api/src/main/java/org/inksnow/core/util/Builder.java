package org.inksnow.core.util;

import org.checkerframework.common.returnsreceiver.qual.This;

/**
 * A builder interface.
 *
 * @param <T> The type of the object to build.
 * @param <B> The type of the builder.
 */
public interface Builder<T, B extends Builder<T, B>> {

    /**
     * Returns this builder.
     *
     * @return this builder
     */
    @SuppressWarnings("unchecked")
    default @This B getThis() {
        return (B) this;
    }

    /**
     * Resets the builder.
     *
     * @return this builder
     */
    @This B reset();

    /**
     * Builds the object.
     *
     * @return the object
     */
    @This T build();
}
