package org.inksnow.core.data.holder;

import org.bukkit.OfflinePlayer;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.persistence.DataContainerHolder;

import java.io.Closeable;
import java.util.Optional;

public interface UserDataHolder extends DataHolder.Mutable, DataContainerHolder.Mutable, Closeable, AutoCloseable {
    Optional<? extends OfflinePlayer> player();

    @Override
    void close();
}
