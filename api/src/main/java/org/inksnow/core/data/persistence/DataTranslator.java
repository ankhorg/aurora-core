package org.inksnow.core.data.persistence;

import io.leangen.geantyref.TypeToken;
import org.inksnow.core.data.exception.InvalidDataException;

import java.util.Map;

/**
 * A compatibility object to translate and translate any type of
 * {@link Object} that is not a {@link DataSerializable}. Natively,
 * {@link DataView} will attempt to locate a {@code DataTranslator}
 * during {@link DataView#set(DataQuery, Object)}.
 *
 * @param <T> The type of object that this translator can handle
 */
public interface DataTranslator<T> {

    /**
     * Gets the {@link TypeToken} of this translator.
     *
     * @see TypeToken
     * @return The type token for this translator
     */
    TypeToken<T> token();

    /**
     * Attempts to translate the {@code T} object from the provided
     * {@link DataView}.
     *
     * @param view The data view to translate the object from
     * @return The deserialized object
     * @throws InvalidDataException If the dataview contained invalid data
     */
    T translate(DataView view) throws InvalidDataException;

    /**
     * Serializes the provided object to a {@link DataContainer}.
     *
     * @param obj The object to translate
     * @return The object serialized to a container
     * @throws InvalidDataException If the desired object is not supported
     *     for any reason
     */
    DataContainer translate(T obj) throws InvalidDataException;

    /**
     * Serializes the {@code T} object and applies the provided
     * data to the provided {@link DataView} instead of creating
     * a new {@link DataContainer}, reducing nested information.
     *
     * @param obj The object to serialize
     * @param dataView The data view to serialize to
     * @return The provided data view, for chaining
     */
    default DataView addTo(T obj, DataView dataView) {
        for (Map.Entry<DataQuery, Object> entry : this.translate(obj).values(false).entrySet()) {
            dataView.set(entry.getKey(), entry);
        }
        return dataView;
    }
}
