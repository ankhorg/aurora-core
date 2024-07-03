package org.inksnow.core.spi.item;

/**
 * A provider of items, what can be saved.
 */
public interface SaveableItemProvider extends ItemProvider {
    /**
     * Saves the item.
     */
    void save();
}
