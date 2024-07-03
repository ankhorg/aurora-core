package org.inksnow.core.impl.util;

import lombok.RequiredArgsConstructor;
import org.checkerframework.checker.nullness.qual.Nullable;

@RequiredArgsConstructor
public final class IdentityBox<T> {
    private final T value;

    public T get() {
        return value;
    }

    @Override
    public boolean equals(@Nullable Object obj) {
        if (this == obj) {
            return true;
        }
        if (obj == null || getClass() != obj.getClass()) {
            return false;
        }
        final IdentityBox<?> that = (IdentityBox<?>) obj;
        return value == that.value;
    }

    @Override
    public int hashCode() {
        return System.identityHashCode(value);
    }

    @Override
    public String toString() {
        return value.toString();
    }
}
