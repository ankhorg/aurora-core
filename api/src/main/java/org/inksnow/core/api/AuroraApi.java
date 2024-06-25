package org.inksnow.core.api;

import lombok.NonNull;
import org.bukkit.plugin.Plugin;
import org.inksnow.core.api.item.ItemSpi;
import org.inksnow.core.api.worldtag.WorldTagSpi;
import org.jetbrains.annotations.NotNull;

public interface AuroraApi {
  @NotNull ItemSpi item();
  @NotNull WorldTagSpi worldTag();
  void scanPlugin(@NonNull Plugin targetPlugin);
}
