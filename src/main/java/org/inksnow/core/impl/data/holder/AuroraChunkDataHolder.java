package org.inksnow.core.impl.data.holder;

import org.bukkit.Chunk;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.holder.BlockDataHolder;
import org.inksnow.core.data.holder.ChunkDataHolder;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.impl.data.holder.bridge.DataCompoundHolder;
import org.inksnow.core.impl.data.store.world.AuroraChunkData;
import org.inksnow.core.impl.data.store.world.AuroraWorldDataService;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.data.provider.nbt.NBTDataTypes;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.impl.util.PosUtil;
import org.inksnow.core.position.BlockPosition;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;

public final class AuroraChunkDataHolder implements ChunkDataHolder, DataCompoundHolder, AuroraMutableDataHolder {
    private final AuroraWorldDataService worldDataService;
    private final WeakReference<Chunk> chunkReference;

    public AuroraChunkDataHolder(AuroraWorldDataService worldDataService, Chunk chunk) {
        this.worldDataService = worldDataService;
        this.chunkReference = new WeakReference<>(chunk);
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
        return NBTDataTypes.CHUNK;
    }

    @Override
    public void setDataContainer(DataContainer container) {
        chunkData().setChunkData(container);
    }

    @Override
    public DataContainer getDataContainer() {
        return chunkData().getChunkData();
    }

    @Override
    public BlockDataHolder block(BlockPosition position) {
        @Nullable Chunk chunk = chunkReference.get();
        if (chunk == null) {
            throw new IllegalStateException("Chunk is no longer available");
        }
        if (!PosUtil.isInChunk(position.x(), position.z(), chunk.getX(), chunk.getZ())) {
            throw new IllegalArgumentException("Block position is not in this chunk");
        }
        return new AuroraBlockDataHolder(
                worldDataService,
                chunk,
                PosUtil.locationXYZ_blockId(position.x(), position.y(), position.z())
        );
    }
}
