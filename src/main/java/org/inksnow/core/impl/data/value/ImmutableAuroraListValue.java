package org.inksnow.core.impl.data.value;

import com.google.common.collect.ImmutableList;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.CollectionValue;
import org.inksnow.core.data.value.ListValue;
import org.inksnow.core.impl.data.key.AuroraKey;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Consumer;

public class ImmutableAuroraListValue<E> extends ImmutableAuroraCollectionValue<E, List<E>, ListValue.Immutable<E>, ListValue.Mutable<E>>
        implements ListValue.Immutable<E> {

    public ImmutableAuroraListValue(
            Key<? extends CollectionValue<E, List<E>>> key, List<E> element) {
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
    public ListValue.Immutable<E> with(int index, E value) {
        return this.modifyCollection(list -> list.add(index, value));
    }

    @Override
    public ListValue.Immutable<E> with(int index, Iterable<E> values) {
        return this.modifyCollection(list -> {
            int offset = 0;
            for (final E value : values) {
                list.add(index + offset++, value);
            }
        });
    }

    @Override
    public ListValue.Immutable<E> without(int index) {
        return this.modifyCollection(list -> list.remove(index));
    }

    @Override
    public ListValue.Immutable<E> set(int index, E element) {
        return this.modifyCollection(list -> list.set(index, element));
    }

    @Override
    protected ListValue.Immutable<E> modifyCollection(Consumer<List<E>> consumer) {
        final List<E> list;
        if (this.element instanceof ImmutableList) {
            final List<E> temp = new ArrayList<>(this.element);
            consumer.accept(temp);
            list = ImmutableList.copyOf(temp);
        } else {
            list = new ArrayList<>(this.element);
            consumer.accept(list);
        }
        return this.key().valueConstructor().getRawImmutable(list).asImmutable();
    }

    @Override
    public ListValue.Immutable<E> with(List<E> value) {
        return this.key().valueConstructor().getImmutable(value).asImmutable();
    }

    @Override
    public ListValue.Mutable<E> asMutable() {
        return new MutableAuroraListValue<>(this.key(), this.get());
    }
}
