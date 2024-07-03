package org.inksnow.core.spi.worldtag;

import org.bukkit.World;
import org.inksnow.core.resource.WithResourcePath;

/**
 * Provides whether a world has a tag.
 */
public interface WorldTagProvider extends WithResourcePath {
    /**
     * Returns if the world has the tag.
     *
     * @param world the world
     * @param tag the tag
     * @return if the world has the tag
     */
    boolean hasTag(World world, String tag);
}
