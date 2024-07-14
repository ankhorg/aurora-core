package org.inksnow.core.impl.data.holder;

import com.google.common.collect.ImmutableList;
import lombok.SneakyThrows;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.holder.EntityDataHolder;

import java.lang.ref.WeakReference;
import java.util.Collections;
import java.util.List;
import java.util.Optional;

public class AuroraPlayerDataHolder extends AuroraUserDataHolder implements EntityDataHolder {
    private final WeakReference<Player> playerReference;
    private final AuroraEntityDataHolder entityDataHolder;

    @SneakyThrows
    public AuroraPlayerDataHolder(Player player) {
        super(player.getUniqueId());
        this.playerReference = new WeakReference<>(player);
        this.entityDataHolder = new AuroraEntityDataHolder(player);
    }

    @Override
    public List<DataHolder> impl$delegateDataHolder() {
        return ImmutableList.of(this, entityDataHolder);
    }

    @Override
    public Optional<Player> player() {
        return Optional.ofNullable(playerReference.get());
    }

    @Override
    public Player entity() {
        @Nullable Player player = playerReference.get();
        if (player == null) {
            throw new IllegalStateException("Player have been collected by GC, don't store DataHolder!");
        }
        return player;
    }

    @Override
    public void close() {
        // if player still online, it will save by AuroraPlayerDataService
        final @Nullable Player player = playerReference.get();
        if (player == null || !player.isOnline()) {
            super.close();
        }
    }

    public void close0() {
        super.close();
    }

    @Override
    @SuppressWarnings("removal")
    protected void finalize() throws Throwable {
        try {
            super.close();
        } finally {
            super.finalize();
        }
    }
}
