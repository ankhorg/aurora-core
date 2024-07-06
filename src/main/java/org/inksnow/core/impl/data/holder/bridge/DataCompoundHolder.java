package org.inksnow.core.impl.data.holder.bridge;

import org.inksnow.core.impl.provider.nbt.NBTDataType;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

public interface DataCompoundHolder {

    RefNbtTagCompound data$getCompound();

    void data$setCompound(RefNbtTagCompound nbt);

    /**
     * Gets the {@link NBTDataType} which this
     * nbt data holder contains data for.
     *
     * @return The nbt data type
     */
    NBTDataType data$getNBTDataType();
}