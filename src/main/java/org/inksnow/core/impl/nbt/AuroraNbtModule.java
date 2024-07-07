package org.inksnow.core.impl.nbt;

import com.google.inject.AbstractModule;
import org.inksnow.core.nbt.TagFactory;

public final class AuroraNbtModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(TagFactory.class).toInstance(AuroraTagFactory.INSTANCE);
    }
}
