package org.inksnow.core.data.holder;

import org.bukkit.entity.Entity;
import org.inksnow.core.data.DataHolder;

import java.util.Optional;

public interface EntityDataHolder extends DataHolder {
    Entity entity();
}
