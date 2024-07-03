package org.inksnow.core.util;

import org.checkerframework.common.returnsreceiver.qual.This;

/**
 * A builder interface that can copy an object.
 *
 * @param <T> The type of the object to build.
 * @param <B> The type of the builder.
 */
public interface CopyableBuilder<T, B extends CopyableBuilder<T, B>> extends Builder<T, B> {
    /**
     * Copies the value to the builder.
     *
     * @param value the value
     * @return this builder
     */
    @This B copy(T value);
}
