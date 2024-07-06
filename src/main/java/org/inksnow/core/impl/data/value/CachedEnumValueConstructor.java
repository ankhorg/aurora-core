package org.inksnow.core.impl.data.value;

import org.checkerframework.checker.nullness.qual.NonNull;
import org.inksnow.core.data.value.Value;

@SuppressWarnings("unchecked")
final class CachedEnumValueConstructor<V extends Value<E>, E extends Enum<E>> implements ValueConstructor<V, E> {

    private final ValueConstructor<V, E> original;
    private final V[] immutableValues;

    public CachedEnumValueConstructor(final ValueConstructor<V, E> original, final Class<E> enumType) {
        this.original = original;
        @SuppressWarnings("assignment") final E @NonNull [] constants = enumType.getEnumConstants();
        this.immutableValues = (V[]) new Value<?>[constants.length];
        for (int i = 0; i < constants.length; i++) {
            this.immutableValues[i] = this.original.getImmutable(constants[i]);
        }
    }

    @Override
    public V getMutable(final E element) {
        return this.original.getMutable(element);
    }

    @Override
    public V getImmutable(final E element) {
        return this.immutableValues[element.ordinal()];
    }

    @Override
    public V getRawImmutable(final E element) {
        return this.getImmutable(element);
    }
}
