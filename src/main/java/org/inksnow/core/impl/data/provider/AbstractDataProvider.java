package org.inksnow.core.impl.data.provider;

import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.value.Value;

public abstract class AbstractDataProvider<V extends Value<E>, E> implements DataProvider<V, E> {

    private final Key<V> key;

    public AbstractDataProvider(Key<V> key) {
        this.key = key;
    }

    @Override
    public Key<V> key() {
        return this.key;
    }

    @Override
    public boolean allowsAsynchronousAccess(DataHolder dataHolder) {
        return false;
    }

    interface KnownHolderType {

        Class<?> getHolderType();
    }
}
