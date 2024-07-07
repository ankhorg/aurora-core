package org.inksnow.core.impl.data;

import com.google.common.collect.HashMultimap;
import com.google.common.collect.Multimap;
import org.inksnow.core.data.DataRegistration;
import org.inksnow.core.data.exception.DuplicateDataStoreException;
import org.inksnow.core.data.exception.DuplicateProviderException;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.data.provider.DataProvider;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;

public class AuroraDataRegistrationBuilder implements DataRegistration.Builder {
    Multimap<Key, DataProvider> dataProviderMap = HashMultimap.create();
    Map<Type, DataStore> dataStoreMap = new HashMap<>();
    List<Key<?>> keys = new ArrayList<>();

    @Override
    public DataRegistration.Builder store(final DataStore store) throws DuplicateDataStoreException {
        for (final Type holderType : store.supportedTypes()) {
            this.dataStoreMap.put(holderType, store);
        }
        return this;
    }

    @Override
    public DataRegistration.Builder provider(final DataProvider<?, ?> provider) throws DuplicateProviderException {
        this.dataProviderMap.put(provider.key(), provider);
        return this;
    }

    @Override
    public DataRegistration.Builder dataKey(final Key<?> key) {
        this.keys.add(key);
        return this;
    }

    @Override
    public DataRegistration.Builder dataKey(final Key<?> key, final Key<?>... others) {
        this.keys.add(key);
        Collections.addAll(this.keys, others);
        return this;
    }

    @Override
    public DataRegistration.Builder dataKey(final Iterable<Key<?>> keys) {
        keys.forEach(this.keys::add);
        return this;
    }

    @Override
    public DataRegistration build() {
        return new AuroraDataRegistration(this);
    }

    @Override
    public AuroraDataRegistrationBuilder reset() {
        this.dataProviderMap = HashMultimap.create();
        this.dataStoreMap = new IdentityHashMap<>();
        this.keys = new ArrayList<>();
        return this;
    }
}
