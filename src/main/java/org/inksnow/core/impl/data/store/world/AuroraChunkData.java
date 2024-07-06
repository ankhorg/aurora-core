package org.inksnow.core.impl.data.store.world;

import lombok.extern.slf4j.Slf4j;
import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.impl.ref.nbt.RefNbtTagList;
import org.inksnow.core.impl.util.ChunkPos;
import org.inksnow.core.impl.util.NbtTypes;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AuroraChunkData {
    private final AuroraRegionData auroraRegionData;
    private final ChunkPos pos;
    private final RefNbtTagCompound nbt;
    private final Map<Integer, RefNbtTagCompound> blockData = new HashMap<>();

    private boolean closed = false;

    public AuroraChunkData(AuroraRegionData auroraRegionData, ChunkPos pos, RefNbtTagCompound nbt) {
        this.auroraRegionData = auroraRegionData;
        this.pos = pos;
        this.nbt = nbt;

        loadBlockData();
    }

    private void loadBlockData() {
        RefNbtTagList blockDataList = nbt.getList("block-data", NbtTypes.TAG_COMPOUND);
        for (int i = 0; i < blockDataList.size(); i++) {
            RefNbtTagCompound blockData = (RefNbtTagCompound) blockDataList.get(i);
            int blockId = blockData.getInt("block-id");
            this.blockData.put(blockId, blockData);
        }
    }

    private void saveBlockData() {
        RefNbtTagList blockDataList = nbt.getList("block-data", NbtTypes.TAG_COMPOUND);
        for (Map.Entry<Integer, RefNbtTagCompound> entry : blockData.entrySet()) {
            RefNbtTagCompound blockData = entry.getValue();
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

    public RefNbtTagCompound getBlock(int blockId) {
        return blockData.computeIfAbsent(blockId, it -> new RefNbtTagCompound());
    }

    public void setBlock(int blockId, RefNbtTagCompound blockData) {
        this.blockData.put(blockId, blockData);
    }

    public void save() {
        try {
            saveBlockData();
            auroraRegionData.saveImpl(pos, nbt);
        } catch (IOException e) {
            logger.error("Failed to save chunk data", e);
        }
    }

    public void setChunkData(RefNbtTagCompound nbt) {
        if (CraftBukkitVersion.v1_14_R1.isSupport()) {
            nbt.set1("chunk-data", nbt);
        } else {
            nbt.set0("chunk-data", nbt);
        }
    }

    public RefNbtTagCompound getChunkData() {
        return nbt.getCompound("chunk-data");
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
