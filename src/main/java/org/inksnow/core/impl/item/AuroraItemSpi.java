package org.inksnow.core.impl.item;

import lombok.Getter;
import lombok.NonNull;
import org.inksnow.core.api.item.ItemProvider;
import org.inksnow.core.api.item.ItemSpi;
import org.inksnow.core.impl.spi.AbstractSpiRegistry;

@Getter
public final class AuroraItemSpi extends AbstractSpiRegistry<ItemProvider.@NonNull Factory> implements ItemSpi {
  public AuroraItemSpi() {
    super(ItemProvider.Factory.class);
  }

  @Override
  public String friendlyName() {
    return "物品提供者";
  }

  @Override
  public ItemProvider getProvider(@NonNull String namespace, @NonNull String key) {
    ItemProvider.Factory factory = get(namespace);
    if (factory == null) {
      return null;
    }
    return factory.get(key);
  }
}
