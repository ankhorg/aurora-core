package org.inksnow.core.impl.data.holder;

import org.inksnow.core.data.holder.ChunkDataHolder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;

import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;

@SuppressWarnings({"type.argument", "unchecked"})
public class SimpleMemoryDataHolder implements ChunkDataHolder {
    private final Map<Key<?>, Value<?>> values = new LinkedHashMap<>();

    @Override
    public <E> Optional<E> get(Key<? extends Value<E>> key) {
        return Optional.ofNullable((Value<E>) values.get(key)).map(Value::get);
    }

    @Override
    public <E, V extends Value<E>> Optional<V> getValue(Key<V> key) {
        return Optional.ofNullable((V) values.get(key));
    }

    @Override
    public boolean supports(Key<?> key) {
        return values.containsKey(key);
    }

    @Override
    public Set<Key<?>> getKeys() {
        return Collections.unmodifiableSet(values.keySet());
    }

    @Override
    public Set<Value.Immutable<?>> getValues() {
        return Collections.unmodifiableSet(values.values()
                .stream()
                .map(Value::asImmutable)
                .collect(LinkedHashSet::new, Set::add, Set::addAll));
    }
}
