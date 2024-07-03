package org.inksnow.core.spi;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.resource.WithResourcePath;

/**
 * Represents a registry for SPIs.
 *
 * @param <S> the type of the SPI
 */
public interface SpiRegistry<S extends WithResourcePath> {
    /**
     * Gets the SPI with the specified namespace.
     *
     * @param resourcePath the namespace of the SPI
     * @return the SPI with the specified namespace, or {@code null} if not found
     */
    @Nullable S get(ResourcePath resourcePath);

    /**
     * Gets the SPI with the specified namespace, or throws an exception if not found.
     *
     * @param resourcePath the namespace of the SPI
     * @return the SPI with the specified namespace
     * @throws IllegalStateException if the SPI is not found
     */
    default S require(ResourcePath resourcePath) throws IllegalStateException {
        final @Nullable S service = get(resourcePath);
        if (service == null) {
            throw new IllegalStateException("Service not found: " + resourcePath);
        }
        return service;
    }

    /**
     * Registers the specified SPI.
     *
     * @param factory the SPI to register
     */
    void register(S factory);
}
