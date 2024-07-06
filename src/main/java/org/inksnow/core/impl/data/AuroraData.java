package org.inksnow.core.impl.data;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.Chunk;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataApi;
import org.inksnow.core.data.holder.EntityDataHolder;
import org.inksnow.core.impl.data.holder.AuroraBlockDataHolder;
import org.inksnow.core.impl.data.holder.AuroraChunkDataHolder;
import org.inksnow.core.impl.data.holder.AuroraEntityDataHolder;
import org.inksnow.core.impl.data.holder.AuroraItemDataHolder;
import org.inksnow.core.impl.data.holder.AuroraPlayerDataHolder;
import org.inksnow.core.impl.data.holder.AuroraUserDataHolder;
import org.inksnow.core.impl.data.holder.AuroraWorldDataHolder;
import org.inksnow.core.impl.data.store.player.AuroraPlayerDataService;
import org.inksnow.core.impl.data.store.world.AuroraWorldDataService;
import org.inksnow.core.impl.util.PosUtil;

import java.util.UUID;

@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public class AuroraData implements DataApi {
    private final AuroraWorldDataService worldDataService;
    private final AuroraPlayerDataService playerDataService;

    @Override
    public AuroraWorldDataHolder of(World world) {
        return worldDataService.get(world).dataHolder();
    }

    @Override
    public AuroraChunkDataHolder of(Chunk chunk) {
        return new AuroraChunkDataHolder(worldDataService, chunk);
    }

    @Override
    public AuroraBlockDataHolder of(Block block) {
        return new AuroraBlockDataHolder(
                worldDataService,
                block.getChunk(),
                PosUtil.location_blockId(block.getLocation())
        );
    }

    @Override
    public EntityDataHolder of(Entity entity) {
        if (entity instanceof Player) {
            return playerDataService.get((Player) entity);
        } else {
            return new AuroraEntityDataHolder(entity);
        }
    }

    @Override
    public AuroraItemDataHolder of(ItemStack itemStack) {
        return new AuroraItemDataHolder(itemStack);
    }

    @Override
    public AuroraPlayerDataHolder of(Player player) {
        return playerDataService.get(player);
    }

    public AuroraUserDataHolder offlinePlayer(UUID id) {
        final @Nullable Player player = Bukkit.getPlayer(id);
        return player == null ? new AuroraUserDataHolder(id) : playerDataService.get(player);
    }
}
