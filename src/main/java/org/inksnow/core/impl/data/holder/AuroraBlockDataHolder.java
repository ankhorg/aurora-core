package org.inksnow.core.impl.data.holder;

import org.bukkit.Chunk;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.holder.BlockDataHolder;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.data.store.world.AuroraChunkData;
import org.inksnow.core.impl.data.store.world.AuroraWorldDataService;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.data.provider.nbt.NBTDataTypes;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;

import java.lang.ref.WeakReference;

public final class AuroraBlockDataHolder implements BlockDataHolder, DataCompoundHolder, AuroraMutableDataHolder {
    private final AuroraWorldDataService worldDataService;
    private final WeakReference<Chunk> chunkReference;
    private final int blockId;

    public AuroraBlockDataHolder(AuroraWorldDataService worldDataService, Chunk chunk, int blockId) {
        this.worldDataService = worldDataService;
        this.chunkReference = new WeakReference<>(chunk);
        this.blockId = blockId;
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
    public RefNbtTagCompound data$getCompound() {
        return chunkData().getBlock(blockId);
    }

    @Override
    public void data$setCompound(RefNbtTagCompound nbt) {
        chunkData().setBlock(blockId, nbt);
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.BLOCK;
    }
}
