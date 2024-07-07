package org.inksnow.core.data.persistence;

import org.inksnow.core.data.exception.InvalidDataException;

import java.util.Objects;
import java.util.Optional;

/**
 * An abstract implementation of {@link DataBuilder} that pre-defines all of
 * the necessary "content update" implementation required for content
 * versioning. Note that the builder itself is versioned to ensure that
 * content versioning is appropriately handled. It is highly recommended to
 * extend this class to implement {@link DataBuilder}  as necessary for future upgradeability of
 * custom content.
 *
 * @param <T> The type of DataSerializable
 */
public abstract class AbstractDataBuilder<T extends DataSerializable> implements DataBuilder<T> {

    private final int supportedVersion;
    private final Class<T> requiredClass;

    protected AbstractDataBuilder(Class<T> requiredClass, int supportedVersion) {
        this.requiredClass = Objects.requireNonNull(requiredClass, "Required class");
        this.supportedVersion = supportedVersion;
    }

    /**
     * Builds the currently {@link #supportedVersion} variant of the intended
     * {@link DataSerializable}, such that all content upgrades have already
     * been handled by {@link #build(DataView)}. This otherwise follows the
     * same contract as {@link DataBuilder#build(DataView)}.
     *
     * @param container The container with data to build from
     * @return The deserialized data serializable, if possible
     * @throws InvalidDataException If there's issues of invalid data formats
     *     or invalid data
     */
    protected abstract Optional<T> buildContent(DataView container) throws InvalidDataException;

    @Override
    public final Optional<T> build(DataView container) throws InvalidDataException {
        try {
            return this.buildContent(container);
        } catch (Exception e) {
            throw new InvalidDataException("Could not deserialize something correctly, likely due to bad type data.", e);
        }
    }
}
