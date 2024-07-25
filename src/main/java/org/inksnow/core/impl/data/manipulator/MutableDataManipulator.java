package org.inksnow.core.impl.data.manipulator;


import com.google.common.collect.ImmutableSet;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.MergeFunction;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.data.value.ValueContainer;
import org.inksnow.core.impl.util.CopyHelper;
import org.inksnow.core.impl.util.DataUtil;

import java.util.*;
import java.util.function.Function;
import java.util.function.Predicate;

@SuppressWarnings("unchecked")
final class MutableDataManipulator extends AuroraDataManipulator implements DataManipulator.Mutable {

    MutableDataManipulator() {
        super(new HashMap<>());
    }

    MutableDataManipulator(final Map<Key<?>, Object> values) {
        super(values);
    }

    @Override
    public Mutable asMutableCopy() {
        return this.copy();
    }

    @Override
    public Immutable asImmutable() {
        return new ImmutableDataManipulator(Collections.unmodifiableMap(this.copyMap()));
    }

    @Override
    public Mutable copyFrom(final ValueContainer valueContainer, final MergeFunction overlap,
                            final Predicate<Key<?>> predicate) {
        Objects.requireNonNull(valueContainer, "valueContainer");
        Objects.requireNonNull(predicate, "predicate");
        Objects.requireNonNull(overlap, "overlap");
        if (valueContainer instanceof AuroraDataManipulator) {
            // Do this to prevent unnecessary object allocations
            final AuroraDataManipulator manipulator = (AuroraDataManipulator) valueContainer;
            for (final Map.Entry<Key<?>, Object> entry : manipulator.values.entrySet()) {
                if (!predicate.test(entry.getKey())) {
                    continue;
                }
                if (overlap == MergeFunction.REPLACEMENT_PREFERRED) {
                    this.values.put(entry.getKey(), CopyHelper.copy(entry.getValue()));
                } else {
                    final @Nullable Object original = this.values.get(entry.getKey());
                    if (overlap == MergeFunction.ORIGINAL_PREFERRED && original != null) {
                        // Prefer the original
                        continue;
                    }
                    final Object merged = DataUtil.merge(overlap, (Key) entry.getKey(), original, entry.getValue());
                    this.values.put(entry.getKey(), CopyHelper.copy(merged));
                }
            }
        } else {
            for (final Key<?> key : valueContainer.getKeys()) {
                if (!predicate.test(key)) {
                    continue;
                }
                if (overlap == MergeFunction.REPLACEMENT_PREFERRED) {
                    this.values.put(key, CopyHelper.copy(valueContainer.require((Key) key)));
                } else {
                    final @Nullable Object original = this.values.get(key);
                    if (overlap == MergeFunction.ORIGINAL_PREFERRED && original != null) {
                        // Prefer the original
                        continue;
                    }
                    final Object merged = DataUtil.merge(overlap, (Key) key, original,
                        valueContainer.require((Key) key));
                    this.values.put(key, CopyHelper.copy(merged));
                }
            }
        }
        return this;
    }

    @Override
    public Mutable copyFrom(final ValueContainer valueContainer, final MergeFunction overlap,
        final Iterable<Key<?>> keys) {
        Objects.requireNonNull(valueContainer, "valueContainer");
        Objects.requireNonNull(overlap, "overlap");
        Objects.requireNonNull(keys, "keys");
        if (valueContainer instanceof AuroraDataManipulator) {
            // Do this to prevent unnecessary object allocations
            final AuroraDataManipulator manipulator = (AuroraDataManipulator) valueContainer;
            MutableDataManipulator.copyFrom(this.values, overlap, keys, manipulator.values::get);
        } else {
            MutableDataManipulator.copyFrom(this.values, overlap, keys, key -> valueContainer.get((Key) key).orElse(null));
        }
        return this;
    }

    @Override
    public Mutable copyFrom(final ValueContainer valueContainer, final MergeFunction overlap) {
        Objects.requireNonNull(valueContainer, "valueContainer");
        Objects.requireNonNull(overlap, "overlap");
        MutableDataManipulator.copyFrom(this.values, valueContainer, overlap);
        return this;
    }

    public static void copyFrom(
        final Map<Key<?>, Object> values, final ValueContainer valueContainer, final MergeFunction overlap) {
        if (valueContainer instanceof AuroraDataManipulator) {
            // Do this to prevent unnecessary object allocations
            final AuroraDataManipulator manipulator = (AuroraDataManipulator) valueContainer;
            MutableDataManipulator.copyFrom(values, overlap, manipulator.values.keySet(), manipulator.values::get);
        } else {
            MutableDataManipulator.copyFrom(values, overlap, valueContainer.getKeys(), key -> valueContainer.get((Key) key).orElse(null));
        }
    }

    private static void copyFrom(final Map<Key<?>, Object> values, final MergeFunction overlap,
        final Iterable<Key<?>> keys,
        final Function<Key<?>, @Nullable Object> function) {
        for (final Key<?> key : keys) {
            final @Nullable Object replacement = function.apply(key);
            if (overlap == MergeFunction.REPLACEMENT_PREFERRED && replacement != null) {
                values.put(key, CopyHelper.copy(replacement));
            } else {
                final @Nullable Object original = values.get(key);
                if (overlap == MergeFunction.ORIGINAL_PREFERRED && original != null) {
                    // Prefer the original
                    continue;
                }
                final Object merged = DataUtil.merge(overlap, (Key) key, original, replacement);
                values.put(key, CopyHelper.copy(merged));
            }
        }
    }

    @Override
    public <E> Mutable set(final Key<? extends Value<E>> key, final E value) {
        Objects.requireNonNull(key, "key");
        Objects.requireNonNull(value, "value");
        this.values.put(key, CopyHelper.copy(value));
        return this;
    }

    @Override
    public Mutable remove(final Key<?> key) {
        Objects.requireNonNull(key, "key");
        this.values.remove(key);
        return this;
    }

    @Override
    public Mutable copy() {
        return new MutableDataManipulator(this.copyMap());
    }

    @Override
    public Set<Key<?>> getKeys() {
        return ImmutableSet.copyOf(this.values.keySet());
    }
}
