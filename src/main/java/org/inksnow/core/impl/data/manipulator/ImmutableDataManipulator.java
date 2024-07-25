package org.inksnow.core.impl.data.manipulator;


import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;

import java.util.Map;
import java.util.Objects;
import java.util.Set;

final class ImmutableDataManipulator extends AuroraDataManipulator implements DataManipulator.Immutable {

    private @Nullable Set<Value.Immutable<?>> cachedValues;

    ImmutableDataManipulator(final Map<Key<?>, Object> values) {
        super(values);
    }

    @Override
    public Mutable asMutableCopy() {
        return new MutableDataManipulator(this.copyMap());
    }

    @Override
    public Immutable without(final Key<?> key) {
        Objects.requireNonNull(key, "key");
        if (!this.values.containsKey(key)) {
            return this;
        }
        return DataManipulator.Immutable.super.without(key);
    }

    @Override
    public Mutable asMutable() {
        return this.asMutableCopy();
    }

    @Override
    public Set<Key<?>> getKeys() {
        return this.values.keySet();
    }

    @Override
    public Set<Value.Immutable<?>> getValues() {
        if (this.cachedValues == null) {
            this.cachedValues = super.getValues();
        }
        return this.cachedValues;
    }
}
