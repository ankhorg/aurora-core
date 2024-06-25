package org.inksnow.core.api.item;

import org.bukkit.inventory.ItemStack;
import org.inksnow.core.api.spi.Namespaced;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemProvider {
  @Nullable
  ItemStack create();

  interface Factory extends Namespaced {
    @Nullable ItemProvider get(@NotNull String name);
  }
}