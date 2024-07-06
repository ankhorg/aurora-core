package org.inksnow.core.data.value;

import org.inksnow.core.data.key.Key;

import java.util.List;

public interface ListValue<E> extends CollectionValue<E, List<E>> {
    @Override
    Key<? extends ListValue<E>> key();

    @Override
    ListValue.Mutable<E> asMutable();

    @Override
    ListValue.Mutable<E> asMutableCopy();

    @Override
    ListValue.Immutable<E> asImmutable();

    interface Mutable<E> extends ListValue<E>, CollectionValue.Mutable<E, List<E>> {

    }

    interface Immutable<E> extends ListValue<E>, CollectionValue.Immutable<E, List<E>> {

    }
}
