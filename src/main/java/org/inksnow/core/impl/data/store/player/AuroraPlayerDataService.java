package org.inksnow.core.impl.data.store.player;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.world.WorldSaveEvent;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.impl.data.holder.AuroraPlayerDataHolder;

import java.util.HashMap;

@Slf4j
@Singleton
@RequiredArgsConstructor(access = AccessLevel.PRIVATE, onConstructor_ = @Inject)
public class AuroraPlayerDataService implements Listener {
    private final HashMap<Player, AuroraPlayerDataHolder> playerData = new HashMap<>();


    public AuroraPlayerDataHolder get(Player player) {
        return playerData.computeIfAbsent(player, AuroraPlayerDataHolder::new);
    }

    @EventHandler(priority = EventPriority.LOWEST)
    public void onPlayerJoin(PlayerJoinEvent event) {
        get(event.getPlayer());
    }

    @EventHandler
    public void onPlayerQuit(PlayerQuitEvent event) {
        final @Nullable AuroraPlayerDataHolder previous = playerData.remove(event.getPlayer());
        if (previous != null) {
            previous.close0();
        }
    }

    public void close() {
        for (AuroraPlayerDataHolder holder : playerData.values()) {
            holder.close0();
        }
    }

    public void flush() {
        logger.info("Saving Aurora player data...");
        long start = System.nanoTime();
        for (AuroraPlayerDataHolder holder : playerData.values()) {
            holder.flush();
        }
        logger.info("Aurora player data saved {} in {}ms", playerData.size(), (System.nanoTime() - start) / 1_000_000);
    }
}
