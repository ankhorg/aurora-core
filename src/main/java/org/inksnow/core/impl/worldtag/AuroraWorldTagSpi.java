package org.inksnow.core.impl.worldtag;

import org.inksnow.core.api.worldtag.WorldTagProvider;
import org.inksnow.core.api.worldtag.WorldTagSpi;
import org.inksnow.core.impl.spi.AbstractSpiRegistry;

public class AuroraWorldTagSpi extends AbstractSpiRegistry<WorldTagProvider> implements WorldTagSpi {
  public AuroraWorldTagSpi() {
    super(WorldTagProvider.class);
  }

  @Override
  public String friendlyName() {
    return "世界标签提供者";
  }
}
