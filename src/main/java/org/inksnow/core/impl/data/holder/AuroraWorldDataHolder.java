package org.inksnow.core.impl.data.holder;

import org.bukkit.World;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.holder.WorldDataHolder;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.data.provider.nbt.NBTDataTypes;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public final class AuroraWorldDataHolder extends AuroraNbtFileDataHolder implements WorldDataHolder, AuroraMutableDataHolder {
    private final WeakReference<World> worldReference;

    public AuroraWorldDataHolder(World world) {
        super(world.getWorldFolder().toPath().resolve("aurora.dat"));
        this.worldReference = new WeakReference<>(world);
    }

    // not necessary, for performance
    @Override
    public List<DataHolder.Mutable> impl$mutableDelegateDataHolder() {
        return Collections.singletonList(this);
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.WORLD;
    }
}
