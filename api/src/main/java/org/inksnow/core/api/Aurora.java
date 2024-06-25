package org.inksnow.core.api;

import org.inksnow.core.api.item.ItemSpi;
import org.inksnow.core.api.worldtag.WorldTagSpi;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class Aurora {
  private static @Nullable AuroraApi api;

  private Aurora() {
    throw new UnsupportedOperationException("This class cannot be instantiated");
  }

  public static void api(@NotNull AuroraApi api) {
    if (api == null) {
      throw new IllegalArgumentException("api cannot be null");
    }
    if (Aurora.api != null) {
      throw new IllegalStateException("Aurora has already been initialized");
    }
    Aurora.api = api;
  }

  public static @NotNull AuroraApi api() {
    AuroraApi api = Aurora.api;
    if (api == null) {
      throw new IllegalStateException("Aurora has not been initialized");
    }
    return api;
  }

  public static @NotNull ItemSpi item() {
    return api().item();
  }

  public static @NotNull WorldTagSpi worldTag() {
    return api().worldTag();
  }
}
