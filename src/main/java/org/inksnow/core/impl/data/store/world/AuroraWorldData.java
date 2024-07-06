package org.inksnow.core.impl.data.store.world;

import lombok.Getter;
import lombok.SneakyThrows;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.impl.data.holder.AuroraWorldDataHolder;
import org.inksnow.core.impl.util.PosUtil;

import java.io.File;
import java.io.IOException;
import java.lang.ref.WeakReference;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashMap;
import java.util.Map;

public class AuroraWorldData {
    @Getter
    private final AuroraWorldDataHolder dataHolder;
    /* package-private */ final Path auroraRegionFolder;
    private final WeakReference<World> worldReference;
    private final File worldFolder;
    private final Map<Long, AuroraRegionData> regionData;

    @SneakyThrows
    /* package-private */ AuroraWorldData(World world) {
        this.dataHolder = new AuroraWorldDataHolder(world);
        this.worldReference = new WeakReference<>(world);
        this.worldFolder = world.getWorldFolder();
        this.regionData = new HashMap<>();

        this.auroraRegionFolder = worldFolder.toPath().resolve("aurora-region");
        Files.createDirectories(auroraRegionFolder);
    }

    public void onChunkLoad(Chunk chunk) {
        final long regionId = PosUtil.chunk_regionId(chunk);
        final AuroraRegionData region = region(regionId);
        region.onChunkLoad(chunk);
    }

    @SneakyThrows
    public void onChunkUnload(Chunk chunk) {
        final long regionId = PosUtil.chunk_regionId(chunk);
        final @Nullable AuroraRegionData region = regionData.get(regionId);
        if (region != null) {
            region.onChunkUnload(chunk);

            if (region.chunkCount() == 0) {
                region.close();
                regionData.remove(regionId);
            }
        }
    }

    public AuroraRegionData region(long regionId) {
        return regionData.computeIfAbsent(regionId, it -> new AuroraRegionData(this, regionId));
    }

    public @Nullable AuroraChunkData chunk(Chunk chunk) {
        return region(chunk).chunk(chunk);
    }

    public AuroraRegionData region(Chunk chunk) {
        return region(PosUtil.chunk_regionId(chunk));
    }

    public int regionCount() {
        return regionData.size();
    }

    public void close() throws IOException {
        dataHolder.close();
        for (AuroraRegionData region : regionData.values()) {
            region.close();
        }
    }
}
