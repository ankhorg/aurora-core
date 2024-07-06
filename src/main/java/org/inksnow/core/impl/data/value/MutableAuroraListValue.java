package org.inksnow.core.impl.data.value;

import com.google.common.collect.ImmutableList;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.ListValue;
import org.inksnow.core.impl.data.key.AuroraKey;
import org.inksnow.core.impl.util.CopyHelper;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public final class MutableAuroraListValue<E> extends MutableAuroraCollectionValue<E, List<E>, ListValue.Mutable<E>, ListValue.Immutable<E>>
        implements ListValue.Mutable<E> {

    public MutableAuroraListValue(Key<? extends ListValue<E>> key, List<E> element) {
        super(key, element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AuroraKey<? extends ListValue<E>, List<E>> key() {
        return (AuroraKey<? extends ListValue<E>, List<E>>) super.key();
    }

    @Override
    public E get(int index) {
        return this.element.get(index);
    }

    @Override
    public int indexOf(E element) {
        return this.element.indexOf(element);
    }

    @Override
    public ListValue.Mutable<E> add(int index, E value) {
        return this.modifyCollection(list -> list.add(index, value));
    }

    @Override
    public ListValue.Mutable<E> add(int index, Iterable<E> values) {
        return this.modifyCollection(list -> {
            int offset = 0;
            for (final E value : values) {
                list.add(index + offset++, value);
            }
        });
    }

    @Override
    public ListValue.Mutable<E> remove(int index) {
        return this.modifyCollection(list -> list.remove(index));
    }

    @Override
    public ListValue.Mutable<E> set(int index, E element) {
        return this.modifyCollection(list -> list.set(index, element));
    }

    @Override
    public ListValue.Immutable<E> asImmutable() {
        return this.key().valueConstructor().getImmutable(this.element).asImmutable();
    }

    @Override
    public ListValue.Mutable<E> copy() {
        return new MutableAuroraListValue<>(this.key(), CopyHelper.copy(this.element));
    }

    @Override
    protected ListValue.Mutable<E> modifyCollection(Consumer<List<E>> consumer) {
        final List<E> list = this.element;
        if (list instanceof ImmutableList) {
            final List<E> copy = new ArrayList<>(list);
            consumer.accept(copy);
            this.set(ImmutableList.copyOf(copy));
        } else {
            consumer.accept(list);
        }
        return this;
    }
}
