package org.inksnow.core.api.worldtag;

import org.bukkit.World;
import org.inksnow.core.api.spi.SpiRegistry;

public interface WorldTagSpi extends SpiRegistry<WorldTagProvider> {
  default boolean hasTag(World world, String namespace, String tag) {
    WorldTagProvider provider = get(namespace);
    if (provider == null) {
      return false;
    }
    return provider.hasTag(world, tag);
  }
}
