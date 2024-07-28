package org.inksnow.core.impl.data.store.world;

import com.google.common.base.Preconditions;
import lombok.extern.slf4j.Slf4j;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.data.persistence.DataContainer;
import org.inksnow.core.impl.data.MemoryDataContainer;
import org.inksnow.core.impl.data.persistence.NBTTranslator;
import org.inksnow.core.impl.nbt.AuroraTagFactory;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.impl.ref.nbt.RefNbtTagList;
import org.inksnow.core.impl.util.ChunkPos;
import org.inksnow.core.impl.util.NbtTypes;
import org.inksnow.core.nbt.CompoundTag;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AuroraChunkData {
    private final AuroraRegionData auroraRegionData;
    private final ChunkPos pos;
    private final RefNbtTagCompound nbt;

    private DataContainer chunkData;
    private final Map<Integer, DataContainer> blockData = new HashMap<>();

    private boolean closed = false;

    public AuroraChunkData(AuroraRegionData auroraRegionData, ChunkPos pos, RefNbtTagCompound nbt) {
        this.auroraRegionData = auroraRegionData;
        this.pos = pos;
        this.nbt = nbt;

        loadChunkData();
        loadBlockData();
    }

    private void loadBlockData() {
        final RefNbtTagList blockDataList = nbt.getList("block-data", NbtTypes.TAG_COMPOUND);
        for (int i = 0; i < blockDataList.size(); i++) {
            final RefNbtTagCompound blockData = (RefNbtTagCompound) blockDataList.get(i);
            final int blockId = blockData.getInt("block-id");
            this.blockData.put(blockId, NBTTranslator.INSTANCE.translate(
                    (CompoundTag) AuroraTagFactory.INSTANCE.wrap(blockData)
            ));
        }
    }

    private void saveBlockData() {
        final RefNbtTagList blockDataList = nbt.getList("block-data", NbtTypes.TAG_COMPOUND);
        int size = blockDataList.size();
        for (int i = size; i > 0; i--) {
            blockDataList.remove(i - 1);
        }
        for (Map.Entry<Integer, DataContainer> entry : blockData.entrySet()) {
            final DataContainer mappedBlockData = entry.getValue();
            final RefNbtTagCompound blockData = (RefNbtTagCompound) AuroraTagFactory.INSTANCE.unwrap(
                    NBTTranslator.INSTANCE.translate(mappedBlockData)
            );
            blockData.setInt("block-id", entry.getKey());

            if (CraftBukkitVersion.v1_14_R1.isSupport()) {
                blockDataList.add2(blockDataList.size(), blockData);
            } else if (CraftBukkitVersion.v1_13_R1.isSupport()) {
                blockDataList.add1(blockData);
            } else {
                blockDataList.add0(blockData);
            }
        }
        if (CraftBukkitVersion.v1_14_R1.isSupport()) {
            nbt.set1("block-data", blockDataList);
        } else {
            nbt.set0("block-data", blockDataList);
        }
    }

    public DataContainer getBlock(int blockId) {
        return blockData.computeIfAbsent(blockId, it -> new MemoryDataContainer());
    }

    public void setBlock(int blockId, DataContainer container) {
        this.blockData.put(blockId, container);
    }

    public void save() {
        try {
            saveBlockData();
            saveChunkData();
            auroraRegionData.saveImpl(pos, nbt);
        } catch (IOException e) {
            logger.error("Failed to save chunk data", e);
        }
    }

    private void loadChunkData() {
        @Nullable RefNbtTagCompound chunkData = nbt.getCompound("chunk-data");
        if (chunkData == null) {
            this.chunkData = new MemoryDataContainer();
        } else {
            this.chunkData = NBTTranslator.INSTANCE.translate(
                    (CompoundTag) AuroraTagFactory.INSTANCE.wrap(chunkData)
            );
        }
    }

    private void saveChunkData() {
        RefNbtTagCompound chunkData = (RefNbtTagCompound) AuroraTagFactory.INSTANCE.unwrap(
                NBTTranslator.INSTANCE.translate(this.chunkData)
        );
        if (CraftBukkitVersion.v1_14_R1.isSupport()) {
            nbt.set1("chunk-data", chunkData);
        } else {
            nbt.set0("chunk-data", chunkData);
        }
    }

    public void setChunkData(DataContainer container) {
        Preconditions.checkNotNull(container, "container is null");

        this.chunkData = container;
    }

    public DataContainer getChunkData() {
        return chunkData;
    }

    public void close() {
        if (closed) {
            return;
        }
        try {
            save();
        } finally {
            closed = true;
        }
    }
}
