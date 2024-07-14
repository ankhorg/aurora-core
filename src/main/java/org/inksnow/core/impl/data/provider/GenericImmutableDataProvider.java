package org.inksnow.core.impl.data.provider;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;

import java.util.function.Supplier;

@SuppressWarnings("unchecked")
public abstract class GenericImmutableDataProvider<H, E> extends GenericImmutableDataProviderBase<H, Value<E>, E> {

    public GenericImmutableDataProvider(final Key<? extends Value<E>> key) {
        super((Key<Value<E>>) key);
    }

    public GenericImmutableDataProvider(final Key<? extends Value<E>> key, final Class<H> target) {
        super((Key<Value<E>>) key, target);
    }

    public GenericImmutableDataProvider(final Supplier<? extends Key<? extends Value<E>>> key) {
        this(key.get());
    }

    public GenericImmutableDataProvider(final Supplier<? extends Key<? extends Value<E>>> key, final Class<H> target) {
        this(key.get(), target);
    }
}
