package org.inksnow.core.impl.data.holder.bridge;

import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataContainerHolder;
import org.inksnow.core.impl.data.persistence.NBTTranslator;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.nbt.AuroraTagFactory;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.nbt.CompoundTag;

public interface DataCompoundHolder extends DataContainerHolder.Mutable {
    /**
     * Gets the {@link NBTDataType} which this
     * nbt data holder contains data for.
     *
     * @return The nbt data type
     */
    NBTDataType data$getNBTDataType();
}