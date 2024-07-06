package org.inksnow.core.impl.data.store.world;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Chunk;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.ankhinvoke.bukkit.util.CraftBukkitVersion;
import org.inksnow.core.impl.data.store.world.linear.LinearRegionFile;
import org.inksnow.core.impl.ref.nbt.RefNbtIo;
import org.inksnow.core.impl.ref.nbt.RefNbtTagCompound;
import org.inksnow.core.impl.util.ChunkPos;
import org.inksnow.core.impl.util.PosUtil;

import java.io.Closeable;
import java.io.DataInput;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

@Slf4j
public class AuroraRegionData implements Closeable {
    private final AuroraWorldData worldData;
    private final int regionX;
    private final int regionZ;
    private final Map<ChunkPos, AuroraChunkData> chunkData;

    private final Path regionPath;
    private final LinearRegionFile regionFile;


    @SneakyThrows
    public AuroraRegionData(AuroraWorldData worldData, long regionId) {
        this.worldData = worldData;
        this.regionX = PosUtil.regionId_regionX(regionId);
        this.regionZ = PosUtil.regionId_regionZ(regionId);
        this.chunkData = new HashMap<>();

        this.regionPath = worldData.auroraRegionFolder.resolve("r." + regionX + "." + regionZ + ".aca");
        this.regionFile = new LinearRegionFile(regionPath, 1);
    }

    @SneakyThrows
    public void onChunkLoad(Chunk chunk) {
        final ChunkPos pos = new ChunkPos(chunk);
        final RefNbtTagCompound nbt;
        try {
            nbt = loadImpl(pos);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to load chunk data: " + regionPath, e);
        }
        final AuroraChunkData data = new AuroraChunkData(this, pos, nbt);
        chunkData.put(pos, data);
    }

    @SneakyThrows
    public void onChunkUnload(Chunk chunk) {
        final ChunkPos pos = new ChunkPos(chunk);
        final @Nullable AuroraChunkData data = chunkData.remove(pos);
        if (data != null) {
            data.close();
        }
    }

    public int chunkCount() {
        return chunkData.size();
    }

    public @Nullable AuroraChunkData chunk(Chunk chunk) {
        return chunkData.get(new ChunkPos(chunk));
    }

    private RefNbtTagCompound loadImpl(ChunkPos pos) throws IOException {
        try (@Nullable DataInputStream dataInputStream = regionFile.getChunkDataInputStream(pos)) {
            if (dataInputStream == null) {
                return new RefNbtTagCompound();
            }
            // 1.16.2 and later versions use the read(DataInput) method
            if (CraftBukkitVersion.v1_16_R2.isSupport()) {
                return RefNbtIo.read((DataInput) dataInputStream);
            } else {
                return RefNbtIo.read(dataInputStream);
            }
        }
    }

    /* package-private */ void saveImpl(ChunkPos pos, RefNbtTagCompound nbt) throws IOException {
        try (DataOutputStream dataOutputStream = regionFile.getChunkDataOutputStream(pos)) {
            RefNbtIo.write(nbt, dataOutputStream);
        }
    }

    public void close() throws IOException {
        for (AuroraChunkData data : chunkData.values()) {
            data.close();
        }
        regionFile.close();
    }
}
