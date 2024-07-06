package org.inksnow.core.data.value;

import java.util.Collection;

public interface CollectionValue<E, C extends Collection<E>> extends Value<C>, Iterable<E> {
    interface Mutable<E, C extends Collection<E>> extends CollectionValue<E, C>, Value.Mutable<C> {

    }

    interface Immutable<E, C extends Collection<E>> extends CollectionValue<E, C>, Value.Immutable<C> {

    }
}