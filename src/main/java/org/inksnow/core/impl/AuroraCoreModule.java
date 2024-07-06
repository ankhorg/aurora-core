package org.inksnow.core.impl;

import com.google.inject.AbstractModule;
import org.inksnow.core.impl.data.AuroraDataModule;
import org.inksnow.core.impl.resource.ResourceModule;
import org.inksnow.core.spi.ServiceApi;

public class AuroraCoreModule extends AbstractModule {
    @Override
    protected void configure() {
        install(new ResourceModule());
        install(new AuroraDataModule());

        bind(ServiceApi.class).to(AuroraService.class);
    }
}
