package org.inksnow.core.api.item;

import org.bukkit.inventory.ItemStack;
import org.inksnow.core.api.spi.SpiRegistry;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public interface ItemSpi extends SpiRegistry<ItemProvider.@NotNull Factory> {
  default @Nullable ItemStack create(@NotNull String namespace, @NotNull String key) {
    return getProvider(namespace, key).create();
  }

  @Nullable ItemProvider getProvider(@NotNull String namespace, @NotNull String key);
}
