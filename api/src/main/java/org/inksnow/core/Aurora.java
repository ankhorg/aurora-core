package org.inksnow.core;

import com.google.common.base.Preconditions;
import org.checkerframework.checker.nullness.qual.MonotonicNonNull;
import org.checkerframework.checker.nullness.qual.Nullable;
import org.inksnow.core.spi.ServiceApi;
import org.inksnow.core.util.Builder;

/**
 * The main static entry point of the Aurora API.
 */
public final class Aurora {
    /**
     * The API instance.
     */
    private static @MonotonicNonNull AuroraApi api;

    private Aurora() {
        throw new UnsupportedOperationException("This class cannot be instantiated");
    }

    /**
     * Initializes the Aurora API.
     *
     * @param api the API instance.
     * @throws IllegalStateException if the API has already been initialized.
     */
    public static void api(AuroraApi api) throws IllegalStateException {
        Preconditions.checkNotNull(api, "api");
        Preconditions.checkState(Aurora.api == null, "Aurora has already been initialized");

        Aurora.api = api;
    }

    /**
     * Gets the Aurora API.
     *
     * @return the Aurora API.
     * @throws IllegalStateException if the API has not been initialized.
     */
    public static AuroraApi api() {
        final @Nullable AuroraApi api = Aurora.api;
        Preconditions.checkState(api != null, "Aurora has not been initialized");
        return api;
    }

    /**
     * Returns the service API.
     *
     * @return the service API
     */
    public static ServiceApi service() {
        return api().service();
    }

    /**
     * Creates a builder.
     *
     * @param clazz the builder class
     * @param <B> the type of the builder
     * @return the builder
     */
    public static <B extends Builder<?, ?>> B createBuilder(Class<B> clazz) {
        return api().createBuilder(clazz);
    }

    /**
     * Gets the factory with the specified class.
     *
     * @param clazz the factory class
     * @param <T> the type of the factory
     * @return the factory
     */
    public static <T> T getFactory(Class<T> clazz) {
        return api().getFactory(clazz);
    }

}
