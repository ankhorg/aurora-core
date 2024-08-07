package org.inksnow.core.data.value;

import org.inksnow.core.data.key.Key;

import java.util.Collection;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.function.Predicate;
import java.util.function.Supplier;

public interface MapValue<K, V> extends Value<Map<K, V>> {
    /**
     * Constructs a mutable {@link MapValue} of the appropriate type based
     * on the given {@link Key} and the element.
     *
     * @param key The key
     * @param element The element
     * @param <K> The map key type
     * @param <V> The map value type
     * @return The constructed mutable value
     */
    static <K, V> MapValue.Mutable<K, V> mutableOf(Key<? extends MapValue<K, V>> key, Map<K, V> element) {
        return Value.mutableOf(key, element);
    }

    /**
     * Constructs a mutable {@link MapValue} of the appropriate type based
     * on the given {@link Key} and the element.
     *
     * @param key The key
     * @param element The element
     * @param <K> The map key type
     * @param <V> The map value type
     * @return The constructed mutable value
     */
    static <K, V> MapValue.Mutable<K, V> mutableOf(Supplier<? extends Key<? extends MapValue<K, V>>> key, Map<K, V> element) {
        return MapValue.mutableOf(key.get(), element);
    }

    /**
     * Constructs an immutable {@link MapValue} of the appropriate type based
     * on the given {@link Key} and the element.
     *
     * @param key The key
     * @param element The element
     * @param <K> The map key type
     * @param <V> The map value type
     * @return The constructed immutable value
     */
    static <K, V> MapValue.Immutable<K, V> immutableOf(Key<? extends MapValue<K, V>> key, Map<K, V> element) {
        return Value.immutableOf(key, element);
    }

    /**
     * Constructs an immutable {@link MapValue} of the appropriate type based
     * on the given {@link Key} and the element.
     *
     * @param key The key
     * @param element The element
     * @param <K> The map key type
     * @param <V> The map value type
     * @return The constructed immutable value
     */
    static <K, V> MapValue.Immutable<K, V> immutableOf(Supplier<? extends Key<? extends MapValue<K, V>>> key, Map<K, V> element) {
        return MapValue.immutableOf(key.get(), element);
    }

    @Override
    Key<? extends MapValue<K, V>> key();

    /**
     * Gets the size of this map.
     *
     * @return The size of this map
     */
    int size();

    /**
     * Checks if the provided key is contained within this map.
     *
     * @param key The key to check
     * @return True if the key is contained
     */
    boolean containsKey(K key);

    /**
     * Checks if the provided value is contained within this map.
     *
     * @param value The value to check
     * @return True if the value is contained
     */
    boolean containsValue(V value);

    /**
     * Gets an {@link Set} of all keys contained in this map value.
     *
     * @return The set of keys
     */
    Set<K> keySet();

    /**
     * Retrieves an {@link Set} of the {@link java.util.Map.Entry}s contained
     * within this map value.
     *
     * @return The immutable set of entries
     */
    Set<Map.Entry<K, V>> entrySet();

    /**
     * Retrieves an {@link Collection} of all available values within
     * this map.
     *
     * @return The collection of values
     */
    Collection<V> values();

    @Override
    MapValue.Mutable<K, V> asMutable();

    @Override
    MapValue.Mutable<K, V> asMutableCopy();

    @Override
    MapValue.Immutable<K, V> asImmutable();

    /**
     * Represents a specialized type of {@link Value.Mutable} that is different from
     * a {@link CollectionValue.Mutable} such that the "elements" are
     * {@link java.util.Map.Entry}. Usually, this type of value is used to represent
     * a particular "type" of "key" that is associated to a particular "value".
     *
     * @param <K> The type of the key
     * @param <V> The type of the value
     */
    interface Mutable<K, V> extends MapValue<K, V>, Value.Mutable<Map<K, V>> {
        /**
         * Associates the provided key to the provided value. If there already
         * exists a value for the provided key, the value is replaced.
         *
         * @param key The key to associate to the value
         * @param value The value associated with the key
         * @return This map value, for chaining
         */
        MapValue.Mutable<K, V> put(K key, V value);

        /**
         * Associates all provided {@link java.util.Map.Entry} to this map value.
         *
         * @param map The map of key values to set
         * @return This map value, for chaining
         */
        MapValue.Mutable<K, V> putAll(Map<K, V> map);

        /**
         * Removes the association of the provided key to the value currently
         * associated.
         *
         * @param key The key to remove
         * @return This map value, for chaining
         */
        MapValue.Mutable<K, V> remove(K key);

        /**
         * Removes all key value associations of the provided keys.
         *
         * @param keys The keys to remove
         * @return This map value, for chaining
         */
        MapValue.Mutable<K, V> removeAll(Iterable<K> keys);

        /**
         * Applies the {@link Predicate} to all {@link java.util.Map.Entry} within this
         * {@link MapValue.Mutable}. Any entries that are false will be removed from the
         * map value.
         *
         * @param predicate The predicate to filer
         * @return This map value, for chaining
         */
        MapValue.Mutable<K, V> removeAll(Predicate<Map.Entry<K, V>> predicate);

        @Override
        MapValue.Mutable<K, V> set(Map<K, V> value);

        @Override
        MapValue.Mutable<K, V> transform(Function<Map<K, V>, Map<K, V>> function);

        @Override
        MapValue.Mutable<K, V> copy();

        @Override
        default MapValue.Mutable<K, V> asMutable() {
            return this;
        }

        @Override
        default MapValue.Mutable<K, V> asMutableCopy() {
            return this.copy();
        }

        @Override
        MapValue.Immutable<K, V> asImmutable();
    }

    /**
     * Represents a specialized type of {@link Value.Immutable} that is different
     * from an {@link CollectionValue.Immutable} such that the "elements" are
     * {@link java.util.Map.Entry}. Usually, this type of value is used to represent
     * a particular "type" of "key" that is associated to a particular "value".
     *
     * @param <K> The type of the key
     * @param <V> The type of the value
     */
    interface Immutable<K, V> extends MapValue<K, V>, Value.Immutable<Map<K, V>> {

        /**
         * Associates the provided key to the provided value in the new map. If
         * there already exists a value for the provided key, the value is
         * replaced.
         *
         * @param key The key to associate to the value
         * @param value The value associated with the key
         * @return The new value, for chaining
         */
        MapValue.Immutable<K, V> with(K key, V value);

        /**
         * Associates all provided {@link java.util.Map.Entry} along with all pre-existing
         * map entries in a new {@link MapValue.Immutable}.
         *
         * @param map The map of key values to set
         * @return The new value, for chaining
         */
        MapValue.Immutable<K, V> withAll(Map<K, V> map);

        /**
         * Creates a new {@link MapValue.Immutable} without the provided key and the
         * associated value.
         *
         * @param key The key to exclude the association
         * @return The new value, for chaining
         */
        MapValue.Immutable<K, V> without(K key);

        /**
         * Creates a new {@link MapValue.Immutable} without the provided keys and
         * their associated values.
         *
         * @param keys The keys to exclude
         * @return The new value, for chaining
         */
        MapValue.Immutable<K, V> withoutAll(Iterable<K> keys);

        /**
         * Creates a new {@link MapValue.Immutable} such that all entries are
         * filtered by the provided {@link Predicate}, any that return
         * {@code true} are retained in the new value. Elements that return
         * <code>true</code> from {@link Predicate#test(Object)} are kept, and
         * those that return <code>false</code> are excluded.
         *
         * @param predicate The predicate to filter
         * @return The new value, for chaining
         */
        MapValue.Immutable<K, V> withoutAll(Predicate<Map.Entry<K, V>> predicate);

        @Override
        MapValue.Immutable<K, V> with(Map<K, V> value);

        @Override
        MapValue.Immutable<K, V> transform(Function<Map<K, V>, Map<K, V>> function);

        @Override
        MapValue.Mutable<K, V> asMutable();

        @Override
        default MapValue.Mutable<K, V> asMutableCopy() {
            return this.asMutable();
        }

        @Override
        default MapValue.Immutable<K, V> asImmutable() {
            return this;
        }
    }
}