package org.inksnow.core.impl.data.value;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.key.AuroraKey;

import java.util.StringJoiner;

public abstract class AuroraValue<E> implements Value<E> {

    protected final AuroraKey<? extends Value<E>, E> key;
    protected E element;

    public AuroraValue(Key<? extends Value<E>> key, E element) {
        this.key = (AuroraKey<? extends Value<E>, E>) key;
        this.element = element;
    }

    @Override
    public E get() {
        return this.element;
    }

    @Override
    public AuroraKey<? extends Value<E>, E> key() {
        return this.key;
    }

    @Override
    public String toString() {
        return new StringJoiner(", ", this.getClass().getSimpleName() + "[", "]")
                .add("key=" + this.key)
                .add("element=" + this.element)
                .toString();
    }
}
