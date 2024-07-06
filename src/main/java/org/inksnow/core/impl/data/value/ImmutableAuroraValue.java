package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.CopyHelper;

public final class ImmutableAuroraValue<E> extends AbstractImmutableAuroraValue<E> {
    public ImmutableAuroraValue(Key<? extends Value<E>> key, E element) {
        super(key, element);
    }

    @Override
    public Immutable<E> with(E value) {
        return new ImmutableAuroraValue<>(this.key, value);
    }

    @Override
    public Mutable<E> asMutable() {
        return new MutableAuroraValue<>(this.key, CopyHelper.copy(this.element));
    }

    @Override
    public Mutable<E> asMutableCopy() {
        return new MutableAuroraValue<>(this.key, CopyHelper.copy(this.element));
    }

    @Override
    public Immutable<E> asImmutable() {
        return this;
    }
}
