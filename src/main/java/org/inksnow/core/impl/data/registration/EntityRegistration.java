package org.inksnow.core.impl.data.registration;

import org.bukkit.entity.Damageable;
import org.inksnow.core.data.holder.EntityDataHolder;
import org.inksnow.core.impl.Keys;
import org.inksnow.core.impl.data.provider.DataProviderRegistrator;

public class EntityRegistration {
    public void register(DataProviderRegistrator registrator) {
        registrator.asMutable(EntityDataHolder.class)
                .create(Keys.HEALTH)
                .get(h -> h.entity() instanceof Damageable ? ((Damageable) h.entity()).getHealth() : null)
                .set((h, v) -> {
                    if (h.entity() instanceof Damageable) {
                        ((Damageable) h.entity()).setHealth(v);
                    }
                });

        registrator.asMutable(EntityDataHolder.class)
                .create(Keys.CUSTOM_NAME)
                .get(h -> h.entity().getName())
                .set((h, v) -> h.entity().setCustomName(v));

        registrator.asImmutable(EntityDataHolder.class)
                .create(Keys.NAME)
                .get(h -> h.entity().getName());
    }
}
