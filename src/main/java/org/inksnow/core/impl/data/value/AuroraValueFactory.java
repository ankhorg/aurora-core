package org.inksnow.core.impl.data.value;

import jakarta.inject.Singleton;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.key.AuroraKey;

@Singleton
public class AuroraValueFactory implements Value.Factory {
    @Override
    public <V extends Value<E>, E> V mutableOf(Key<V> key, E element) {
        return ((AuroraKey<V, E>) key).valueConstructor().getMutable(element);
    }

    @Override
    public <V extends Value<E>, E> V immutableOf(Key<V> key, E element) {
        return ((AuroraKey<V, E>) key).valueConstructor().getImmutable(element);
    }
}
