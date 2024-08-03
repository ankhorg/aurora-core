package org.inksnow.core.data.store;

import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.resource.ResourcePath;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Optional;

/**
 * A registry for {@link DataStore}s.
 */
public interface DataStoreRegistry {
    /**
     * Registers a {@link DataStore} with the given keys.
     *
     * @param dataStore The data store to register
     * @param keys The keys to register
     */
    void register(DataStore dataStore, Iterable<Key<?>> keys);

    /**
     * Gets all {@link DataStore}s that support the given key.
     *
     * @param dataKey The key
     * @return The data stores
     */
    Collection<DataStore> getDataStores(Key<?> dataKey);

    /**
     * Gets a {@link DataStore} that supports the given key and holder type.
     *
     * @param dataKey The key
     * @param holderType The holder type
     * @return The data store
     */
    DataStore getDataStore(Key<?> dataKey, TypeToken<? extends DataHolder> holderType);

    /**
     * Gets a {@link DataStore} that supports the given key and holder type.
     *
     * @param dataKey The key
     * @param holderType The holder type
     * @return The data store
     */
    DataStore getDataStore(Key<?> dataKey, Type holderType);

    /**
     * Gets a {@link DataStore} that supports the given key and holder type.
     *
     * @param key The key
     * @param holderType The holder type
     * @return The data store
     */
    Optional<DataStore> getDataStore(ResourcePath key, Type holderType);

    /**
     * Gets all {@link DataStore}s that support the given holder type.
     *
     * @param holderType The holder type
     * @return The data stores
     */
    Collection<DataStore> getDataStoresForType(Class<? extends DataHolder> holderType);
}
