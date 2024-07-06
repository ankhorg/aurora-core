package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.CopyHelper;

import java.util.function.Function;

public final class MutableAuroraValue<E> extends AbstractMutableAuroraValue<E> {

    public MutableAuroraValue(Key<? extends Value<E>> key, E element) {
        super(key, element);
    }

    @Override
    public Mutable<E> transform(Function<E, E> function) {
        return this.set(function.apply(this.get()));
    }

    @Override
    public Immutable<E> asImmutable() {
        return new ImmutableAuroraValue<>(this.key(), CopyHelper.copy(this.element));
    }

    @Override
    public Mutable<E> copy() {
        return new MutableAuroraValue<>(this.key(), CopyHelper.copy(this.element));
    }
}
