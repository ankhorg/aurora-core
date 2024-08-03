package org.inksnow.core.impl.data;

import com.google.inject.AbstractModule;
import org.inksnow.core.data.DataApi;
import org.inksnow.core.data.DataManager;
import org.inksnow.core.data.DataManipulator;
import org.inksnow.core.data.DataRegistration;
import org.inksnow.core.data.key.Key;
import org.inksnow.core.data.persistence.DataStore;
import org.inksnow.core.data.value.Value;
import org.inksnow.core.impl.data.key.AuroraKeyBuilder;
import org.inksnow.core.impl.data.manipulator.ImmutableDataManipulatorFactory;
import org.inksnow.core.impl.data.manipulator.MutableDataManipulatorFactory;
import org.inksnow.core.impl.data.persistence.datastore.AuroraDataStoreBuilder;
import org.inksnow.core.impl.data.provider.DataProviderRegistry;
import org.inksnow.core.impl.data.store.AuroraDataStoreRegistry;
import org.inksnow.core.impl.data.store.world.AuroraWorldDataService;
import org.inksnow.core.impl.data.value.AuroraValueFactory;

public class AuroraDataModule extends AbstractModule {
    @Override
    protected void configure() {
        bind(DataApi.class).to(AuroraData.class);

        bind(AuroraDataStoreRegistry.class);
        bind(DataManager.class).to(AuroraDataManager.class);

        // key
        bind(Key.Builder.class).to(AuroraKeyBuilder.class);

        // value
        bind(Value.Factory.class).to(AuroraValueFactory.class);

        // provider
        bind(DataProviderRegistry.class);
        bind(AuroraWorldDataService.class);

        // manipulator
        bind(DataManipulator.Immutable.Factory.class).to(ImmutableDataManipulatorFactory.class);
        bind(DataManipulator.Mutable.Factory.class).to(MutableDataManipulatorFactory.class);

        bind(DataRegistration.Builder.class).to(AuroraDataRegistrationBuilder.class);
        bind(DataStore.Builder.class).to(AuroraDataStoreBuilder.class);
    }
}
