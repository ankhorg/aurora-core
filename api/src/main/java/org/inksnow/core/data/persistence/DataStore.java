package org.inksnow.core.data.persistence;

import io.leangen.geantyref.TypeToken;
import org.inksnow.core.Aurora;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.util.Builder;

import java.lang.reflect.Type;
import java.util.Collection;
import java.util.Map;
import java.util.Optional;
import java.util.function.BiConsumer;
import java.util.function.Function;

public interface DataStore {

    /**
     * Gets the supported {@link DataHolder} types.
     *
     * <p>Every returned {@link java.lang.reflect.Type} will be a subtype
     * of {@link DataHolder}.</p>
     *
     * @return The supported dataHolder type.
     */
    Collection<Type> supportedTypes();

    /**
     * Serializes the values of the {@link DataManipulator}
     * into the {@link DataView}.
     *
     * @param dataManipulator The data manipulator
     * @param view The data view to serialize to
     * @return The view, for chaining
     */
    DataView serialize(DataManipulator dataManipulator, DataView view);

    /**
     * Serializes the passed in {@link Value values} to the {@link DataView view}.
     *
     * @param values The values to serialize
     * @param view The view
     * @return The view, for chaining
     */
    default DataView serialize(Iterable<Value<?>> values, DataView view) {
        return this.serialize(DataManipulator.immutableOf(values), view);
    }

    /**
     * Serializes the {@link Value}s.
     *
     * @param values The value container
     * @return This view, for chaining
     */
    default DataView serialize(Iterable<Value<?>> values) {
        return this.serialize(DataManipulator.immutableOf(values));
    }

    /**
     * Serializes the values of the {@link DataManipulator}.
     *
     * @param dataManipulator The data manipulator
     * @return This view, for chaining
     */
    default DataView serialize(DataManipulator dataManipulator) {
        final DataView dataView = DataContainer.createNew();
        this.serialize(dataManipulator, dataView);
        return dataView;
    }

    /**
     * Deserializes the data from the {@link DataView} and puts
     * it in the {@link DataManipulator.Mutable}.
     *
     * @param dataManipulator The mutable data manipulator
     * @param view The data view to deserialize
     */
    void deserialize(DataManipulator.Mutable dataManipulator, DataView view);

    /**
     * Deserializes the {@link DataView} as a {@link DataManipulator.Mutable}.
     *
     * @param view The data view to deserialize
     * @return The value store
     */
    default DataManipulator.Mutable deserialize(DataView view) {
        final DataManipulator.Mutable dataManipulator = DataManipulator.mutableOf();
        this.deserialize(dataManipulator, view);
        return dataManipulator;
    }

    /**
     * Provides a {@link DataStore} for a single {@link Key}.
     * <p>
     *     Note that default deserializers do not support {@link Collection}, {@link Map} or Array types!
     *     Use {@link Builder.SerializersStep#key(Key, BiConsumer, Function)} for these.
     * </p>
     *
     * @param key The data key
     * @param dataQuery The dataQuery to serialize this key under
     * @param typeTokens The dataHolder types
     *
     * @return The new data store
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    static <T, V extends Value<T>> DataStore of(final Key<V> key, final DataQuery dataQuery, final TypeToken<? extends DataHolder> typeToken, final TypeToken<? extends DataHolder>... typeTokens) {
        return DataStore.builder()
            .pluginData(key.resourcePath())
            .holder(typeToken)
            .holder(typeTokens)
            .key(key, dataQuery)
            .build();
    }

    /**
     * Provides a {@link DataStore} for a single {@link Key}.
     * <p>
     *     Note that default deserializers do not support {@link Collection}, {@link Map} or Array types!
     *     Use {@link Builder.SerializersStep#key(Key, BiConsumer, Function)} for these.
     * </p>
     *
     * @param key The data key
     * @param dataQuery The dataQuery to serialize this key under
     * @param types The dataHolder types
     *
     * @return The new data store
     */
    @SafeVarargs
    @SuppressWarnings("unchecked")
    static <T, V extends Value<T>> DataStore of(final Key<V> key, final DataQuery dataQuery, final Class<?extends DataHolder> type, final Class<? extends DataHolder>... types) {
        return DataStore.builder().pluginData(key.resourcePath()).holder(type).holder(types).key(key, dataQuery).build();
    }

    /**
     * Returns the {@link DataStore} builder.
     *
     * @return The dataStore builder.
     */
    static DataStore.Builder builder() {
        return Aurora.createBuilder(Builder.class);
    }

    interface Builder extends org.inksnow.core.util.Builder<DataStore, Builder> {

        /**
         * Starts building a DataStore for plugin data.
         * <p>Serializers and Deserializers will operate on their own {@link DataView}.</p>
         *
         * @param key the key under which all data from this DataStore is registered
         *
         * @return this builder for chaining
         */
        HolderStep pluginData(ResourcePath key);

        /**
         * Starts building a DataStore for raw data.
         * <p>Serializers and deserializers will operate on the root {@link DataView}
         * which includes all data from vanilla minecraft and more</p>
         * <p>Consider using {@link #pluginData} instead.</p>
         *
         * @return this builder for chaining
         */
        HolderStep vanillaData();

        interface HolderStep extends org.inksnow.core.util.Builder<DataStore, Builder> {
            /**
             * Adds one or more allowed dataHolder types
             *
             * @param typeTokens the dataHolder types
             *
             * @return this builder for chaining
             */
            @SuppressWarnings("unchecked")
            SerializersStep holder(TypeToken<? extends DataHolder>... typeTokens);

            /**
             * Adds one or more allowed dataHolder types
             *
             * <p>These must not be parameterized types.</p>
             *
             * @param types the dataHolder types
             *
             * @return this builder for chaining
             */
            @SuppressWarnings("unchecked")
            SerializersStep holder(Class<? extends DataHolder>... types);
        }

        interface SerializersStep extends HolderStep, org.inksnow.core.util.Builder<DataStore, Builder> {

            /**
             * Adds one or more keys using the default implemented serializers for the given key.
             * <p>The {@link Key#resourcePath()} resource-key} value will be used as DataQuery</p>
             *
             * @param key The data key
             * @param moreKeys more data keys
             *
             * @return this builder for chaining
             */
            Builder.EndStep keys(final Key<?> key, final Key<?>... moreKeys);

            /**
             * Adds the default implemented serializers for the given key.
             *
             * @param key The data key
             * @param dataQueries The dataQuery to serialize this key under
             *
             * @return this builder for chaining
             */
            default <T, V extends Value<T>> Builder.EndStep key(final Key<V> key, final String... dataQueries) {
                if (dataQueries.length == 0) {
                    throw new IllegalArgumentException("dataQueries cannot be empty");
                }
                return this.key(key, DataQuery.of(dataQueries));
            }

            /**
             * Adds the default implemented serializers for the given key.
             *
             * @param key The data key
             * @param dataQuery The dataQuery to serialize this key under
             *
             * @return this builder for chaining
             */
            <T, V extends Value<T>> Builder.EndStep key(final Key<V> key, final DataQuery dataQuery);

            /**
             * Adds the serializers for the given key.
             *
             * @param key The data key
             * @param serializer the data serializer
             * @param deserializer the data serserializer
             *
             * @return this builder for chaining
             */
            <T, V extends Value<T>> Builder.EndStep key(Key<V> key, BiConsumer<DataView, T> serializer, Function<DataView, Optional<T>> deserializer);
        }

        interface EndStep extends SerializersStep, org.inksnow.core.util.Builder<DataStore, Builder> {

            /**
             * Builds a dataStore for given dataHolder type.
             *
             * @return The new data store
             */
            DataStore build();
        }
    }
}
