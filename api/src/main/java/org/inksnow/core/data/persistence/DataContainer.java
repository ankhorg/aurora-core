package org.inksnow.core.data.persistence;

import org.inksnow.core.Aurora;

/**
 * Represents a data structure that contains data. A DataContainer is
 * an object that can be considered a root {@link DataView}.
 */
public interface DataContainer extends DataView {

    /**
     * Creates a new {@link DataContainer} with a default
     * {@link DataView.SafetyMode} of
     * {@link DataView.SafetyMode#ALL_DATA_CLONED}.
     *
     * @return A new data container
     */
    static DataContainer createNew() {
        return Aurora.data().dataManager().createContainer();
    }

    /**
     * Creates a new {@link DataContainer} with the provided
     * {@link DataView.SafetyMode}.
     *
     * @param safety The safety mode to use
     * @see DataView.SafetyMode
     * @return A new data container with the provided safety mode
     */
    static DataContainer createNew(SafetyMode safety) {
        return Aurora.data().dataManager().createContainer(safety);
    }

    @Override
    DataContainer set(DataQuery path, Object value);

    @Override
    DataContainer remove(DataQuery path);
}
