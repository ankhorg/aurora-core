package org.inksnow.core.impl.data;

import com.google.inject.AbstractModule;
import org.inksnow.core.data.DataApi;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.key.AuroraKeyBuilder;
import org.inksnow.core.impl.data.provider.DataProviderRegistry;
import org.inksnow.core.impl.data.store.world.AuroraWorldDataService;
import org.inksnow.core.impl.data.value.AuroraValueFactory;

public class AuroraDataModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataApi.class).to(AuroraData.class);
        bind(DataProviderRegistry.class);
        bind(AuroraWorldDataService.class);
        bind(Key.Builder.class).to(AuroraKeyBuilder.class);
        bind(Value.Factory.class).to(AuroraValueFactory.class);
    }
}
