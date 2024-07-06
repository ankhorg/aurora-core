package org.inksnow.core.data.value;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.key.Key;

import java.util.NoSuchElementException;
import java.util.Objects;
import java.util.Optional;
import java.util.OptionalDouble;
import java.util.OptionalInt;
import java.util.OptionalLong;
import java.util.Set;

@SuppressWarnings("type.argument")
public interface ValueContainer {
    <E> Optional<E> get(Key<? extends Value<E>> key);

    default OptionalInt getInt(final Key<? extends Value<Integer>> key) {
        return this.get(key).map(OptionalInt::of).orElseGet(OptionalInt::empty);
    }

    default OptionalDouble getDouble(final Key<? extends Value<Double>> key) {
        return this.get(key).map(OptionalDouble::of).orElseGet(OptionalDouble::empty);
    }

    default OptionalLong getLong(final Key<? extends Value<Long>> key) {
        return this.get(key).map(OptionalLong::of).orElseGet(OptionalLong::empty);
    }

    default <E> E require(final Key<? extends Value<E>> key) {
        return this.get(key).orElseThrow(() -> new NoSuchElementException(String.format(
            "Could not retrieve value for key '%s'", key)));
    }

    default <E> @Nullable E getOrNull(final Key<? extends Value<E>> key) {
        final Optional<E> value = this.get(key);
        if (value.isPresent()) {
            return value.get();
        }
        if (!this.supports(key)) {
            throw new UnsupportedOperationException("Key not supported. Key: " + key);
        }
        return null;
    }

    default <E> E getOrElse(final Key<? extends Value<E>> key, @Nullable E defaultValue) {
        Preconditions.checkArgument(defaultValue != null, "defaultValue cannot be null");
        return this.get(key).orElse(defaultValue);
    }

    <E, V extends Value<E>> Optional<V> getValue(Key<V> key);

    default <E, V extends Value<E>> V requireValue(final Key<V> key) {
        return this.getValue(key).orElseThrow(() -> new NoSuchElementException(String.format(
            "Could not retrieve value for key '%s'", key)));
    }

    boolean supports(Key<?> key);

    default boolean supports(final Value<?> value) {
        return this.supports(value.key());
    }

    Set<Key<?>> getKeys();

    Set<Value.Immutable<?>> getValues();
}
