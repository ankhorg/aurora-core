package org.inksnow.core.api.worldtag;

import org.bukkit.World;
import org.inksnow.core.api.spi.Namespaced;
import org.jetbrains.annotations.NotNull;

public interface WorldTagProvider extends Namespaced {
  boolean hasTag(@NotNull World world, @NotNull String tag);
}
