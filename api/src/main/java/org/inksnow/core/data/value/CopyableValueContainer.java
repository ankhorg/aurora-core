package org.inksnow.core.data.value;

/**
 * Represents a {@link ValueContainer} that can be copied.
 */
public interface CopyableValueContainer extends ValueContainer {

    /**
     * Creates a clone copy of this {@link CopyableValueContainer} as a new
     * {@link CopyableValueContainer} such that all the {@link Value}s are
     * safely duplicated to the new instance. It is not guaranteed that
     * the returning container is of the same type as this container.
     *
     * @return The new copy
     */
    CopyableValueContainer copy();
}
