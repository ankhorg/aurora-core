package org.inksnow.core.impl.spi.worldtag;

import lombok.NonNull;
import org.bukkit.World;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.spi.worldtag.FetchableWorldTagProvider;

import java.util.Collections;
import java.util.List;

public class WorldIdWorldTagProvider implements FetchableWorldTagProvider {
    private static final ResourcePath PATH = ResourcePath.of("aurora:minecraft:uuid");

    @Override
    public @NonNull List<@NonNull String> fetchTags(@NonNull World world) {
        return Collections.singletonList(world.getUID().toString());
    }

    @Override
    public boolean hasTag(@NonNull World world, @NonNull String tag) {
        return tag.equalsIgnoreCase(world.getUID().toString());
    }

    @Override
    public @NonNull ResourcePath resourcePath() {
        return PATH;
    }
}
