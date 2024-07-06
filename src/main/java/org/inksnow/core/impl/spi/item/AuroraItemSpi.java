package org.inksnow.core.impl.spi.item;

import jakarta.inject.Inject;
import jakarta.inject.Singleton;
import lombok.Getter;
import lombok.NonNull;
import org.inksnow.core.impl.spi.AbstractSpiRegistry;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.spi.item.ItemProvider;
import org.inksnow.core.spi.item.ItemSpi;

import java.util.Optional;

@Singleton
@Getter
public final class AuroraItemSpi extends AbstractSpiRegistry<ItemProvider.@NonNull Factory> implements ItemSpi {
    @Inject
    private AuroraItemSpi() {
        super(ItemProvider.Factory.class);
    }

    @Override
    public String friendlyName() {
        return "物品提供者";
    }

    @Override
    public Optional<ItemProvider> getProvider(@NonNull ResourcePath resourcePath, @NonNull String key) {
        return get(resourcePath).flatMap(factory -> factory.get(key));
    }
}
