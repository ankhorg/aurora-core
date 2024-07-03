package org.inksnow.core.data.value;

import org.inksnow.core.data.key.Key;

/**
 * The abstract base interface for all of the "Value API". In short, a
 * {@link Value} is a "wrapper" around an actual value from a
 * {@link ValueContainer}. The actual value may come from various sources of
 * the {@link ValueContainer}, but usually it's a generic dynamic system for
 * being able to fetch values from object fields without having to know the
 * type of {@link Class} of the {@link ValueContainer}, the getters and
 * setters for that particular value. The driving force behind this is that
 * instead of having a traditional hierarchical structure of data that is
 * possible to be retrieved from an {@link Entity}, {@link Living}, etc.,
 * all that is required is <pre>{@code container.supports(Keys.HEALTH) ?
 * container.get(Keys.HEALTH).get() : 0 }</pre> where the container is simply
 * a {@link ValueContainer}, nothing more, nothing less.
 *
 * <p>The advantage of this is that now, these various known and unknown
 * {@link Mutable}s can be retrieved by simple java generics:
 * {@link ValueContainer#getValue(Key)}. While having a {@link Mutable} for
 * something so primitive as the current health of a {@link Living} entity,
 * the power is wielded when a {@link Mutable} can be offered up to multiple
 * {@link ValueContainer}s without worrying about whether it's supported or not,
 * or getting the right cast information.</p>
 *
 * @param <E> The type of element wrapped by this value
 */
public interface Value<E> {
    /**
     * Gets the held value.
     *
     * @return The held value
     */
    Key<? extends Value<E>> key();

    /**
     * Gets the key for this {@link Value}.
     *
     * @return The key for this value
     */
    E get();

    /**
     * Represents a type of {@link Value} that is mutable. Simply put, the
     * underlying value can always be changed without creating a new {@link Mutable}.
     *
     * @param <E> The type of element
     */
    interface Mutable<E> extends Value<E> {
        /**
         * Sets the underlying value to the provided {@code value}.
         *
         * @param value The value to set
         * @return The owning {@link ValueContainer}
         */
        Mutable<E> set(E value);
    }

    /**
     * Represents an immutable representation of a {@link Value} where any
     * modifications of the underlying value result in a new instance of an
     * {@link Immutable} and/or the {@link ValueContainer} if the
     * {@link ValueContainer} too is immutable.
     *
     * <p>The basis for immutability is that once created, the value can not be
     * changed for any reason. Change requires a new instance to be created. As the
     * {@link Immutable} always has a {@link ValueContainer}, it is
     * recommended that the owning {@link ValueContainer} too is immutable, unless
     * the {@link Immutable} is being passed around for data processing. The
     * underlying value of an {@link Immutable} may be itself mutable, however
     * utilizing any provided methods by any of the {@link Immutable} classes
     * is recommended.</p>
     *
     * @param <E> The type of value
     */
    interface Immutable<E> extends Value<E> {

    }

    /**
     * A factory for creating {@link Mutable} and {@link Immutable} instances of {@link Value}s.
     */
    interface Factory {
        /**
         * Creates a new {@link Mutable} instance of the provided {@code element}.
         *
         * @param key The key
         * @param element The element
         * @param <V> The type of value
         * @param <E> The type of element
         * @return The new mutable value
         */
        <V extends Value<E>, E> V mutableOf(Key<V> key, E element);

        /**
         * Creates a new {@link Immutable} instance of the provided {@code element}.
         *
         * @param key The key
         * @param element The element
         * @param <V> The type of value
         * @param <E> The type of element
         * @return The new immutable value
         */
        <V extends Value<E>, E> V immutableOf(Key<V> key, E element);
    }
}
