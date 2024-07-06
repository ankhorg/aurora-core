package org.inksnow.core.spi;

import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.resource.ResourcePath;
import org.inksnow.core.resource.WithResourcePath;

import java.util.List;
import java.util.Optional;

/**
 * Represents a registry for SPIs.
 *
 * @param <S> the type of the SPI
 */
public interface SpiRegistry<S extends WithResourcePath> {
    /**
     * Gets the SPI with the specified namespace.
     * <p>
     * For example, if the SPI has the resourcePath {@code "aurora:minecraft:name"}, you can get it by calling
     * {@code get(ResourcePath.of("aurora:minecraft:name"))}, {@code get(ResourcePath.of("minecraft:name"))} or
     * {@code get(ResourcePath.of("name"))}.
     *
     * @param resourcePath the namespace of the SPI
     * @return the SPI with the specified namespace
     */
    Optional<S> get(ResourcePath resourcePath);

    /**
     * Gets all the services.
     *
     * @return immutable copy of all the services.
     */
    List<S> getAll();

    /**
     * Gets the SPI with the specified namespace, or throws an exception if not found.
     *
     * @param resourcePath the namespace of the SPI
     * @return the SPI with the specified namespace
     * @throws IllegalStateException if the SPI is not found
     */
    default S require(ResourcePath resourcePath) throws IllegalStateException {
        return get(resourcePath)
            .orElseThrow(() -> new IllegalStateException("Service not found: " + resourcePath));
    }

    /**
     * Registers the specified SPI.
     *
     * @param factory the SPI to register
     */
    void register(S factory);
}
