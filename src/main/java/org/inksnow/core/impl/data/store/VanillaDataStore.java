package org.inksnow.core.impl.data.store;

import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.util.Tuple;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public class VanillaDataStore implements DataStore {

    private Map<Key<?>, Tuple<BiConsumer<DataView, ?>, Function<DataView, Optional<?>>>>  queriesByKey;
    private Collection<Type> tokens;

    public VanillaDataStore(final Map<Key<?>, Tuple<BiConsumer<DataView, ?>, Function<DataView, Optional<?>>>> queriesByKey,
            final Collection<Type> tokens) {
        this.queriesByKey = queriesByKey;
        this.tokens = tokens;
    }

    @Override
    public Collection<Type> supportedTypes() {
        return this.tokens;
    }

    @Override
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public DataView serialize(DataManipulator dataManipulator, DataView view) {
        for (Map.Entry<Key<?>, Tuple<BiConsumer<DataView, ?>, Function<DataView, Optional<?>>>> entry : this.queriesByKey.entrySet()) {
            final BiConsumer serializer = entry.getValue().first();
            dataManipulator.get((Key) entry.getKey()).ifPresent(value -> serializer.accept(view, value));
        }
        return view;
    }

    @Override
    @SuppressWarnings(value = {"unchecked", "rawtypes"})
    public void deserialize(DataManipulator.Mutable dataManipulator, DataView view) {
        for (Map.Entry<Key<?>, Tuple<BiConsumer<DataView, ?>, Function<DataView, Optional<?>>>> entry : this.queriesByKey.entrySet()) {
            final Function<DataView, Optional<?>> deserializer = entry.getValue().second();
            deserializer.apply(view).ifPresent(value -> dataManipulator.set((Key) entry.getKey(), value));
        }
    }

}
