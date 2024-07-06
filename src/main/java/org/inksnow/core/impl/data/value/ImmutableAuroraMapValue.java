package org.inksnow.core.impl.data.value;

import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Streams;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.MapValue;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.key.AuroraKey;

import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class ImmutableAuroraMapValue<K, V> extends AbstractImmutableAuroraValue<Map<K, V>> implements MapValue.Immutable<K, V> {
    public ImmutableAuroraMapValue(Key<? extends Value<Map<K, V>>> key, Map<K, V> element) {
        super(key, element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AuroraKey<? extends MapValue<K, V>, Map<K, V>> key() {
        return (AuroraKey<? extends MapValue<K, V>, Map<K, V>>) super.key();
    }

    @Override
    public int size() {
        return this.element.size();
    }

    @Override
    public boolean containsKey(K key) {
        return this.element.containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return this.element.containsValue(value);
    }

    @Override
    public Set<K> keySet() {
        return this.element.keySet();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.element.entrySet();
    }

    @Override
    public Collection<V> values() {
        return this.element.values();
    }

    @Override
    public MapValue.Immutable<K, V> transform(Function<Map<K, V>, Map<K, V>> function) {
        return this.with(function.apply(this.get()));
    }

    @Override
    public MapValue.Immutable<K, V> with(Map<K, V> value) {
        return this.key().valueConstructor().getImmutable(value).asImmutable();
    }

    @Override
    public MapValue.Immutable<K, V> with(K key, V value) {
        return this.modifyMap(map -> map.put(key, value));
    }

    private MapValue.Immutable<K, V> modifyMap(Consumer<Map<K, V>> consumer) {
        final Map<K, V> map;
        if (this.element instanceof LinkedHashMap) {
            map = new LinkedHashMap<>(this.element);
            consumer.accept(map);
        } else if (this.element instanceof ImmutableMap) {
            final LinkedHashMap<K, V> temp = new LinkedHashMap<>(this.element);
            consumer.accept(temp);
            map = ImmutableMap.copyOf(temp);
        } else {
            map = new HashMap<>(this.element);
            consumer.accept(map);
        }
        return this.key().valueConstructor().getRawImmutable(map).asImmutable();
    }

    @Override
    public MapValue.Immutable<K, V> withAll(Map<K, V> other) {
        return this.modifyMap(map -> map.putAll(other));
    }

    @Override
    public MapValue.Immutable<K, V> without(K key) {
        if (!this.element.containsKey(key)) {
            return this;
        }
        return this.modifyMap(map -> map.remove(key));
    }

    @Override
    public MapValue.Immutable<K, V> withoutAll(Iterable<K> keys) {
        if (Streams.stream(keys).noneMatch(key -> this.element.containsKey(key))) {
            return this;
        }
        return this.modifyMap(map -> keys.forEach(map::remove));
    }

    @Override
    public MapValue.Immutable<K, V> withoutAll(Predicate<Map.Entry<K, V>> predicate) {
        return this.modifyMap(map -> map.entrySet().removeIf(predicate));
    }

    @Override
    public MapValue.Mutable<K, V> asMutable() {
        return new MutableAuroraMapValue<>(this.key(), this.get());
    }
}
