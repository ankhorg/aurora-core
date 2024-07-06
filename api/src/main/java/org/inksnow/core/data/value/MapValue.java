package org.inksnow.core.data.value;

import org.inksnow.core.data.key.Key;

import java.util.Map;

public interface MapValue<K, V> extends Value<Map<K, V>> {
    @Override
    Key<? extends MapValue<K, V>> key();

    @Override
    MapValue.Mutable<K, V> asMutable();

    @Override
    MapValue.Mutable<K, V> asMutableCopy();

    @Override
    MapValue.Immutable<K, V> asImmutable();

    interface Mutable<K, V> extends MapValue<K, V>, Value.Mutable<Map<K, V>> {

    }

    interface Immutable<K, V> extends MapValue<K, V>, Value.Immutable<Map<K, V>> {

    }
}