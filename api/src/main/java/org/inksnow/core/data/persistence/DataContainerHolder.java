package org.inksnow.core.data.persistence;

import org.inksnow.core.data.DataHolder;

public interface DataContainerHolder {

    DataContainer getDataContainer();

    interface Mutable extends DataContainerHolder {
        void setDataContainer(DataContainer container);
    }

    interface Immutable<T extends DataHolder> extends DataContainerHolder {
        T withDataContainer(DataContainer container);
    }
}
