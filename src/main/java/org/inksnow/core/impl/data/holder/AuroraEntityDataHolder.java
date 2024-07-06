package org.inksnow.core.impl.data.holder;

import org.bukkit.entity.Entity;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.holder.EntityDataHolder;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.data.provider.nbt.NBTDataTypes;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

import java.lang.ref.WeakReference;

public final class AuroraEntityDataHolder implements EntityDataHolder, DataCompoundHolder, AuroraMutableDataHolder {
    private final WeakReference<Entity> entityReference;

    public AuroraEntityDataHolder(Entity entity) {
        this.entityReference = new WeakReference<>(entity);
    }

    public Entity entity() {
        final @Nullable Entity entity = entityReference.get();
        if (entity == null) {
            throw new IllegalStateException("Entity is not available");
        }
        return entity;
    }

    @Override
    public RefNbtTagCompound data$getCompound() {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public void data$setCompound(RefNbtTagCompound nbt) {
        throw new UnsupportedOperationException("Not implemented yet");
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.ENTITY;
    }
}
