package org.inksnow.core.data.key;

import org.inksnow.core.data.value.Value;
import org.inksnow.core.resource.WithResourcePath;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * Represents a key to an underlying {@link Value} such that the underlying
 * value can be retrieved from a {@link ValueContainer}. For the key to be used
 * through retrieval of {@link DataHolder}s, it's required to use a
 * {@link DataRegistration} if the data is needed to be serialized, or dynamically
 * provided for through external mechanisms, through {@link DataProvider}s.
 *
 * <p>If dynamic or persistent retention of the {@link Value Values} by
 * {@link Key keys} is not desired, a registration with {@link DataRegistration}
 * is optional. This would mean that any submitted {@link Value}s of a
 * {@link Key} without an associated {@link DataRegistration} will be only
 * stored on a
 * {@link org.spongepowered.api.data.DataHolder.Mutable mutable DataHolder} for
 * the duration that that holder exists. The value would not persist between
 * reloads, restarts, etc.</p>
 *
 * @param <V> The type of {@link Value}
 */
public interface Key<V extends Value<?>> extends WithResourcePath {
    /**
     * Gets the type of the {@link Value} this {@link Key} is representing.
     *
     * @return The value generic type
     */
    Type valueType();

    /**
     * Gets the type of the element of the {@link Value} this {@link Key}
     * is representing. On occasion, if the element is a {@link Collection} type,
     * one can use {@link ParameterizedType#getActualTypeArguments()} to access
     * type parameters, such as the element type parameter for {@link List} or
     * {@link Map} values.
     *
     * @return The element generic type
     */
    Type elementType();
}
