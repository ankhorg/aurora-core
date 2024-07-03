package org.inksnow.core.spi.item;

import org.bukkit.inventory.ItemStack;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.resource.WithResourcePath;

/**
 * Provides an item stack.
 */
public interface ItemProvider {
    /**
     * Creates the item stack.
     *
     * @return the item stack
     */
    @Nullable ItemStack create();

    /**
     * The factory for item providers.
     */
    interface Factory extends WithResourcePath {
        /**
         * Returns the item provider by name.
         *
         * @param name the name
         * @return the item provider, or null if not found
         */
        @Nullable ItemProvider get(String name);
    }
}