package org.inksnow.core.impl.data.holder;

import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.holder.EntityDataHolder;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.data.provider.nbt.NBTDataTypes;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public final class AuroraEntityDataHolder implements EntityDataHolder, DataCompoundHolder, AuroraMutableDataHolder {
    private final WeakReference<Entity> entityReference;

    public AuroraEntityDataHolder(Entity entity) {
        this.entityReference = new WeakReference<>(entity);
    }

    // not necessary, for performance
    @Override
    public List<DataHolder.Mutable> impl$mutableDelegateDataHolder() {
        return Collections.singletonList(this);
    }

    @Override
    public Entity entity() {
        @Nullable Entity entity = entityReference.get();
        if (entity == null) {
            throw new IllegalStateException("Entity have been collected by GC, don't store DataHolder!");
        }
        return entity;
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.ENTITY;
    }

    @Override
    public void setDataContainer(DataContainer container) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public DataContainer getDataContainer() {
        throw new UnsupportedOperationException("Not implemented yet");
    }
}
