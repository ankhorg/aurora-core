package org.inksnow.core.impl.data.store;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.resource.ResourcePath;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

public final class DataStoreRegistry {

    private final DataStore NO_OP_DATASTORE = new VanillaDataStore(Collections.emptyMap(), Collections.emptyList());
    private final Multimap<Key<?>, DataStore> dataStoreByValueKey = HashMultimap.create();
    private final Multimap<ResourcePath, DataStore> dataStoreByDataStoreKey = HashMultimap.create();
    private final List<DataStore> allDataStores = new ArrayList<>();

    private final Map<LookupKey, DataStore> dataStoreCache = new ConcurrentHashMap<>();
    private final Multimap<Type, DataStore> dataStoreByTokenCache = HashMultimap.create();

    public void register(final DataStore dataStore, Iterable<Key<?>> keys) {
        keys.forEach(k -> this.dataStoreByValueKey.put(k, dataStore));
        if (dataStore instanceof AuroraDataStore) {
            final ResourcePath customDataKey = ((AuroraDataStore) dataStore).getDataStoreKey();
            this.dataStoreByDataStoreKey.put(customDataKey, dataStore);
        }
        this.allDataStores.add(dataStore);
        this.dataStoreCache.clear();
        this.dataStoreByTokenCache.clear();
    }

    public Collection<DataStore> getDataStores(Key<?> dataKey) {
        return this.dataStoreByValueKey.get(dataKey);
    }

    public DataStore getDataStore(final Key<?> dataKey, final TypeToken<? extends DataHolder> holderType) {
        return this.getDataStore(dataKey, holderType.getType());
    }

    public DataStore getDataStore(final Key<?> dataKey, final Type holderType) {
        return this.dataStoreCache.computeIfAbsent(new LookupKey(holderType, dataKey), this::loadDataStore);
    }

    public Optional<DataStore> getDataStore(final ResourcePath key, final Type holderType) {
        // TODO do we need caching for this too?
        final List<DataStore> dataStores = this.filterDataStoreCandidates(this.dataStoreByDataStoreKey.get(key), holderType);
        if (dataStores.size() > 1) {
            throw new IllegalStateException("Multiple data-stores registered for the same key (" + key + ") and data-holder " + holderType.toString());
        }
        return dataStores.stream().findAny();
    }

    private DataStore loadDataStore(final LookupKey lookupKey) {
        final List<DataStore> dataStores = filterDataStoreCandidates(this.dataStoreByValueKey.get(lookupKey.key), lookupKey.holderType);
        if (dataStores.size() > 1) {
            throw new IllegalStateException("Multiple data-stores registered for the same data-key (" + lookupKey.key.resourcePath() + ") and data-holder " + lookupKey.holderType.toString());
        }
        if (dataStores.isEmpty()) {
            dataStores.add(this.NO_OP_DATASTORE);
        }
        return dataStores.get(0);
    }

    private List<DataStore> filterDataStoreCandidates(Collection<DataStore> candidates, Type holderType) {
        return candidates.stream()
                .filter(ds -> ds.supportedTypes().stream().anyMatch(token -> GenericTypeReflector.isSuperType(token, holderType)))
                .collect(Collectors.toList());
    }

    public Collection<DataStore> getDataStoresForType(Class<? extends DataHolder> holderType) {
        if (!this.dataStoreByTokenCache.containsKey(holderType)) {
            for (DataStore dataStore : this.allDataStores) {
                if (dataStore.supportedTypes().stream().anyMatch(token -> GenericTypeReflector.isSuperType(token, holderType))) {
                    this.dataStoreByTokenCache.put(holderType, dataStore);
                }
            }
        }
        return this.dataStoreByTokenCache.get(holderType);
    }

    private static class LookupKey {

        private final Type holderType;
        private final Key<?> key;

        public LookupKey(final Type holderType, final Key<?> key) {
            this.holderType = holderType;
            this.key = key;
        }

        @Override
        public boolean equals(final Object o) {
            if (this == o) {
                return true;
            }
            if (o == null || this.getClass() != o.getClass()) {
                return false;
            }
            final LookupKey lookupKey = (LookupKey) o;
            return this.holderType.equals(lookupKey.holderType) &&
                    this.key.equals(lookupKey.key);
        }

        @Override
        public int hashCode() {
            return Objects.hash(this.holderType, this.key);
        }

    }

}
