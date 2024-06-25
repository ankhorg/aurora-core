package org.inksnow.core.api.spi;

import org.jetbrains.annotations.NotNull;

public interface SpiRegistry<S extends Namespaced> {
  S get(@NotNull String namespace);

  void register(@NotNull S factory);
  void registerForce(@NotNull S factory);
}
