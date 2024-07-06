package org.inksnow.core.data.value;

import org.inksnow.core.data.key.Key;

import java.util.Set;

public interface SetValue<E> extends CollectionValue<E, Set<E>> {
    @Override
    Key<? extends SetValue<E>> key();

    @Override
    SetValue.Mutable<E> asMutable();

    @Override
    SetValue.Mutable<E> asMutableCopy();

    @Override
    SetValue.Immutable<E> asImmutable();

    interface Mutable<E> extends SetValue<E>, CollectionValue.Mutable<E, Set<E>> {

    }

    interface Immutable<E> extends SetValue<E>, CollectionValue.Immutable<E, Set<E>> {

    }
}