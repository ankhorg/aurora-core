package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.CopyHelper;

import java.util.function.Function;

public final class ImmutableAuroraValue<E> extends AbstractImmutableAuroraValue<E> {
    public ImmutableAuroraValue(Key<? extends Value<E>> key, E element) {
        super(key, element);
    }

    @Override
    public Immutable<E> with(E value) {
        return this.key().valueConstructor().getImmutable(value).asImmutable();
    }

    @Override
    public Immutable<E> transform(Function<E, E> function) {
        return this.with(function.apply(this.get()));
    }

    @Override
    public Mutable<E> asMutable() {
        return new MutableAuroraValue<>(this.key, CopyHelper.copy(this.element));
    }
}
