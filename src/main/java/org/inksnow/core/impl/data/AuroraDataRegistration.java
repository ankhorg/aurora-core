package org.inksnow.core.impl.data;

import com.google.common.collect.Multimap;
import io.leangen.geantyref.GenericTypeReflector;
import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataRegistration;
import org.inksnow.core.data.exception.UnregisteredKeyException;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.value.Value;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.List;
import java.util.Map;
import java.util.Optional;

public class AuroraDataRegistration implements DataRegistration {

    final List<Key<?>> keys;
    final Map<Type, DataStore> dataStoreMap;
    final Multimap<Key, DataProvider> dataProviderMap;

    AuroraDataRegistration(final AuroraDataRegistrationBuilder builder) {
        this.keys = builder.keys;
        this.dataStoreMap = builder.dataStoreMap;
        this.dataProviderMap = builder.dataProviderMap;
    }

    @Override
    @SuppressWarnings("unchecked")
    public <V extends Value<E>, E> Collection<DataProvider<V, E>> providersFor(Key<V> key) throws UnregisteredKeyException {
        return (Collection) this.dataProviderMap.get(key);
    }

    @Override
    public Optional<DataStore> dataStore(final TypeToken<? extends DataHolder> token) {
        return this.getDataStore0(token.getType());
    }

    @Override
    public Optional<DataStore> dataStore(final Class<? extends DataHolder> token) {
        return this.getDataStore0(token);
    }

    private Optional<DataStore> getDataStore0(final Type type) {
        DataStore dataStore = this.dataStoreMap.get(type);
        if (dataStore != null) {
            return Optional.of(dataStore);
        }
        for (final Map.Entry<Type, DataStore> entry : this.dataStoreMap.entrySet()) {
            if (GenericTypeReflector.isSuperType(entry.getKey(), type)) {
                dataStore = entry.getValue();
                this.dataStoreMap.put(type, dataStore);
                return Optional.of(dataStore);
            }
        }
        return Optional.empty();
    }

    @Override
    public Iterable<Key<?>> keys() {
        return this.keys;
    }

    public Collection<DataStore> getDataStores() {
        return this.dataStoreMap.values();
    }

    public Collection<DataProvider> getDataProviders() {
        return this.dataProviderMap.values();
    }
}
