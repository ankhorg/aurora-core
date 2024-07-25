package org.inksnow.core.impl.data.manipulator;


import com.google.common.collect.ImmutableSet;
import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.util.CopyHelper;

import java.util.*;

@SuppressWarnings("unchecked")
abstract class AuroraDataManipulator implements DataManipulator {

    protected final Map<Key<?>, Object> values;

    AuroraDataManipulator(final Map<Key<?>, Object> values) {
        this.values = values;
    }

    Map<Key<?>, Object> copyMap() {
        final Map<Key<?>, Object> copy = new HashMap<>();
        for (final Map.Entry<Key<?>, Object> entry : this.values.entrySet()) {
            copy.put(entry.getKey(), CopyHelper.copy(entry.getValue()));
        }
        return copy;
    }

    @Override
    public <E> Optional<E> get(final Key<? extends Value<E>> key) {
        Objects.requireNonNull(key, "key");
        return Optional.ofNullable((E) CopyHelper.copy(this.values.get(key)));
    }

    @Override
    public <E, V extends Value<E>> Optional<V> getValue(final Key<V> key) {
        Objects.requireNonNull(key, "key");
        final E element = (E) CopyHelper.copy(this.values.get(key));
        return element == null ? Optional.empty() : Optional.of(Value.genericMutableOf(key, element));
    }

    @Override
    public boolean supports(final Key<?> key) {
        return true;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public Set<Value.Immutable<?>> getValues() {
        return this.values.entrySet().stream()
                .map(entry -> (Value.Immutable<?>)Value.immutableOf((Key) entry.getKey(), CopyHelper.copy(entry.getValue())).asImmutable())
                .collect(ImmutableSet.toImmutableSet());
    }

    @Override
    public String toString() {
        final StringJoiner builder = new StringJoiner(", ", AuroraDataManipulator.class.getSimpleName() + "[", "]");
        for (final Map.Entry<Key<?>, Object> entry : this.values.entrySet()) {
            builder.add(entry.getKey().resourcePath() + "= " + entry.getValue());
        }
        return builder.toString();
    }
}
