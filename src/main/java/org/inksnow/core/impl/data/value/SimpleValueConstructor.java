package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;

import java.util.function.BiFunction;

final class SimpleValueConstructor<V extends Value<E>, E> implements ValueConstructor<V, E> {

    private final Key<V> key;
    private final BiFunction<Key<V>, E, V> mutableConstructor;
    private final BiFunction<Key<V>, E, V> immutableConstructor;

    public SimpleValueConstructor(Key<V> key,
                                  BiFunction<Key<V>, E, V> mutableConstructor,
                                  BiFunction<Key<V>, E, V> immutableConstructor) {
        this.key = key;
        this.mutableConstructor = mutableConstructor;
        this.immutableConstructor = immutableConstructor;
    }

    @Override
    public V getMutable(E element) {
        return this.mutableConstructor.apply(this.key, element);
    }

    @Override
    public V getRawImmutable(E element) {
        return this.immutableConstructor.apply(this.key, element);
    }
}
