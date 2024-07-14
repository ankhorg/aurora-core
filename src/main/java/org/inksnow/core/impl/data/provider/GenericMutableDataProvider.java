package org.inksnow.core.impl.data.provider;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class GenericMutableDataProvider<H, E> extends GenericMutableDataProviderBase<H, Value<E>, E> {

    public GenericMutableDataProvider(final Key<? extends Value<E>> key) {
        super((Key<Value<E>>) key);
    }

    public GenericMutableDataProvider(final Supplier<? extends Key<? extends Value<E>>> key) {
        this(key.get());
    }

    public GenericMutableDataProvider(final Key<? extends Value<E>> key, final Class<H> holderType) {
        super((Key<Value<E>>) key, holderType);
    }

    public GenericMutableDataProvider(final Supplier<? extends Key<? extends Value<E>>> key, final Class<H> holderType) {
        this(key.get(), holderType);
    }
}
