package org.inksnow.core;

import org.inksnow.core.data.DataApi;
import org.inksnow.core.spi.ServiceApi;
import org.inksnow.core.util.Builder;

/**
 * The main entry point of the Aurora API.
 */
public interface AuroraApi {
    /**
     * Returns the service API.
     *
     * @return the service API
     */
    ServiceApi service();

    DataApi data();

    /**
     * Creates a builder.
     *
     * @param clazz the builder class
     * @param <B> the type of the builder
     * @return the builder
     */
    <B extends Builder<?, ?>> B createBuilder(Class<B> clazz);

    /**
     * Gets the factory with the specified class.
     *
     * @param clazz the factory class
     * @param <T> the type of the factory
     * @return the factory
     */
    <T> T getFactory(Class<T> clazz);
}
