package org.inksnow.core.impl.data.holder.bridge;

import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.data.persistence.DataContainerHolder;
import org.inksnow.core.impl.data.persistence.NBTTranslator;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.nbt.AuroraTagFactory;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.nbt.CompoundTag;

public interface DataCompoundHolder extends DataContainerHolder.Mutable {

    RefNbtTagCompound data$getCompound();

    void data$setCompound(RefNbtTagCompound nbt);

    /**
     * Gets the {@link NBTDataType} which this
     * nbt data holder contains data for.
     *
     * @return The nbt data type
     */
    NBTDataType data$getNBTDataType();

    @Override
    default DataContainer getDataContainer() {
        return NBTTranslator.INSTANCE.translate(
            (CompoundTag) AuroraTagFactory.INSTANCE.wrap(data$getCompound())
        );
    }

    @Override
    default void setDataContainer(DataContainer container) {
        data$setCompound((RefNbtTagCompound) AuroraTagFactory.INSTANCE.unwrap(
            NBTTranslator.INSTANCE.translate(container)
        ));
    }
}