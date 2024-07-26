package org.inksnow.core.impl.data;

import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataQuery;
import org.inksnow.core.data.persistence.DataView;

import java.util.Optional;

/**
 * The default implementation of {@link DataContainer} that can be instantiated
 * for any use. This is the primary implementation of any {@link DataView} that
 * is used throughout Aurora.
 */
public final class MemoryDataContainer extends MemoryDataView implements DataContainer {

    /**
     * Creates a new {@link MemoryDataContainer} with a default
     * {@link DataView.SafetyMode} of
     * {@link DataView.SafetyMode#ALL_DATA_CLONED}.
     *
     */
    public MemoryDataContainer() {
        this(DataView.SafetyMode.ALL_DATA_CLONED);
    }

    /**
     * Creates a new {@link MemoryDataContainer} with the provided
     * {@link DataView.SafetyMode}.
     *
     * @param safety The safety mode to use
     * @see DataView.SafetyMode
     */
    public MemoryDataContainer(final DataView.SafetyMode safety) {
        super(safety);
    }

    @Override
    public Optional<DataView> parent() {
        return Optional.empty();
    }

    @Override
    public final DataContainer container() {
        return this;
    }

    @Override
    public DataContainer set(final DataQuery path, final Object value) {
        return (DataContainer) super.set(path, value);
    }

    @Override
    public DataContainer remove(final DataQuery path) {
        return (DataContainer) super.remove(path);
    }

}
