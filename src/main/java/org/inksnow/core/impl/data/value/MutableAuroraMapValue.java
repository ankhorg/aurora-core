package org.inksnow.core.impl.data.value;

import com.google.common.collect.ImmutableMap;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.MapValue;
import org.inksnow.core.impl.data.key.AuroraKey;
import org.inksnow.core.impl.util.CopyHelper;

import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Set;
import java.util.function.Consumer;
import java.util.function.Function;
import java.util.function.Predicate;

public final class MutableAuroraMapValue<K, V> extends AbstractMutableAuroraValue<Map<K, V>> implements MapValue.Mutable<K, V> {

    public MutableAuroraMapValue(Key<? extends MapValue<K, V>> key, Map<K, V> element) {
        super(key, element);
    }

    @Override
    @SuppressWarnings("unchecked")
    public AuroraKey<? extends MapValue<K, V>, Map<K, V>> key() {
        return (AuroraKey<? extends MapValue<K, V>, Map<K, V>>) super.key();
    }

    @Override
    public int size() {
        return this.get().size();
    }

    @Override
    public boolean containsKey(K key) {
        return this.get().containsKey(key);
    }

    @Override
    public boolean containsValue(V value) {
        return this.get().containsValue(value);
    }

    @Override
    public Set<K> keySet() {
        return this.get().keySet();
    }

    @Override
    public Set<Map.Entry<K, V>> entrySet() {
        return this.get().entrySet();
    }

    @Override
    public Collection<V> values() {
        return this.get().values();
    }

    @Override
    public MapValue.Mutable<K, V> set(Map<K, V> value) {
        super.set(value);
        return this;
    }

    @Override
    public MapValue.Mutable<K, V> put(K key, V value) {
        this.modifyMap(map -> map.put(key, value));
        return this;
    }

    @Override
    public MapValue.Mutable<K, V> putAll(Map<K, V> other) {
        this.modifyMap(map -> map.putAll(other));
        return this;
    }

    @Override
    public MapValue.Mutable<K, V> remove(K key) {
        this.modifyMap(map -> map.remove(key));
        return this;
    }

    @Override
    public MapValue.Mutable<K, V> removeAll(Iterable<K> keys) {
        this.modifyMap(map -> keys.forEach(map::remove));
        return this;
    }

    @Override
    public MapValue.Mutable<K, V> removeAll(Predicate<Map.Entry<K, V>> predicate) {
        this.modifyMap(map -> map.entrySet().removeIf(predicate));
        return this;
    }

    private void modifyMap(Consumer<Map<K, V>> consumer) {
        final Map<K, V> map = this.get();
        if (map instanceof ImmutableMap) {
            final Map<K, V> mutable = new LinkedHashMap<>(map);
            consumer.accept(mutable);
            this.set(ImmutableMap.copyOf(mutable));
        } else {
            consumer.accept(map);
        }
    }

    @Override
    public MapValue.Mutable<K, V> transform(Function<Map<K, V>, Map<K, V>> function) {
        return this.set(function.apply(this.get()));
    }

    @Override
    public MapValue.Mutable<K, V> copy() {
        return new MutableAuroraMapValue<>(this.key(), CopyHelper.copy(this.get()));
    }

    @Override
    public MapValue.Immutable<K, V> asImmutable() {
        return this.key().valueConstructor().getImmutable(this.get()).asImmutable();
    }
}
