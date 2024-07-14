package org.inksnow.core.impl.data.provider;

import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataTransactionResult;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.value.Value;

public abstract class ImmutableDataProvider<V extends Value<E>, E> extends AbstractDataProvider<V, E> implements DataProvider<V, E> {

    public ImmutableDataProvider(Key<V> key) {
        super(key);
    }

    @Override
    public boolean allowsAsynchronousAccess(DataHolder dataHolder) {
        return false;
    }

    @Override
    public DataTransactionResult offer(DataHolder.Mutable dataHolder, E element) {
        return DataTransactionResult.failResult(Value.immutableOf(this.key(), element));
    }

    @Override
    public DataTransactionResult remove(DataHolder.Mutable dataHolder) {
        return DataTransactionResult.failNoData();
    }
}
