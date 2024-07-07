package org.inksnow.core.data.persistence;

/**
 * Represents an object that can be represented by a {@link DataContainer}.
 * <p>DataContainers received from {@link DataSerializable#toContainer()}
 * should be considered to be copies of the original data, and therefore,
 * thread safe.</p>
 */
public interface DataSerializable {
    /**
     * Serializes this object into a comprehensible {@link DataContainer}.
     *
     * @return A newly created DataContainer
     */
    DataContainer toContainer();

}
