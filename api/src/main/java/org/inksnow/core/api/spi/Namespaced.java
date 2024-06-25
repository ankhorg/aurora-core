package org.inksnow.core.api.spi;

import org.jetbrains.annotations.NotNull;

public interface Namespaced {
  @NotNull String namespace();
}
