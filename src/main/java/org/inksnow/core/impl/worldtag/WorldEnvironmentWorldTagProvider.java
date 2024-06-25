package org.inksnow.core.impl.worldtag;

import lombok.NonNull;
import org.bukkit.World;
import org.inksnow.core.api.worldtag.FetchableWorldTagProvider;

import java.util.Collections;
import java.util.List;

public class WorldEnvironmentWorldTagProvider implements FetchableWorldTagProvider {
  @Override
  public @NonNull List<@NonNull String> fetchTags(@NonNull World world) {
    return Collections.singletonList(world.getEnvironment().name());
  }

  @Override
  public boolean hasTag(@NonNull World world, @NonNull String tag) {
    return tag.equalsIgnoreCase(world.getEnvironment().name());
  }

  @Override
  public @NonNull String namespace() {
    return "aurora:minecraft:environment";
  }
}
