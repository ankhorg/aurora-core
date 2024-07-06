package org.inksnow.core.impl.data.provider;

import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.value.Value;

import java.lang.reflect.Type;
import java.util.Optional;

public final class EmptyDataProvider<V extends Value<E>, E> implements DataProvider<V, E> {

    private final Key<V> key;

    public EmptyDataProvider(Key<V> key) {
        this.key = key;
    }

    @Override
    public Key<V> key() {
        return this.key;
    }

    @Override
    public boolean allowsAsynchronousAccess(final DataHolder dataHolder) {
        return false;
    }

    @Override
    public Optional<E> get(final DataHolder dataHolder) {
        return Optional.empty();
    }

    @Override
    public boolean isSupported(final DataHolder dataHolder) {
        return false;
    }

    @Override
    public boolean isSupported(final Type dataHolder) {
        return false;
    }

    @Override
    public DataTransactionResult offer(final DataHolder.Mutable dataHolder, final E element) {
        return DataTransactionResult.failResult(Value.immutableOf(this.key, element));
    }

    @Override
    public DataTransactionResult remove(final DataHolder.Mutable dataHolder) {
        return DataTransactionResult.failNoData();
    }

    @Override
    public <I extends DataHolder.Immutable<I>> Optional<I> with(final I immutable, final E element) {
        return Optional.empty();
    }

    @Override
    public <I extends DataHolder.Immutable<I>> Optional<I> without(final I immutable) {
        return Optional.of(immutable);
    }
}
