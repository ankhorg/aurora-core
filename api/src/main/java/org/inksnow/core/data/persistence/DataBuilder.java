package org.inksnow.core.data.persistence;

import org.inksnow.core.data.exception.InvalidDataException;

import java.util.Optional;

/**
 * Represents a builder that can take a {@link DataContainer} and create a
 * new instance of a {@link DataSerializable}. The builder should be a
 * singleton and may not exist for every data serializable.
 *
 * <p>It is <strong>HIGHLY</strong> recommended to extend
 * {@link AbstractDataBuilder} as it implements content updating</p>
 *
 * @param <T> The type of data serializable this builder can build
 */
public interface DataBuilder<T extends DataSerializable> {

    /**
     * Attempts to build the provided {@link DataSerializable} from the given
     * {@link DataView}. If the {@link DataView} is invalid or
     * missing necessary information to complete building the
     * {@link DataSerializable}, {@link Optional#empty()} may be returned.
     *
     * @param container The container containing all necessary data
     * @return The instance of the {@link DataSerializable}, if successful
     * @throws InvalidDataException In the event that the builder is unable to
     *     properly construct the data serializable from the data view
     */
    Optional<T> build(DataView container) throws InvalidDataException;
}
