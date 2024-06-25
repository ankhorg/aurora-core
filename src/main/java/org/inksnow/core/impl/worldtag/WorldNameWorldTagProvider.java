package org.inksnow.core.impl.worldtag;

import lombok.NonNull;
import org.bukkit.World;
import org.inksnow.core.api.worldtag.FetchableWorldTagProvider;

import java.util.Collections;
import java.util.List;

public class WorldNameWorldTagProvider implements FetchableWorldTagProvider {
  @Override
  public @NonNull List<@NonNull String> fetchTags(@NonNull World world) {
    return Collections.singletonList(world.getName());
  }

  @Override
  public boolean hasTag(@NonNull World world, @NonNull String tag) {
    return tag.equals(world.getName());
  }

  @Override
  public @NonNull String namespace() {
    return "aurora:minecraft:name";
  }
}
