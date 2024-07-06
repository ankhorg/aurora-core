package org.inksnow.core.data.holder;

import org.bukkit.entity.Player;
import org.inksnow.core.data.DataHolder;

import java.io.Closeable;
import java.io.IOException;
import java.util.Optional;

public interface UserDataHolder extends DataHolder, Closeable, AutoCloseable {
    Optional<Player> player();

    @Override
    void close();
}
