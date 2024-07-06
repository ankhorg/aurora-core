package org.inksnow.core.impl.data.store.world;

import com.google.common.base.Preconditions;
import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.World;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.event.world.ChunkUnloadEvent;
import org.bukkit.event.world.WorldLoadEvent;
import org.bukkit.event.world.WorldUnloadEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.impl.util.ThreadUtil;

import java.util.HashMap;

@Slf4j
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public class AuroraWorldDataService implements Listener {
    private final HashMap<World, AuroraWorldData> worldData = new HashMap<>();

    @SneakyThrows
    @EventHandler(priority = EventPriority.LOWEST)
    public void onWorldLoad(WorldLoadEvent event) {
        ThreadUtil.ensureMainThread();
        final World world = event.getWorld();
        logger.info("World load: {}", world.getName());
        // paper in some version call ChunkLoad event
        final AuroraWorldData worldHolder = get(event.getWorld());
    }

    @SneakyThrows
    @EventHandler(priority = EventPriority.MONITOR)
    public void onWorldUnload(WorldUnloadEvent event) {
        ThreadUtil.ensureMainThread();
        final World world = event.getWorld();
        logger.info("World unload: {}", world.getName());
        final @Nullable AuroraWorldData previous = worldData.remove(world);
        if (previous != null) {
            previous.close();
        }
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onChunkLoad(ChunkLoadEvent event) {
        ThreadUtil.ensureMainThread();
        final AuroraWorldData worldData = get(event.getWorld());
        worldData.onChunkLoad(event.getChunk());
    }

    @SneakyThrows
    @EventHandler(priority = EventPriority.MONITOR)
    public void onChunkUnload(ChunkUnloadEvent event) {
        ThreadUtil.ensureMainThread();
        final @Nullable AuroraWorldData worldData = this.worldData.get(event.getWorld());
        if (worldData != null) {
            worldData.onChunkUnload(event.getChunk());
            if (worldData.regionCount() == 0) {
                worldData.close();
                this.worldData.remove(event.getWorld());
            }
        }
    }

    @SneakyThrows
    public void close() {
        for (AuroraWorldData worldData : this.worldData.values()) {
            worldData.close();
        }
    }

    public AuroraWorldData get(World world) {
        ThreadUtil.ensureMainThread();
        Preconditions.checkNotNull(world, "world");
        return worldData.computeIfAbsent(world, AuroraWorldData::new);
    }
}
