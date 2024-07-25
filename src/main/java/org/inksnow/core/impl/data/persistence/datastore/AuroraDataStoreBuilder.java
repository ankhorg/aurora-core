package org.inksnow.core.impl.data.persistence.datastore;

import io.leangen.geantyref.TypeToken;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.DataDeserializer;
import org.inksnow.core.impl.data.store.AuroraDataStore;
import org.inksnow.core.impl.data.store.VanillaDataStore;
import org.inksnow.core.impl.util.TypeTokenUtil;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.util.Tuple;

import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.Collections;
import java.util.IdentityHashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Function;


public final class AuroraDataStoreBuilder implements DataStore.Builder, DataStore.Builder.HolderStep, DataStore.Builder.SerializersStep, DataStore.Builder.EndStep {

    private final Map<Key<?>, Tuple<BiConsumer<DataView, ?>, Function<DataView, Optional<?>>>> serializers = new IdentityHashMap<>();
    private final List<Type> dataHolderTypes = new ArrayList<>();
    private @Nullable ResourcePath key;

    @Override
    public <T, V extends Value<T>> AuroraDataStoreBuilder key(final Key<V> key, final DataQuery dataQuery) {
        final BiFunction<DataView, DataQuery, Optional<T>> deserializer = DataDeserializer.deserializer(key.elementType());
        return this.key(key, (view, value) -> view.set(dataQuery, value), v -> deserializer.apply(v, dataQuery));
    }

    public boolean isEmpty() {
        return this.serializers.isEmpty();
    }

    public List<Type> getDataHolderTypes() {
        return this.dataHolderTypes;
    }

    @Override
    @SuppressWarnings("rawtypes")
    public <T, V extends Value<T>> AuroraDataStoreBuilder key(final Key<V> key, final BiConsumer<DataView, T> serializer, final Function<DataView, Optional<T>> deserializer) {
        if (this.key != null) {
            final DataQuery query = this.key.asDataQuery();
            final SpongeDataSerializer<T> customSerializer = new SpongeDataSerializer<>(serializer, query);
            final SpongeDataDeserializer<T> customDeserializer = new SpongeDataDeserializer<>(deserializer, query);
            this.serializers.put(key, (Tuple) Tuple.of(customSerializer, customDeserializer));
        } else {
            this.serializers.put(key, (Tuple) Tuple.of(serializer, deserializer));
        }

        return this;
    }

    @Override
    @SuppressWarnings({"rawtypes", "unchecked"})
    public EndStep keys(final Key<?> key, final Key<?>... moreKeys) {
        this.key((Key) key, key.resourcePath().asDataQuery());
        for (final Key<?> moreKey : moreKeys) {
            this.key((Key) moreKey, moreKey.resourcePath().asDataQuery());
        }
        return this;
    }

    @Override
    public DataStore.Builder reset() {
        this.serializers.clear();
        this.dataHolderTypes.clear();
        this.key = null;
        return this;
    }

    @Override
    public AuroraDataStoreBuilder holder(final TypeToken<? extends DataHolder>... typeTokens) {
        for (final TypeToken<? extends DataHolder> token : typeTokens) {
            this.dataHolderTypes.add(token.getType());
        }
        return this;
    }

    @Override
    public AuroraDataStoreBuilder holder(final Class<? extends DataHolder>... classes) {
        for (final Class<? extends DataHolder> clazz : classes) {
            this.dataHolderTypes.add(TypeTokenUtil.requireCompleteGenerics(clazz));
        }
        return this;
    }

    @Override
    public AuroraDataStoreBuilder pluginData(final ResourcePath key) {
        this.key = key;
        return this;
    }

    @Override
    public AuroraDataStoreBuilder vanillaData() {
        this.key = null;
        return this;
    }

    @Override
    public DataStore build() {
        if (key == null) {
            return new VanillaDataStore(
                Collections.unmodifiableMap(this.serializers),
                this.dataHolderTypes
            );
        } else {
            return new AuroraDataStore(
                key,
                Collections.unmodifiableMap(this.serializers),
                this.dataHolderTypes
            );
        }
    }

    private static class SpongeDataSerializer<T> implements BiConsumer<DataView, T> {

        private final BiConsumer<DataView, T> serializer;
        private final DataQuery key;

        public SpongeDataSerializer(final BiConsumer<DataView, T> serializer, final DataQuery key) {
            this.serializer = serializer;
            this.key = key;
        }

        @Override
        public void accept(final DataView view, final T v) {
            this.serializer.accept(view, v);
            if (view.isEmpty()) {
                return;
            }
        }
    }

    private static class SpongeDataDeserializer<T> implements Function<DataView, Optional<T>> {

        private final Function<DataView, Optional<T>> deserializer;
        private final DataQuery key;

        public SpongeDataDeserializer(final Function<DataView, Optional<T>> deserializer, final DataQuery key) {
            this.deserializer = deserializer;
            this.key = key;
        }

        @Override
        public Optional<T> apply(final DataView view) {
            return this.deserializer.apply(view);
        }
    }
}
