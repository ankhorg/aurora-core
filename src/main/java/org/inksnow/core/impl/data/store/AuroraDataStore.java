package org.inksnow.core.impl.data.store;

import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.util.Tuple;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public final class AuroraDataStore extends VanillaDataStore {

    private ResourcePath key;

    public AuroraDataStore(
        ResourcePath key,
        final Map<Key<?>, Tuple<BiConsumer<DataView, ?>, Function<DataView, Optional<?>>>> queriesByKey,
        final Collection<Type> tokens
    ) {
        super(queriesByKey, tokens);
        this.key = key;
    }

    public ResourcePath getDataStoreKey() {
        return this.key;
    }
}
