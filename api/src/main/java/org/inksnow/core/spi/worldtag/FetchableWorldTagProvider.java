package org.inksnow.core.spi.worldtag;

import org.bukkit.World;

import java.util.List;

/**
 * Provides the tags of a world.
 */
public interface FetchableWorldTagProvider extends WorldTagProvider {
    List<String> fetchTags(World world);
}
