package org.inksnow.core.data;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.CollectionValue;
import org.inksnow.core.data.value.MapValue;
import org.inksnow.core.data.value.MergeFunction;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.data.value.ValueContainer;

import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.Function;
import java.util.function.Supplier;

@SuppressWarnings("argument")
public interface DataHolder extends ValueContainer {

    /**
     * Represents a data holder that allows its data to be modified.
     */
    interface Mutable extends DataHolder {
        /**
         * Applies a transformation on the provided {@link Value} such that
         * the return value of {@link Function#apply(Object)} will become the end
         * resulting value set into this {@link Mutable}. It is not
         * necessary that the input is actually present, in which case the
         * {@link Key}ed data is compatible, but not necessarily present. Writing
         * a {@link Function} to properly handle the potential for a null input
         * is required for this method to execute without exception.
         *
         * @param key The key linked to
         * @param function The function to manipulate the value
         * @param <E> The type of value
         * @return The end resulting value
         */
        default <E> DataTransactionResult transform(Key<? extends Value<E>> key, Function<E, E> function) {
            if (this.supports(key)) {
                return this.get(key)
                    .map(function)
                    .map(value -> this.offer(key, value))
                    .orElseGet(DataTransactionResult::failNoData);
            }
            return DataTransactionResult.failNoData();
        }

        /**
         * Applies a transformation on the provided {@link Value} such that
         * the return value of {@link Function#apply(Object)} will become the end
         * resulting value set into this {@link Mutable}. It is not
         * necessary that the input is actually present, in which case the
         * {@link Key}ed data is compatible, but not necessarily present. Writing
         * a {@link Function} to properly handle the potential for a null input
         * is required for this method to execute without exception.
         *
         * @param key The key linked to
         * @param function The function to manipulate the value
         * @param <E> The type of value
         * @return The end resulting value
         */
        default <E> DataTransactionResult transform(Supplier<? extends Key<? extends Value<E>>> key, Function<E, E> function) {
            return this.transform(key.get(), function);
        }

        /**
         * Offers the given {@code value} as defined by the provided {@link Key}
         * such that a {@link DataTransactionResult} is returned for any
         * successful, rejected, and replaced {@link Value}s from this
         * {@link Mutable}.
         *
         * @param key The key to the value to set
         * @param value The value to set
         * @param <E> The type of value
         * @return The transaction result
         */
        <E> DataTransactionResult offer(Key<? extends Value<E>> key, E value);

        /**
         * Offers the given {@code value} as defined by the provided {@link Key}
         * such that a {@link DataTransactionResult} is returned for any
         * successful, rejected, and replaced {@link Value}s from this
         * {@link Mutable}.
         *
         * @param key The key to the value to set
         * @param value The value to set
         * @param <E> The type of value
         * @return The transaction result
         */
        default <E> DataTransactionResult offer(Supplier<? extends Key<? extends Value<E>>> key, E value) {
            return this.offer(key.get(), value);
        }

        /**
         * Offers the given {@code value} as defined by the provided {@link Key}
         * such that a {@link DataTransactionResult} is returned for any
         * successful, rejected, and replaced {@link Value}s from this
         * {@link Mutable}.
         *
         * @param key The key to the value to set
         * @param value The value to set
         * @param <E> The type of value
         * @return The transaction result
         */
        default <E> DataTransactionResult offer(Supplier<? extends Key<? extends Value<E>>> key, Supplier<? extends E> value) {
            return this.offer(key.get(), value.get());
        }

        /**
         * Offers the given {@link Value} as defined by the provided
         * {@link Key} such that a {@link DataTransactionResult} is returned for
         * any successful, rejected, and replaced {@link Value}s from this
         * {@link Mutable}.
         *
         * @param value The value to set
         * @return The transaction result
         */
        DataTransactionResult offer(Value<?> value);

        <E> DataTransactionResult offerSingle(Key<? extends CollectionValue<E, ?>> key, E element);

        default <E> DataTransactionResult offerSingle(Supplier<? extends Key<? extends CollectionValue<E, ?>>> key, E element) {
            return this.offerSingle(key.get(), element);
        }

        <K, V> DataTransactionResult offerSingle(Key<? extends MapValue<K, V>> key, K valueKey, V value);

        default <K, V> DataTransactionResult offerSingle(Supplier<? extends Key<? extends MapValue<K, V>>> key, K valueKey, V value) {
            return this.offerSingle(key.get(), valueKey, value);
        }

        <K, V> DataTransactionResult offerAll(Key<? extends MapValue<K, V>> key, Map<? extends K, ? extends V> map);

        default <K, V> DataTransactionResult offerAll(Supplier<? extends Key<? extends MapValue<K, V>>> key, Map<? extends K, ? extends V> map) {
            return this.offerAll(key.get(), map);
        }

        DataTransactionResult offerAll(MapValue<?, ?> value);

        DataTransactionResult offerAll(CollectionValue<?, ?> value);

        <E> DataTransactionResult offerAll(Key<? extends CollectionValue<E, ?>> key, Collection<? extends E> elements);

        default <E> DataTransactionResult offerAll(Supplier<? extends Key<? extends CollectionValue<E, ?>>> key, Collection<? extends E> elements) {
            return this.offerAll(key.get(), elements);
        }

        <E> DataTransactionResult removeSingle(Key<? extends CollectionValue<E, ?>> key, E element);

        default <E> DataTransactionResult removeSingle(Supplier<? extends Key<? extends CollectionValue<E, ?>>> key, E element) {
            return this.removeSingle(key.get(), element);
        }

        <K> DataTransactionResult removeKey(Key<? extends MapValue<K, ?>> key, K mapKey);

        default <K> DataTransactionResult removeKey(Supplier<? extends Key<? extends MapValue<K, ?>>> key, K mapKey) {
            return this.removeKey(key.get(), mapKey);
        }

        DataTransactionResult removeAll(CollectionValue<?, ?> value);

        <E> DataTransactionResult removeAll(Key<? extends CollectionValue<E, ?>> key, Collection<? extends E> elements);

        default <E> DataTransactionResult removeAll(Supplier<? extends Key<? extends CollectionValue<E, ?>>> key, Collection<? extends E> elements) {
            return this.removeAll(key.get(), elements);
        }

        DataTransactionResult removeAll(MapValue<?, ?> value);

        <K, V> DataTransactionResult removeAll(Key<? extends MapValue<K, V>> key, Map<? extends K, ? extends V> map);

        default <K, V> DataTransactionResult removeAll(Supplier<? extends Key<? extends MapValue<K, V>>> key, Map<? extends K, ? extends V> map) {
            return this.removeAll(key.get(), map);
        }

        /**
         * Offers the given {@code value} as defined by the provided {@link Key}
         * such that a {@link DataTransactionResult} is returned for any
         * successful {@link Value}s from this {@link Mutable}.
         * Intentionally, however, this differs from {@link #offer(Key, Object)}
         * as it will intentionally throw an exception if the result was a failure.
         *
         * @param key The key to the value to set
         * @param value The value to set
         * @param <E> The type of value
         * @return The transaction result
         * @throws IllegalArgumentException If the result is a failure likely due to
         *     incompatibility
         */
        <E> DataTransactionResult tryOffer(Key<? extends Value<E>> key, E value);

        /**
         * Offers the given {@code value} as defined by the provided {@link Key}
         * such that a {@link DataTransactionResult} is returned for any
         * successful {@link Value}s from this {@link Mutable}.
         * Intentionally, however, this differs from {@link #offer(Key, Object)}
         * as it will intentionally throw an exception if the result was a failure.
         *
         * @param key The key to the value to set
         * @param value The value to set
         * @param <E> The type of value
         * @return The transaction result
         * @throws IllegalArgumentException If the result is a failure likely due to
         *     incompatibility
         */
        default <E> DataTransactionResult tryOffer(Supplier<? extends Key<? extends Value<E>>> key, E value) {
            return this.tryOffer(key.get(), value);
        }

        /**
         * Offers the given {@code value} as defined by the provided {@link Key}
         * such that a {@link DataTransactionResult} is returned for any
         * successful {@link Value}s from this {@link Mutable}.
         * Intentionally, however, this differs from {@link #offer(Key, Object)}
         * as it will intentionally throw an exception if the result was a failure.
         *
         * @param value The value to set
         * @param <E> The type of value
         * @return The transaction result
         * @throws IllegalArgumentException If the result is a failure likely due to
         *     incompatibility
         */
        default <E> DataTransactionResult tryOffer(Value<E> value) throws IllegalArgumentException {
            final DataTransactionResult result = this.offer(value.key(), value.get());
            if (!result.isSuccessful()) {
                throw new IllegalArgumentException("Failed offer transaction!");
            }
            return result;
        }

        /**
         * Attempts to remove the provided {@link Value}. All values that were
         * successfully removed will be provided in
         * {@link DataTransactionResult#replacedData()}. If the data can not be
         * removed, the result will be an expected
         * {@link DataTransactionResult.Type#FAILURE}.
         *
         * @param value The value to remove
         * @return The transaction result
         */
        default DataTransactionResult remove(Value<?> value) {
            return this.remove(value.key());
        }

        /**
         * Attempts to remove the data associated with the provided {@link Key}.
         * All values that were successfully removed will be provided in
         * {@link DataTransactionResult#replacedData()}. If the data can not be
         * removed, the result will be an expected
         * {@link DataTransactionResult.Type#FAILURE}.
         *
         * @param key The key of the data
         * @return The transaction result
         */
        DataTransactionResult remove(Key<?> key);

        /**
         * Attempts to remove the data associated with the provided {@link Key}.
         * All values that were successfully removed will be provided in
         * {@link DataTransactionResult#replacedData()}. If the data can not be
         * removed, the result will be an expected
         * {@link DataTransactionResult.Type#FAILURE}.
         *
         * @param key The key of the data
         * @return The transaction result
         */
        default DataTransactionResult remove(Supplier<? extends Key<?>> key) {
            return this.remove(key.get());
        }

        /**
         * Attempts to "revert" a {@link DataTransactionResult} such that any
         * of the {@link DataTransactionResult#replacedData()} are offered
         * back, and any {@link DataTransactionResult#successfulData()} are
         * removed if they were not the same types as any exising in the
         * replaced values.
         *
         * @param result The result to undo
         * @return The result of the undo
         */
        DataTransactionResult undo(DataTransactionResult result);

        /**
         * Performs an absolute copy of all {@link Value.Mutable}s and
         * {@link ValueContainer}s to this {@link Mutable} such that
         * any overlapping {@link Value.Mutable}s are offered for replacement. The
         * result is provided as a {@link DataTransactionResult}.
         *
         * @param that The other {@link Mutable} to copy values from
         * @return The transaction result
         */
        default DataTransactionResult copyFrom(ValueContainer that) {
            return this.copyFrom(that, MergeFunction.REPLACEMENT_PREFERRED);
        }

        /**
         * Performs an absolute copy of all {@link Value.Mutable}s and
         * {@link ValueContainer}s to this {@link Mutable} such that
         * any overlapping {@link Value.Mutable}s are offered for replacement. The
         * result is provided as a {@link DataTransactionResult}.
         *
         * @param that The other {@link Mutable} to copy values from
         * @param function The function to resolve merge conflicts
         * @return The transaction result
         */
        DataTransactionResult copyFrom(ValueContainer that, MergeFunction function);
    }

    /**
     * Represents a {@link DataHolder} that is immutable and can be transformed
     * into other immutable data holders.
     */
    interface Immutable<I extends Immutable<I>> extends DataHolder {

        /**
         * Applies a transformation on the provided {@link Value} such that
         * the return value of {@link Function#apply(Object)} will become the end
         * resulting value set into the newly created {@link Immutable}.
         *
         * @param key The key linked to
         * @param function The function to manipulate the value
         * @param <E> The type of value
         * @return The newly created immutable value store
         */
        <E> Optional<I> transform(Key<? extends Value<E>> key, Function<E, E> function);

        /**
         * Applies a transformation on the provided {@link Value} such that
         * the return value of {@link Function#apply(Object)} will become the end
         * resulting value set into the newly created {@link Immutable}.
         *
         * @param key The key linked to
         * @param function The function to manipulate the value
         * @param <E> The type of value
         * @return The newly created immutable value store
         */
        default <E> Optional<I> transform(Supplier<? extends Key<? extends Value<E>>> key, Function<E, E> function) {
            return this.transform(key.get(), function);
        }

        /**
         * Creates a new {@link Immutable} with the provided
         * value by {@link Key}. If the key is supported by this value store,
         * the returned value store will be present.
         *
         * @param key The key to the value to set
         * @param value The value to set
         * @param <E> The type of value
         * @return The new immutable value store
         */
        <E> Optional<I> with(Key<? extends Value<E>> key, E value);

        /**
         * Creates a new {@link Immutable} with the provided
         * value by {@link Key}. If the key is supported by this value store,
         * the returned value store will be present.
         *
         * @param key The key to the value to set
         * @param value The value to set
         * @param <E> The type of value
         * @return The new immutable value store
         */
        default <E> Optional<I> with(Supplier<? extends Key<? extends Value<E>>> key, E value) {
            return this.with(key.get(), value);
        }

        /**
         * Offers the given {@code value} as defined by the provided {@link Key}
         * such that if the {@link Key} is supported, a new
         * {@link Immutable} is created.
         *
         * @param value The value to set
         * @return The new immutable value store
         */
        Optional<I> with(Value<?> value);

        /**
         * Creates a new {@link Immutable} without the key of the provided
         * {@link Value}. If the key is supported by this value store,
         * the returned value store will be present.
         *
         * @param value The value
         * @return The new immutable value store
         */
        default Optional<I> without(Value<?> value) {
            return this.without(value.key());
        }

        /**
         * Creates a new {@link Immutable} without the provided {@link Key}. If the
         * key is supported by this value store, the returned value store will
         * be present.
         *
         * @param key The key to remove
         * @return The new immutable value store
         */
        Optional<I> without(Key<?> key);

        /**
         * Creates a new {@link Immutable} without the provided {@link Key}. If the
         * key is supported by this value store, the returned value store will
         * be present.
         *
         * @param key The key to remove
         * @return The new immutable value store
         */
        default Optional<I> without(Supplier<? extends Key<?>> key) {
            return this.without(key.get());
        }

        /**
         * Attempts to merge the {@link Value.Immutable}s from this
         * {@link Immutable} and the given {@link Immutable} to
         * produce a new instance of the merged result.
         *
         * @param that The other immutable value store to gather values from
         * @return The new immutable value store instance
         */
        default I mergeWith(I that) {
            return this.mergeWith(that, MergeFunction.REPLACEMENT_PREFERRED);
        }

        /**
         * Attempts to merge the {@link Value.Immutable}s from this
         * {@link Immutable} and the given {@link Immutable} to
         * produce a new instance of the merged result. Any overlapping
         * {@link ValueContainer}s are merged through the {@link MergeFunction}.
         *
         * @param that The other immutable value store to gather values from
         * @param function The function to resolve merge conflicts
         * @return The new immutable value store instance
         */
        I mergeWith(I that, MergeFunction function);
    }
}
