package org.inksnow.core.impl.data.manipulator;

import com.google.common.collect.ImmutableMap;
import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.MergeFunction;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.data.value.ValueContainer;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class ImmutableDataManipulatorFactory implements DataManipulator.Immutable.Factory {

    @Override
    public DataManipulator.Immutable of() {
        return new ImmutableDataManipulator(ImmutableMap.of());
    }

    @Override
    public DataManipulator.Immutable of(final Iterable<? extends Value<?>> values) {
        final Map<Key<?>, Object> mappedValues = MutableDataManipulatorFactory.mapValues(values);
        return new ImmutableDataManipulator(Collections.unmodifiableMap(mappedValues));
    }

    @Override
    public DataManipulator.Immutable of(final ValueContainer valueContainer) {
        Objects.requireNonNull(valueContainer);
        if (valueContainer instanceof DataManipulator.Immutable) {
            return (DataManipulator.Immutable) valueContainer;
        }
        final Map<Key<?>, Object> values = new HashMap<>();
        MutableDataManipulator.copyFrom(values, valueContainer, MergeFunction.REPLACEMENT_PREFERRED);
        return new ImmutableDataManipulator(Collections.unmodifiableMap(values));
    }
}
