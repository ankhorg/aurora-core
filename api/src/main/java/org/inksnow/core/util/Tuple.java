package org.inksnow.core.util;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.checkerframework.checker.nullness.qual.Nullable;

import java.util.Objects;
import java.util.StringJoiner;

/**
 * A tuple of objects.
 *
 * @param <K> The key
 * @param <V> The value
 */
public class Tuple<K, V> {

    /**
     * Creates a new {@link Tuple} with the desired {@code first} and
     * {@code second} objects.
     *
     * @param first The first object
     * @param second The second object
     * @param <K> The type of first object
     * @param <V> The type of second object
     * @return The new Tuple
     */
    public static <K, V> Tuple<K, V> of(final @NonNull K first, final @NonNull V second) {
        return new Tuple<>(first, second);
    }

    private final K first;
    private final V second;

    /**
     * Creates a new {@link Tuple}.
     *
     * @param first The first object
     * @param second The second object
     */
    public Tuple(final @NonNull K first, final @NonNull V second) {
        this.first = Objects.requireNonNull(first);
        this.second = Objects.requireNonNull(second);
    }

    /**
     * Gets the first object, otherwise known as "key".
     *
     * @return The first object
     */
    public K first() {
        return this.first;
    }

    /**
     * Gets the second object, otherwise known as "value".
     *
     * @return The value
     */
    public V second() {
        return this.second;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", Tuple.class.getSimpleName() + "[", "]")
            .add("first=" + this.first)
            .add("second=" + this.second)
            .toString();
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.first, this.second);
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean equals(final @Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || this.getClass() != obj.getClass()) {
            return false;
        }
        final Tuple other = (Tuple) obj;
        return Objects.equals(this.first, other.first)
                && Objects.equals(this.second, other.second);
    }
}
