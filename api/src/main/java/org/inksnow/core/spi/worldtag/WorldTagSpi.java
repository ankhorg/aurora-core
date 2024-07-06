package org.inksnow.core.spi.worldtag;

import org.bukkit.World;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.spi.SpiRegistry;

/**
 * Represents the service provider interface for world tags.
 */
public interface WorldTagSpi extends SpiRegistry<WorldTagProvider> {
    /**
     * Returns if the world has the tag.
     *
     * @param world the world
     * @param path the path of the provider
     * @param tag the tag
     * @return if the world has the tag
     */
    default boolean hasTag(World world, ResourcePath path, String tag) {
        return get(path)
            .map(provider -> provider.hasTag(world, tag))
            .orElse(false);
    }
}
