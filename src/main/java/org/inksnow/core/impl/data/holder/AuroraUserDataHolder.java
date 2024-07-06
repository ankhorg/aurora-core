package org.inksnow.core.impl.data.holder;

import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.bukkit.entity.Player;
import org.inksnow.core.data.holder.UserDataHolder;
import org.inksnow.core.impl.provider.nbt.NBTDataType;
import org.inksnow.core.impl.provider.nbt.NBTDataTypes;

import java.nio.file.Paths;
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


    @Override
    public Optional<Player> player() {
        return Optional.empty();
    }

    @Override
    public NBTDataType data$getNBTDataType() {
        return NBTDataTypes.PLAYER;
    }
}