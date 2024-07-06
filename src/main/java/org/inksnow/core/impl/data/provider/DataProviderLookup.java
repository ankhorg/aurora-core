package org.inksnow.core.impl.data.provider;

import com.google.common.collect.ImmutableMap;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.provider.DataProvider;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.key.AuroraKey;

import java.util.Collection;
import java.util.Map;

public final class DataProviderLookup {

    private final Map<Key<?>, DataProvider<?, ?>> providerMap;

    DataProviderLookup(Map<Key<?>, DataProvider<?, ?>> providerMap) {
        this.providerMap = ImmutableMap.copyOf(providerMap);
    }

    /**
     * Gets all the non-empty delegate {@link DataProvider}s.
     *
     * @return The delegate data providers
     */
    public Collection<DataProvider<?, ?>> getAllProviders() {
        return this.providerMap.values();
    }

    /**
     * Gets the delegate {@link DataProvider} for the given {@link Key}.
     *
     * @param key The key
     * @param <V> The value type
     * @param <E> The element type
     * @return The delegate provider
     */
    @SuppressWarnings("unchecked")
    public <V extends Value<E>, E> DataProvider<V, E> getProvider(Key<V> key) {
        return (DataProvider<V, E>) this.providerMap.getOrDefault(key, ((AuroraKey<V, E>) key).emptyDataProvider());
    }
}
