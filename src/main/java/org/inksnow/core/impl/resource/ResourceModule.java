package org.inksnow.core.impl.resource;

import com.google.inject.AbstractModule;
import org.inksnow.core.resource.ResourcePath;

public class ResourceModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(ResourcePath.Factory.class).to(AuroraResourcePathFactory.class);
        bind(ResourcePath.Builder.class).to(AuroraResourcePathBuilder.class);
    }
}
