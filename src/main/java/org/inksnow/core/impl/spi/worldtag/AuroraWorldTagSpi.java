package org.inksnow.core.impl.spi.worldtag;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import org.inksnow.core.impl.spi.AbstractSpiRegistry;
import org.inksnow.core.spi.worldtag.WorldTagProvider;
import org.inksnow.core.spi.worldtag.WorldTagSpi;

@Singleton
public class AuroraWorldTagSpi extends AbstractSpiRegistry<WorldTagProvider> implements WorldTagSpi {
    @Inject
    private AuroraWorldTagSpi() {
        super(WorldTagProvider.class);
    }

    @Override
    public String friendlyName() {
        return "世界标签提供者";
    }
}
