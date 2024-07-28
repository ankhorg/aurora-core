package org.inksnow.core.impl.data.holder;

import com.google.common.collect.ImmutableList;
import org.bukkit.Chunk;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.holder.BlockDataHolder;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.data.store.world.AuroraChunkData;
import org.inksnow.core.impl.data.store.world.AuroraWorldDataService;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.data.provider.nbt.NBTDataTypes;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public final class AuroraBlockDataHolder implements BlockDataHolder, DataCompoundHolder, AuroraMutableDataHolder {
    private final AuroraWorldDataService worldDataService;
    private final WeakReference<Chunk> chunkReference;
    private final int blockId;

    public AuroraBlockDataHolder(AuroraWorldDataService worldDataService, Chunk chunk, int blockId) {
        this.worldDataService = worldDataService;
        this.chunkReference = new WeakReference<>(chunk);
        this.blockId = blockId;
    }

    // not necessary, for performance
    @Override
    public List<DataHolder.Mutable> impl$mutableDelegateDataHolder() {
        return Collections.singletonList(this);
    }

    private AuroraChunkData chunkData() {
        final @Nullable Chunk chunk = chunkReference.get();
        if (chunk == null) {
            throw new IllegalStateException("Chunk is no longer available");
        }
        final @Nullable AuroraChunkData chunkData = worldDataService.get(chunk.getWorld()).chunk(chunk);
        if (chunkData == null) {
            throw new IllegalStateException("Chunk data is no longer available");
        }
        return chunkData;
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.BLOCK;
    }

    @Override
    public void setDataContainer(DataContainer container) {
        chunkData().setBlock(blockId, container);
    }

    @Override
    public DataContainer getDataContainer() {
        return chunkData().getBlock(blockId);
    }
}
