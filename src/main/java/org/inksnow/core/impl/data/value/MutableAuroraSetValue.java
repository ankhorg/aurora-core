package org.inksnow.core.impl.data.value;

import com.google.common.collect.ImmutableSet;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.SetValue;
import org.inksnow.core.impl.data.key.AuroraKey;
import org.inksnow.core.impl.util.CopyHelper;

import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public final class MutableAuroraSetValue<E> extends MutableAuroraCollectionValue<E, Set<E>, SetValue.Mutable<E>, SetValue.Immutable<E>>
        implements SetValue.Mutable<E> {

    public MutableAuroraSetValue(Key<? extends SetValue<E>> key, Set<E> element) {
        super(key, element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AuroraKey<? extends SetValue<E>, Set<E>> key() {
        return (AuroraKey<? extends SetValue<E>, Set<E>>) super.key();
    }

    @Override
    public SetValue.Immutable<E> asImmutable() {
        return this.key().valueConstructor().getImmutable(this.element).asImmutable();
    }

    @Override
    public SetValue.Mutable<E> copy() {
        return new MutableAuroraSetValue<>(this.key(), CopyHelper.copy(this.element));
    }

    @Override
    protected SetValue.Mutable<E> modifyCollection(Consumer<Set<E>> consumer) {
        final Set<E> list = this.element;
        if (list instanceof ImmutableSet) {
            final Set<E> copy = new LinkedHashSet<>(list);
            consumer.accept(copy);
            this.set(ImmutableSet.copyOf(copy));
        } else {
            consumer.accept(list);
        }
        return this;
    }
}
