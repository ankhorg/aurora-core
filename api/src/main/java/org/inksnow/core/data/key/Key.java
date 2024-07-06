package org.inksnow.core.data.key;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.common.returnsreceiver.qual.This;
import org.inksnow.core.Aurora;
import org.inksnow.core.data.value.ListValue;
import org.inksnow.core.data.value.MapValue;
import org.inksnow.core.data.value.SetValue;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.resource.WithResourcePath;
import org.inksnow.core.util.Builder;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Set;

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
    static <E, V extends Value<E>> Builder<E, V> builder() {
        return Aurora.createBuilder(Builder.class);
    }

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

    interface Builder<E, V extends Value<E>> extends org.inksnow.core.util.Builder<Key<V>, Builder<E, V>> {
        @This Builder<E, V> resourcePath(ResourcePath resourcePath);

        <T, B extends Value<T>> @This Builder<T, B> type(TypeToken<B> token);

        <T> @This Builder<T, Value<T>> elementType(Class<T> type);

        <T> @This Builder<T, Value<T>> elementType(TypeToken<T> type);

        <T> @This Builder<List<T>, ListValue<T>> listElementType(Class<T> type);

        <T> @This Builder<List<T>, ListValue<T>> listElementType(TypeToken<T> type);

        <T> @This Builder<Set<T>, SetValue<T>> setElementType(Class<T> type);

        <T> @This Builder<Set<T>, SetValue<T>> setElementType(TypeToken<T> type);

        <K, V1> @This Builder<Map<K, V1>, MapValue<K, V1>> mapElementType(Class<K> keyType, Class<V1> valueType);

        <K, V1> @This Builder<Map<K, V1>, MapValue<K, V1>> mapElementType(TypeToken<K> keyType, TypeToken<V1> valueType);
    }
}
