package org.inksnow.core.data;

import org.inksnow.core.data.holder.DataHolderBuilder;
import org.inksnow.core.data.persistence.DataBuilder;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataSerializable;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.data.persistence.DataTranslator;
import org.inksnow.core.data.persistence.DataView;
import org.inksnow.core.resource.ResourcePath;

import java.util.Optional;

/**
 * A manager of the overall Data API. This handles the registration of
 * {@link DataSerializable}s and their {@link DataBuilder}s,
 * {@link DataRegistration}s, etc.
 *
 * <p>Note that this manager powers not just serialization and deserialization,
 * but also powers a majority of the Data API.</p>
 */
public interface DataManager {

    /**
     * Registers a {@link DataBuilder} that will dynamically build
     * the given {@link DataSerializable} from a {@link DataContainer}.
     *
     * <p>Builders may not always exist for a given {@link DataSerializable},
     * nor is it guaranteed that a provided builder will function with all
     * {@link DataContainer}s.
     * </p>
     *
     * @param clazz The class of the {@link DataSerializable}
     * @param builder The builder that can build the data serializable
     * @param <T> The type of data serializable
     */
    <T extends DataSerializable> void registerBuilder(Class<T> clazz, DataBuilder<T> builder);

    /**
     * Attempts to retrieve the {@link DataBuilder} for the desired
     * {@link DataSerializable} class.
     *
     * <p>Builders may not always exist for a given {@link DataSerializable},
     * nor is it guaranteed that a provided builder will function with all
     * {@link DataContainer}s.</p>
     *
     * @param clazz The class of the data serializable
     * @param <T> The type of data serializable
     * @return The builder, if available
     */
    <T extends DataSerializable> Optional<DataBuilder<T>> builder(Class<T> clazz);

    /**
     * Attempts to translate an instance of the {@link DataSerializable} from
     * the provided {@link DataView}. If there is no {@link DataBuilder}
     * registered for the provided {@link DataSerializable}, then
     * {@link Optional#empty()} may be returned.
     *
     * @param clazz The class of the data serializable
     * @param dataView The data view containing raw data
     * @param <T> The type of data serializable
     * @return The data serializable, if available
     */
    <T extends DataSerializable> Optional<T> deserialize(Class<T> clazz, DataView dataView);

    /**
     * Registers the given {@link DataHolder.Immutable} class with it's
     * associated {@link DataHolderBuilder.Immutable}. The builder can be used to
     * create new instances of the given {@link DataHolder.Immutable} for data
     * retrieval, data representation, etc.
     *
     * @param holderClass The class of the immutable data holder
     * @param builder The builder instance of the immutable data holder
     * @param <T> The type of immutable data holder
     * @param <B> The type of immutable data builder
     */
    <T extends DataHolder.Immutable<T>, B extends DataHolderBuilder.Immutable<T, B>> void register(Class<T> holderClass, B builder);

    /**
     * Registers a legacy {@code id} that is used by a previous version of
     * {@link DataRegistration} from a plugin such that the custom data can
     * be read by a plugin-data datastore.
     *
     * @param legacyId The legacy id
     * @param dataStoreKey The dataStore key set in {@link DataStore.Builder.HolderStep#pluginData(ResourcePath)}
     */
    void registerLegacyManipulatorIds(String legacyId, ResourcePath dataStoreKey);

    /**
     * Attempts to retrieve the builder for the given
     * {@link DataHolder.Immutable}.
     *
     * <p>If the {@link DataHolder.Immutable} was not registered, multiple
     * systems could fail to retrieve specific data.</p>
     *
     * @param holderClass The immutable data holder class
     * @param <T> The type of immutable data holder
     * @param <B> The type of immutable data builder
     * @return The builder, if available
     */
    <T extends DataHolder.Immutable<T>, B extends DataHolderBuilder.Immutable<T, B>> Optional<B> immutableBuilder(Class<T> holderClass);

    /**
     * Gets the desired {@link DataTranslator} for the provided class.
     *
     * @param objectClass The class of the object
     * @param <T> The type of object
     * @return The data translator, if available
     */
    <T> Optional<DataTranslator<T>> translator(Class<T> objectClass);

    /**
     * Registers a {@link DataTranslator} for the desired class.
     *
     * @param objectClass The class of the object type being managed
     * @param translator The translator for the desired class object
     * @param <T> The type of object
     */
    <T> void registerTranslator(Class<T> objectClass, DataTranslator<T> translator);

    /**
     * Creates a new {@link DataContainer} with a default
     * {@link DataView.SafetyMode} of
     * {@link DataView.SafetyMode#ALL_DATA_CLONED}.
     *
     * @return A new data container
     */
    DataContainer createContainer();

    /**
     * Creates a new {@link DataContainer} with the provided
     * {@link DataView.SafetyMode}.
     *
     * @param safety The safety mode to use
     * @see DataView.SafetyMode
     * @return A new data container with the provided safety mode
     */
    DataContainer createContainer(DataView.SafetyMode safety);

}
