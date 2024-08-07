package org.inksnow.core.spi.item;

import lombok.NonNull;
import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.spi.SpiRegistry;

import java.util.Optional;

/**
 * Represents the service provider interface for items.
 */
public interface ItemSpi extends SpiRegistry<ItemProvider.Factory> {
    /**
     * Creates an item stack with the specified namespace and key.
     *
     * @param path the resource path of the provider
     * @param key the key of the provider
     * @return the item stack with the specified namespace and key, or {@code null} if not found
     */
    default Optional<ItemStack> create(ResourcePath path, String key) {
        return getProvider(path, key).flatMap(ItemProvider::create);
    }

    /**
     * Creates an item stack with the specified namespace and key.
     *
     * @param path the resource path of the provider
     * @param key the key of the provider
     * @return the item stack with the specified namespace and key, or {@code null} if not found
     */
    default Optional<ItemStack> create(String path, String key) {
        return create(ResourcePath.of(path), key);
    }

    /**
     * Gets the provider with the specified namespace and key.
     *
     * @param path the resource path of the provider
     * @param key the key of the provider
     * @return the provider with the specified namespace and key
     */
    Optional<ItemProvider> getProvider(ResourcePath path, String key);

    /**
     * Gets the provider with the specified namespace and key, or throws an exception if not found.
     *
     * @param path the resource path of the provider
     * @param key the key of the provider
     * @return the provider with the specified namespace and key
     * @throws IllegalStateException if the provider is not found
     */
    default @NonNull ItemProvider requireProvider(ResourcePath path, String key) throws IllegalStateException {
        return getProvider(path, key)
            .orElseThrow(() -> new IllegalStateException("Provider not found: " + path + ":" + key));
    }
}
