package org.inksnow.core.impl.data.value;

import com.google.common.collect.ImmutableSet;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.CollectionValue;
import org.inksnow.core.data.value.SetValue;
import org.inksnow.core.impl.data.key.AuroraKey;

import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.Set;
import java.util.function.Consumer;

public class ImmutableAuroraSetValue<E> extends ImmutableAuroraCollectionValue<E, Set<E>, SetValue.Immutable<E>, SetValue.Mutable<E>>
        implements SetValue.Immutable<E> {

    public ImmutableAuroraSetValue(
            Key<? extends CollectionValue<E, Set<E>>> key, Set<E> element) {
        super(key, element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AuroraKey<? extends SetValue<E>, Set<E>> key() {
        return (AuroraKey<? extends SetValue<E>, Set<E>>) super.key();
    }

    @Override
    protected SetValue.Immutable<E> modifyCollection(Consumer<Set<E>> consumer) {
        final Set<E> set;
        if (this.element instanceof ImmutableSet) {
            final Set<E> temp = new LinkedHashSet<>(this.element);
            consumer.accept(temp);
            set = ImmutableSet.copyOf(temp);
        } else if (this.element instanceof LinkedHashSet) {
            set = new LinkedHashSet<>(this.element);
            consumer.accept(set);
        } else {
            set = new HashSet<>(this.element);
            consumer.accept(set);
        }
        return this.key().valueConstructor().getRawImmutable(set).asImmutable();
    }

    @Override
    public SetValue.Immutable<E> with(Set<E> value) {
        return this.key().valueConstructor().getImmutable(value).asImmutable();
    }

    @Override
    public SetValue.Mutable<E> asMutable() {
        return new MutableAuroraSetValue<>(this.key(), this.get());
    }
}
