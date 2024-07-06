package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;

public abstract class AbstractMutableAuroraValue<E> extends AuroraValue<E> implements Value.Mutable<E> {

    public AbstractMutableAuroraValue(Key<? extends Value<E>> key, E element) {
        super(key, element);
    }

    @Override
    public Mutable<E> set(E value) {
        this.element = value;
        return this;
    }
}
