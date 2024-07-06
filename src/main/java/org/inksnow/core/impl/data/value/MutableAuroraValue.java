package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.CopyHelper;

public final class MutableAuroraValue<E> extends AbstractMutableAuroraValue<E> {

    public MutableAuroraValue(Key<? extends Value<E>> key, E element) {
        super(key, element);
    }

    @Override
    public Mutable<E> asMutable() {
        return this;
    }

    @Override
    public Mutable<E> asMutableCopy() {
        return new MutableAuroraValue<>(this.key(), CopyHelper.copy(this.element));
    }

    @Override
    public Immutable<E> asImmutable() {
        return new ImmutableAuroraValue<>(this.key(), CopyHelper.copy(this.element));
    }
}
