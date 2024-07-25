package org.inksnow.core.impl.data.manipulator;

import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.data.value.ValueContainer;
import org.inksnow.core.impl.util.CopyHelper;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public final class MutableDataManipulatorFactory implements DataManipulator.Mutable.Factory {

    @Override
    public DataManipulator.Mutable of() {
        return new MutableDataManipulator();
    }

    @Override
    public DataManipulator.Mutable of(final Iterable<? extends Value<?>> values) {
        return new MutableDataManipulator(MutableDataManipulatorFactory.mapValues(values));
    }

    static Map<Key<?>, Object> mapValues(final Iterable<? extends Value<?>> values) {
        Objects.requireNonNull(values);

        final Map<Key<?>, Object> mappedValues = new HashMap<>();
        for (final Value<?> value : values) {
            mappedValues.put(value.key(), CopyHelper.copy(value.get()));
        }
        return mappedValues;
    }

    @Override
    public DataManipulator.Mutable of(final ValueContainer valueContainer) {
        Objects.requireNonNull(valueContainer);

        final MutableDataManipulator manipulator = new MutableDataManipulator();
        manipulator.copyFrom(valueContainer);
        return manipulator;
    }
}
