package org.inksnow.core.api.worldtag;

import org.bukkit.World;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public interface FetchableWorldTagProvider extends WorldTagProvider {
  @NotNull List<@NotNull String> fetchTags(@NotNull World world);
}
