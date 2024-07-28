package org.inksnow.core.impl.data.holder;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.Bukkit;
import org.bukkit.OfflinePlayer;
import org.bukkit.entity.Player;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.holder.UserDataHolder;
import org.inksnow.core.impl.data.provider.nbt.NBTDataType;
import org.inksnow.core.impl.data.provider.nbt.NBTDataTypes;

import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Slf4j
public class AuroraUserDataHolder extends AuroraNbtFileDataHolder implements UserDataHolder, AuroraMutableDataHolder {
    private final UUID playerId;

    @SneakyThrows
    public AuroraUserDataHolder(UUID playerId) {
        super(Paths.get("plugins", "aurora-core", "data", "player", playerId + ".dat"));
        this.playerId = playerId;
    }

    // not necessary, for performance
    @Override
    public List<DataHolder.Mutable> impl$mutableDelegateDataHolder() {
        return Collections.singletonList(this);
    }

    @Override
    public Optional<? extends OfflinePlayer> player() {
        return Optional.ofNullable(Bukkit.getServer().getOfflinePlayer(this.playerId));
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.PLAYER;
    }
}
