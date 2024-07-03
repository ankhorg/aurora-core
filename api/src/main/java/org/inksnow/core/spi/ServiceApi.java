package org.inksnow.core.spi;

import org.bukkit.plugin.Plugin;
import org.inksnow.core.spi.item.ItemSpi;
import org.inksnow.core.spi.worldtag.WorldTagSpi;

import java.util.ServiceLoader;

/**
 * Represents the service API.
 */
public interface ServiceApi {
    /**
     * Scans the specified plugin.
     * <p>
     * This method will scan the plugin and register all services provided by the plugin.
     * Services should be registered as {@link ServiceLoader} services.
     * <p>
     * However, Aurora will not use {@link ServiceLoader} to load services, but will use its own service registry.
     * So you should not use {@link ServiceLoader} to load services. Instead, you should use the service registry
     * provided by Aurora.
     * <p>
     *
     * @param targetPlugin the plugin to scan
     */
    void scanPlugin(Plugin targetPlugin);

    /**
     * Gets the item service provider.
     *
     * @return the item service provider
     */
    ItemSpi item();

    /**
     * Gets the world tag service provider.
     *
     * @return the world tag service provider
     */
    WorldTagSpi worldTag();
}
