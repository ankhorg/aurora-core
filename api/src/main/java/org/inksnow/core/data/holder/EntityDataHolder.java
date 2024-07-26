package org.inksnow.core.data.holder;

import org.bukkit.entity.Entity;
import org.inksnow.core.data.DataHolder;
import org.inksnow.core.data.persistence.DataContainerHolder;

public interface EntityDataHolder extends DataHolder.Mutable, DataContainerHolder.Mutable {
    Entity entity();
}
