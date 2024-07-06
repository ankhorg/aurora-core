package org.inksnow.core.data.value;

import org.inksnow.core.data.key.Key;

import java.util.Set;
import java.util.function.Supplier;

public interface SetValue<E> extends CollectionValue<E, Set<E>> {
    /**
     * Constructs a mutable {@link SetValue} of the appropriate type based
     * on the given {@link Key} and the element.
     *
     * @param key The key
     * @param element The element
     * @param <E> The element type
     * @return The constructed mutable value
     */
    static <E> SetValue.Mutable<E> mutableOf(Key<? extends SetValue<E>> key, Set<E> element) {
        return Value.mutableOf(key, element);
    }

    /**
     * Constructs a mutable {@link SetValue} of the appropriate type based
     * on the given {@link Key} and the element.
     *
     * @param key The key
     * @param element The element
     * @param <E> The element type
     * @return The constructed mutable value
     */
    static <E> SetValue.Mutable<E> mutableOf(Supplier<? extends Key<? extends SetValue<E>>> key, Set<E> element) {
        return SetValue.mutableOf(key.get(), element);
    }

    /**
     * Constructs an immutable {@link SetValue} of the appropriate type based
     * on the given {@link Key} and the element.
     *
     * @param key The key
     * @param element The element
     * @param <E> The element type
     * @return The constructed immutable value
     */
    static <E> SetValue.Immutable<E> immutableOf(Key<? extends SetValue<E>> key, Set<E> element) {
        return Value.immutableOf(key, element);
    }

    /**
     * Constructs an immutable {@link SetValue} of the appropriate type based
     * on the given {@link Key} and the element.
     *
     * @param key The key
     * @param element The element
     * @param <E> The element type
     * @return The constructed immutable value
     */
    static <E> SetValue.Immutable<E> immutableOf(Supplier<? extends Key<? extends SetValue<E>>> key, Set<E> element) {
        return SetValue.immutableOf(key.get(), element);
    }

    @Override
    Key<? extends SetValue<E>> key();

    @Override
    SetValue.Mutable<E> asMutable();

    @Override
    SetValue.Mutable<E> asMutableCopy();

    @Override
    SetValue.Immutable<E> asImmutable();

    /**
     * Represents a type of {@link CollectionValue.Mutable} backed by a {@link Set}. The
     * reasoning is that a {@link Set} retains no ordering of the elements it
     * contains.
     *
     * @param <E> The type of elements supported
     */
    interface Mutable<E> extends SetValue<E>, CollectionValue.Mutable<E, Set<E>, Mutable<E>, Immutable<E>> {

        @Override
        default SetValue.Mutable<E> asMutable() {
            return this;
        }

        @Override
        default SetValue.Mutable<E> asMutableCopy() {
            return this.copy();
        }

        @Override
        SetValue.Immutable<E> asImmutable();
    }

    /**
     * Represents a type of {@link CollectionValue.Immutable} backed by a
     * {@link Set}. The reasoning is that a {@link Set} retains no ordering of the
     * elements it contains.
     *
     * @param <E> The type of elements supported
     */
    interface Immutable<E> extends SetValue<E>, CollectionValue.Immutable<E, Set<E>, Immutable<E>, Mutable<E>> {

        @Override
        SetValue.Mutable<E> asMutable();

        @Override
        default SetValue.Mutable<E> asMutableCopy() {
            return this.asMutable();
        }

        @Override
        default SetValue.Immutable<E> asImmutable() {
            return this;
        }
    }
}