package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.CopyHelper;

public abstract class AbstractImmutableAuroraValue<E> extends AuroraValue<E> implements Value.Immutable<E> {

    public AbstractImmutableAuroraValue(Key<? extends Value<E>> key, E element) {
        super(key, element);
    }

    @Override
    public E get() {
        return CopyHelper.copy(super.get());
    }
}
